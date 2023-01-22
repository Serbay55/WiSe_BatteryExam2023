package com.example.wise_batteryexam2023

import android.content.SharedPreferences
import android.os.BatteryManager
import android.os.Bundle
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Dao
import androidx.room.Room
import com.example.wise_batteryexam2023.data.*
import com.example.wise_batteryexam2023.ui.main.SectionsPagerAdapter
import com.example.wise_batteryexam2023.databinding.ActivityMainBinding
import com.example.wise_batteryexam2023.methods.InstallTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.awt.font.NumericShaper.Range
import java.lang.Math.abs
import kotlin.concurrent.schedule
import java.util.*
import kotlin.system.measureTimeMillis

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var cDao: CDAO
    private lateinit var scDao: CDAO
    private lateinit var hcDao: CDAO
    private lateinit var hDao: CDAO
    private lateinit var sotDao: CDAO
    private lateinit var lraDao: CDAO
    private lateinit var bstate : BatteryState
    private lateinit var sh : SharedPreferences
    private lateinit var shedit: SharedPreferences.Editor
    private var newcycle: Double = 0.0
    var intervals = arrayOf(
        intArrayOf(10, 0),
        intArrayOf(20, 1),
        intArrayOf(30, 2),
        intArrayOf(40, 3),
        intArrayOf(50, 4),
        intArrayOf(60, 5),
        intArrayOf(70, 6),
        intArrayOf(80, 7),
        intArrayOf(90, 8),
        intArrayOf(95, 9),
        intArrayOf(100, 10)
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = Room.databaseBuilder(
            applicationContext,
            DB::class.java, "charge_stats"
        ).build()
        val sdb = Room.databaseBuilder(
            applicationContext,
            DB::class.java, "lastChargeStats"
        ).build()
        val hdb = Room.databaseBuilder(
            applicationContext,
            DB::class.java, "battery_health"
        ).build()
        val hdbs = Room.databaseBuilder(
            applicationContext,
            DB::class.java, "current_health"
        ).build()
        val sotdb = Room.databaseBuilder(
            applicationContext,
            DB::class.java, "screen_on_time"
        )
        val lradb = Room.databaseBuilder(
            applicationContext,
            DB::class.java, "last_running_app"
        )


        hDao = hdbs.cDAO()
        cDao = db.cDAO()
        scDao = sdb.cDAO()
        hcDao = hdb.cDAO()
        testDB()


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabs
        tabs.setupWithViewPager(viewPager)
        val fab: FloatingActionButton = binding.fab

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        batteryStatechecker()
        actionHealth()
        Log.i("TEST::: ", ""+ InstallTime().getInstallTime(this))
        Log.i("TEST::: ", ""+ RunningApps().getOldestAppsAge(this))
    }

    /*private fun fakeApiRequest() {
        CoroutineScope(Dispatchers.IO).launch {
            val executionTime = measureTimeMillis {
                val result1 = async {
                    Log.i("ASYNC LOGG","HELLO FROM THE OTHER SIDE")
                    fakeApiRequest()
                }
            }
            Log.i("ASYNC TIME", "TIME ELAPSED MOTHERFUCKER: $executionTime ms.")
        }
    }*/
    private fun checkForegroundapp(){
        val s: String = RunningApps().getCurrentForegroundRunningApp(this)
        Log.i("FOREGROUNDAPP:: ", ""+s)
    }

    private fun testDB(){
        lifecycleScope.launch(Dispatchers.IO){
            //Insert
            Log.i("MyTAG","***** Inserting 3 stats ********")
            cDao.insertCharge(Charge(0,1.0, Calendar.DAY_OF_YEAR, Calendar.YEAR))
            scDao.insertLCS(LCS(0,getBattery()))
            Log.i("MyTAG","***** Inserted 3 stats ********")

            //Query
            val stats = cDao.getAllCharges()
            for(x in stats){
                Log.i("MyTAG","id: ${x.id} charges: ${x.chargeStep} ")
                Log.i("LCS:__:", ""+scDao.getLastCharge())
            }
        }
    }

    private fun setlastchargestate(){
        lifecycleScope.launch(Dispatchers.IO){
            val s = scDao.getLastCharge()
            if(s == 0) {
                //To Do deleter of selected table row
                scDao.insertLCS(LCS(0, getBattery()))
            }
        }
    }

    private fun checklastchargestate(){
        lifecycleScope.launch(Dispatchers.IO){
            val lastrecordedcharge = scDao.getLastCharge()
            val currentChargeValue = getBattery()
            if(lastrecordedcharge != null){
                    checkDifference(lastrecordedcharge, currentChargeValue)
                    val new_cs = cDao.getTodaysChargeCycles(getCurrentDay(),getCurrentYear()) + (newcycle * tCheck())
                    cDao.updateCharge(Charge(cDao.getTodaysChargeStatid(getCurrentDay(),getCurrentYear()),new_cs,getCurrentDay(),getCurrentYear()))
                    newcycle = 0.0
            } else {
                Log.e("ERROR: ", "No last charge ever recorded. Current charge level will be new last charge value")
                setlastchargestate()
            }
        }
    }

    private fun checkDifference(givenCharge: Int, currentCharge: Int){

        val calc = kotlin.math.abs(givenCharge - currentCharge)
        for (i in intervals.indices) {
            if (calc <= intervals[i][0]) {
                newcycle = (intervals[i][1] * 0.1)
            }
        }
    }


    private fun batteryStatechecker(){
        CoroutineScope(Dispatchers.IO).launch {
            val getBat = async {
                Timer().schedule(10000) {
                    getBattery()
                    checklastchargestate()
                    setlastchargestate()
                    checkForegroundapp()
                    batteryStatechecker()
                }
            }
        }
    }

    private fun getBattery() : Int{
        bstate = BatteryState()
        Log.i("MyTAG: ",""+bstate.getBatteryPercentage(this))
        return bstate.getBatteryPercentage(this)
    }

    private fun getCurrentDay(): Int {
        val cal: Calendar = Calendar.getInstance()
        return cal.get(Calendar.DAY_OF_YEAR)
    }
    private fun getCurrentYear(): Int{
        val cal: Calendar = Calendar.getInstance()
        return cal.get(Calendar.YEAR)
    }

    private fun tCheck(): Double{
        val temperature = BatteryState().batteryTemperature(this)
        var tempMult = 0.0
        when {
            temperature in 20.0..25.0 -> {
                tempMult = 1.0
            }
            temperature in 25.1..30.0 -> {
                tempMult = 1.1
            }
            temperature in 30.1..35.0 -> {
                tempMult = 1.2
            }
            temperature > 35.0 -> {
                tempMult = 1.25
            }
            temperature in 15.0..19.9 -> {
                tempMult = 1.1
            }
            temperature < 15.0 -> {
                tempMult = 1.2
            }
        }
        return tempMult


    }

    private fun actionHealth(){
        CoroutineScope(Dispatchers.IO).launch {
            val gBHS = async {
                Timer().schedule(60000*60*6){
                    batteryhealthcalucation()
                }
            }
        }
    }

    private fun batteryhealthcalucation(){
        lifecycleScope.launch(Dispatchers.IO){
            val cycles = cDao.getCycles(getCurrentDay(),getCurrentYear())
            hcDao.insertHealth(BH(getCurrentDay(),getCurrentYear(),
                kotlin.math.abs(hDao.getCurrentHealth() - (cycles * 0.025))
            ))
        }
    }
}
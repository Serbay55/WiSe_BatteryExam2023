package com.example.wise_batteryexam2023

import android.os.Bundle
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.wise_batteryexam2023.data.*
import com.example.wise_batteryexam2023.ui.main.SectionsPagerAdapter
import com.example.wise_batteryexam2023.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.Math.abs
import kotlin.concurrent.schedule
import java.util.*
import kotlin.system.measureTimeMillis

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var cDao: CDAO
    private lateinit var scDao: CDAO
    private lateinit var bstate : BatteryState

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


        cDao = db.cDAO()
        scDao = sdb.cDAO()
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
    }

    private fun fakeApiRequest() {
        CoroutineScope(Dispatchers.IO).launch {
            val executionTime = measureTimeMillis {
                val result1 = async {
                    Log.i("ASYNC LOGG","HELLO FROM THE OTHER SIDE")
                    fakeApiRequest()
                }
            }
            Log.i("ASYNC TIME", "TIME ELAPSED MOTHERFUCKER: $executionTime ms.")
        }
    }

    private fun testDB(){
        lifecycleScope.launch(Dispatchers.IO){
            //Insert
            Log.i("MyTAG","***** Inserting 3 stats ********")
            cDao.insertCharge(Charge(0,1, Calendar.DAY_OF_YEAR, Calendar.YEAR))
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

    fun setlastchargestate(){
        lifecycleScope.launch(Dispatchers.IO){
            val s = scDao.getLastCharge()
            if(s != 0) {
                //To Do deleter of selected table row
                scDao.insertLCS(LCS(0, getBattery()))
            }
        }
    }

    fun checklastchargestate(){
        lifecycleScope.launch(Dispatchers.IO){
            val lastrecordedcharge = scDao.getLastCharge()
            val currentChargeValue = getBattery()
            if(lastrecordedcharge != null){
                if(checkDifference(lastrecordedcharge, currentChargeValue)){
                    val new_cs = scDao.getLastCharge() + 1
                    cDao.updateCharge(Charge(cDao.getTodaysChargeStatid(getCurrentDay()),new_cs,getCurrentDay(),getCurrentYear()))
                } else {
                    Log.e("MISTAKE: ","Difference between last charge and current charge is too low.")
                }
            } else {
                Log.e("ERROR: ", "No last charge ever recorded. Current charge level will be new last charge value")
                setlastchargestate()
            }
        }
    }

    fun checkDifference(givenCharge: Int, currentCharge: Int): Boolean {
        if(abs(givenCharge - currentCharge) >= 10 && abs(givenCharge - currentCharge) < 20){
            return true
        }
        return false
    }

    fun batteryStatechecker(){
        CoroutineScope(Dispatchers.IO).launch {
            val getBat = async {
                Timer().schedule(10000) {
                    val today : Date
                    val cal: Calendar = Calendar.getInstance()
                    val currentday = cal.get(Calendar.DAY_OF_YEAR)
                    Log.i("TEST: ",""+currentday)
                    getBattery()
                    checklastchargestate()
                    batteryStatechecker()
                    //setlastchargestate()
                }
            }
        }
    }

    fun getBattery() : Int{
        bstate = BatteryState()
        Log.i("MyTAG: ",""+bstate.getBatteryPercentage(this))
        return bstate.getBatteryPercentage(this)
    }

    fun getCurrentDay(): Int {
        val cal: Calendar = Calendar.getInstance()
        return cal.get(Calendar.DAY_OF_YEAR)
    }
    fun getCurrentYear(): Int{
        val cal: Calendar = Calendar.getInstance()
        return cal.get(Calendar.YEAR)
    }
}
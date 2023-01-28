package com.example.wise_batteryexam2023

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.BatteryManager
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
import com.example.wise_batteryexam2023.methods.InstallTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.concurrent.schedule
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var cDao: CDAO
    private lateinit var scDao: CDAO
    private lateinit var hcDao: CDAO
    private lateinit var hDao: CDAO
    private lateinit var sotDao: CDAO
    private lateinit var lraDao: CDAO
    private lateinit var fcsDao: CDAO
    private lateinit var bstate : BatteryState
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

        //Databasebuilder for building tables if not exists.
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
        ).build()
        val lradb = Room.databaseBuilder(
            applicationContext,
            DB::class.java, "last_running_app"
        ).build()
        val fcsdb = Room.databaseBuilder(
            applicationContext,
            DB::class.java, "final_charge_stats"
        ).build()
        //Dataaccessobject servicer
        hDao = hdbs.cDAO()
        cDao = db.cDAO()
        scDao = sdb.cDAO()
        hcDao = hdb.cDAO()
        lraDao = lradb.cDAO()
        sotDao = sotdb.cDAO()
        fcsDao = fcsdb.cDAO()

        testDB()
        if(BatteryState().isBatterybeingcharged(this)){
            Log.e("Battery: ", "is being charged")
        } else {
            Log.e("Battery: ","is not being charged")
        }
        sotcaller(this)
        Log.i("nono: ",""+getBattery())


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
        //Log.i("First installation date:::  ", ""+ packageinformation())
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
            //scDao.insertLCS(LCS(0,getBattery()))
            Log.i("MyTAG","***** Inserted 3 stats ********")

            //Query
            val stats = cDao.getAllCharges()
            for(x in stats){
                Log.i("MyTAG","id: ${x.id} charges: ${x.chargeStep} ")
                Log.i("LCS:__:", ""+scDao.getLastCharge())
            }
            val stats2 = scDao.getAllLastCharges()
            for(y in stats2){
                Log.i("finally::  ","sid: ${y.sid} lcs: ${y.lastChargeStatus}")
            }
            val stat4 = sotDao.getAllSOT()
            for(z in stat4){
                Log.i("SOT:::  ","Time: ${z.time}")
            }
        }
    }


    private fun setlastchargestate(){
        lifecycleScope.launch(Dispatchers.IO){
            val s = scDao.checkExistingLC(1)
            if(s == 0){
                scDao.insertLCS(LCS(0, getBattery()))
            } else {
                scDao.updateLCS(LCS(1,getBattery()))
            }
            //To Do deleter of selected table row

        }
    }

    private fun checklastchargestate(){
        lifecycleScope.launch(Dispatchers.IO){
            val lastrecordedcharge : Int
            if(scDao.checkExistingLC(1) == 0) lastrecordedcharge = 0 else lastrecordedcharge = scDao.getLastCharge()
            val currentChargeValue = getBattery()
            checkDifference(lastrecordedcharge, currentChargeValue)
            if(newcycle >= 0.1) {
                val newcs = (newcycle * tCheck())

                if(fcsDao.checkExistingFCS(1)== 0){fcsDao.insertFCS(FCS(0,newcs))} else {fcsDao.updateFCS(FCS(1,fcsDao.getFCS(1)+newcs))}

                cDao.updateCharge(
                    Charge(
                        cDao.getTodaysChargeStatid(
                            getCurrentDay(),
                            getCurrentYear()
                        ),
                        newcs,
                        getCurrentDay(),
                        getCurrentYear()
                    )
                )
                newcycle = 0.0
                setlastchargestate()
                Log.i("Error:: ","Discharge officially registered")
                //TO DO:: newcycle never reaches below 0.1 cycles and therefore it always updates last recorded charge -> shouldn't happen
            } else {
                Log.i("Error::  ", "Discharge difference is too low")
            }
        }
    }

    private fun calclifeexpectancy(): Int{
        val life = 800.0
        val discrepancy: Double = fcsDao.getFCS(1)
        return (life-discrepancy).toInt()
    }

    private fun checkDifference(givenCharge: Int, currentCharge: Int){

        val calc = kotlin.math.abs(givenCharge - currentCharge)
        for (i in intervals.indices) {
            if (calc >= intervals[0][0] && calc <= intervals[i][0]) {
                newcycle = (intervals[i][1] * 0.1)
            } else {
                newcycle = 0.0
            }
        }
    }


    private fun batteryStatechecker(){
        CoroutineScope(Dispatchers.IO).launch {
            val getBat = async {
                Timer().schedule(10000) {
                    checklastchargestate()
                    checkForegroundapp()
                    batteryStatechecker()
                }
            }
        }
    }

    private fun getBattery() : Int{
        bstate = BatteryState()
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
            temperature in 20.0..25.0 -> { tempMult = 1.0 }
            temperature in 25.1..30.0 -> { tempMult = 1.1 }
            temperature in 30.1..35.0 -> { tempMult = 1.2 }
            temperature > 35.0 -> { tempMult = 1.25 }
            temperature in 15.0..19.9 -> { tempMult = 1.1 }
            temperature < 15.0 -> { tempMult = 1.2 }
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
        var cycle = 0.0
        lifecycleScope.launch(Dispatchers.IO){

            if(fcsDao.checkExistingFCS(1)==1) {
                cycle = fcsDao.getFCS(1)
            }
            if(hcDao.checkexistinghealth() == 1){
                hcDao.updateHealth(LH(1, hcDao.getCurrentHealth() - (cycle * 0.025)))
            } else {
                hcDao.insertHealth(LH(0,100.0 - (cycle * 0.025)))
            }
        }
    }



    private fun sotcaller(context: Context){
            lifecycleScope.launch(Dispatchers.IO){
                val async = async {
                    Timer().schedule(180000){
                        CoroutineScope(Dispatchers.IO).launch {
                            if (ScreenActivity().checkScreenActivity(context)) {
                                Log.e("Screen State::  ", "Screen is on!")
                                if (sotDao.checkExistingSOT(
                                        getCurrentDay(),
                                        getCurrentYear()
                                    ) == 0
                                ) {
                                    sotDao.insertScreenOnTime(
                                        ScreenOTime(
                                            0,
                                            getCurrentDay(),
                                            getCurrentYear(),
                                            3
                                        )
                                    )
                                } else {
                                    sotDao.updateSOT(
                                        ScreenOTime(
                                            sotDao.getIDSOT(getCurrentDay(), getCurrentYear()),
                                            getCurrentDay(),
                                            getCurrentYear(),
                                            sotDao.gettimefromtoday(
                                                getCurrentYear(),
                                                getCurrentDay()
                                            ) + 3
                                        )
                                    )
                                }
                                sotcaller(context)
                            } else {
                                sotcaller(context)
                                Log.e("Screen State  ", "Screen is off")
                            }
                        }
                    }
                }
            }
        }

    private fun packageinformation(): Date {
        val pack = "com.brave.browser"
        val pm: PackageManager = this.packageManager
        val packageInfo: PackageInfo = pm.getPackageInfo(pack, PackageManager.GET_PERMISSIONS)
        val installTime = Date(packageInfo.firstInstallTime)
        var updateTime = Date(packageInfo.lastUpdateTime)

        return installTime
    }



}
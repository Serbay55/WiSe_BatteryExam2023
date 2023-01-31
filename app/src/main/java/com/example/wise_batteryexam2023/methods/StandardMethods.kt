package com.example.wise_batteryexam2023.methods

import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.wise_batteryexam2023.MainActivity
import com.example.wise_batteryexam2023.R
import com.example.wise_batteryexam2023.RunningApps
import com.example.wise_batteryexam2023.ScreenActivity
import com.example.wise_batteryexam2023.data.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*
import kotlin.concurrent.schedule

class StandardMethods (applicationContext: Context){

    var newcycle: Double = 0.0
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
    var app: Context = applicationContext



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
    //Dataaccessobject service
    var cDao: CDAO = db.cDAO()
    var scDao: CDAO = sdb.cDAO()
    var hcDao: CDAO = hdb.cDAO()
    var hDao: CDAO = hdbs.cDAO()
    var sotDao: CDAO = sotdb.cDAO()
    var lraDao: CDAO = lradb.cDAO()
    var fcsDao: CDAO = fcsdb.cDAO()
    lateinit var bstate : BatteryState


    fun checkForegroundapp(context: Context): String{
        val s: String = RunningApps().getCurrentForegroundRunningApp(context)
        return s
    }
    //calculates difference of current year and year entered in inital startup
    fun calculatediffyear(input: Int){
        val difference = getCurrentYear() - input
        if(difference == 0){
            updatefinalchargecycles(0.0)
        } else {
            updatefinalchargecycles(difference * 365.0)
        }
    }

    //saves the last ever recorded charge or updates it if it already exists
    fun setlastchargestate(){
        CoroutineScope(Dispatchers.IO).launch{
            val s = scDao.checkExistingLC(1)
            if(s == 0){
                scDao.insertLCS(LCS(0, getBattery()))
            } else {
                scDao.updateLCS(LCS(1,getBattery()))
            }
            //To Do deleter of selected table row

        }
    }

    //Essenstial function to calculate Charge / discharge cycles based on temperature parameters
    fun checklastchargestate(){
        CoroutineScope(Dispatchers.IO).launch{
            val lastrecordedcharge : Int
            if(scDao.checkExistingLC(1) == 0) lastrecordedcharge = 0 else lastrecordedcharge = scDao.getLastCharge()
            val currentChargeValue = getBattery()
            checkDifference(lastrecordedcharge, currentChargeValue)
            if(newcycle >= 0.1) {
                val newcs = (newcycle * tCheck())

                if(fcsDao.checkExistingFCS(1)== 0){fcsDao.insertFCS(FCS(0,newcs))} else {fcsDao.updateFCS(
                    FCS(1,fcsDao.getFCS(1)+newcs)
                )}

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

                //TO DO:: newcycle never reaches below 0.1 cycles and therefore it always updates last recorded charge -> shouldn't happen
            }
        }
    }

    //Necessary for estimation of Battery Health
    fun updatefinalchargecycles(cycles: Double){
        CoroutineScope(Dispatchers.IO).launch {
            if (fcsDao.checkExistingFCS(1) == 0) {
                fcsDao.insertFCS(FCS(0, cycles))
            } else {
                fcsDao.updateFCS(FCS(1, fcsDao.getFCS(1) + cycles))
            }
            batteryhealthcalucation()
        }
    }

    //calculates based on practical knowledge the estimated time left before next Battery Upgrade
    fun calclifeexpectancy(): Int{
        val life = 800.0
        val discrepancy: Double = fcsDao.getFCS(1)
        return (life-discrepancy).toInt()
    }

    //Cake chart filler for current nett battery capacity
    fun getnetchargecapacity(): Float {
        var currentBattery = getBattery().toFloat()
        var currentHealth: Float = 0.0F
        var result: Int = 0
        var finalresult = 0f
        CoroutineScope(Dispatchers.IO).launch {
            if (hcDao.checkexistinghealth() == 1) {
                currentHealth = hcDao.getCurrentHealth().toFloat()
                finalresult = ((currentHealth / 100) * currentBattery)
                result = 1
            } else {
                batteryhealthcalucation()
                getnetchargecapacity()
            }
        }

        if(result == 1){
            return 0.9f
        }
        return 0.7f
    }

    fun checkDifference(givenCharge: Int, currentCharge: Int){

        val calc = kotlin.math.abs(givenCharge - currentCharge)
        for (i in intervals.indices) {
            if (calc >= intervals[0][0] && calc <= intervals[i][0]) {
                newcycle = (intervals[i][1] * 0.1)
            } else {
                newcycle = 0.0
            }
        }
    }


    fun batteryStatechecker(){
        CoroutineScope(Dispatchers.IO).launch {
            val getBat = async {
                Timer().schedule(10000) {
                    Log.e("Test","Test")
                    checklastchargestate()
                    checkBatteryCondition()
                    checkVoltageHealth()
                    checkTemperatureCondition()
                    batteryStatechecker()
                }
            }
        }
    }

    fun getBattery() : Int{
        bstate = BatteryState()
        return bstate.getBatteryPercentage(app)
    }

    fun getCurrentDay(): Int {
        val cal: Calendar = Calendar.getInstance()
        return cal.get(Calendar.DAY_OF_YEAR)
    }
    fun getCurrentYear(): Int{
        val cal: Calendar = Calendar.getInstance()
        return cal.get(Calendar.YEAR)
    }
    fun getCurrentDayofMonth(): Int{
        val cal: Calendar = Calendar.getInstance()
        return cal.get(Calendar.DAY_OF_MONTH)
    }

    fun tCheck(): Double{
        val temperature = BatteryState().batteryTemperature(app)
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

    fun actionHealth(){
        CoroutineScope(Dispatchers.IO).launch {
            val gBHS = async {
                Timer().schedule(60000*60*24){
                    if(getCurrentDayofMonth() == 1) {
                        batteryhealthcalucation()
                        actionHealth()
                    }
                    actionHealth()
                }
            }
        }
    }

    fun batteryhealthcalucation(){
        var cycle = 0.0
        CoroutineScope(Dispatchers.IO).launch{

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



    fun sotcaller(context: Context){
        CoroutineScope(Dispatchers.IO).launch{
            val async = async {
                Timer().schedule(180000){
                    CoroutineScope(Dispatchers.IO).launch {
                        if (ScreenActivity().checkScreenActivity(context)) {
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
                        }
                    }
                }
            }
        }
    }

    fun packageinformation(): Date {
        val pack = "com.brave.browser"
        val pm: PackageManager = app.packageManager
        val packageInfo: PackageInfo = pm.getPackageInfo(pack, PackageManager.GET_PERMISSIONS)
        val installTime = Date(packageInfo.firstInstallTime)
        var updateTime = Date(packageInfo.lastUpdateTime)

        return installTime
    }

    fun checkBatteryCondition(){
        if(getBattery()<20) notificationHandling("Battery very low!","Please charge your battery to keep it above 20% and below 90%","Battery Warning")
    }

    fun checkVoltageHealth(){
        if(BatteryState().getBatteryVoltage(app) < 3.8 && BatteryState().getBatteryPercentage(app) == 100 && !BatteryState().isBatterybeingcharged(app)){
            notificationHandling("Voltage Error!","Your voltage levels are way below what it should be! Its time for a new Battery","Battery Warning")
        }
    }

    fun checkTemperatureCondition(){
        if(BatteryState().batteryTemperature(app) > 40) notificationHandling("Temperature Error!", "Your Battery temperature is way too high make sure you cool down the Battery by not using it","Battery Warning")
    }

    fun notificationHandling(title: String, message: String, smallmessage: String){
        val b = NotificationCompat.Builder(this.app)
        b.setAutoCancel(true).
        setDefaults(NotificationCompat.DEFAULT_ALL).
        setTicker(smallmessage).
        setContentTitle(title).
        setContentText(message).
        setSmallIcon(R.drawable.large_battery_icon)

        var nm = this.app.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(1,b.build())
    }
}
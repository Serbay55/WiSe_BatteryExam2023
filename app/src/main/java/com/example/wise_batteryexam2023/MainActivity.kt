package com.example.wise_batteryexam2023

import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.compose.setContent
import com.example.wise_batteryexam2023.ui.theme.WiSe_BatteryExam2023Theme
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.room.Room
import com.example.wise_batteryexam2023.data.*
import com.example.wise_batteryexam2023.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.concurrent.schedule
import java.util.*


@Suppress("DEPRECATION")
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

    //lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Initial first start of App on Device
        val sharedPreferences: SharedPreferences = getSharedPreferences("",Context.MODE_PRIVATE)
        val firstTime: Boolean = sharedPreferences.getBoolean("firstTime",true)
        if(firstTime) {
            val editor : SharedPreferences.Editor = sharedPreferences.edit()
            editor.putBoolean("firstTime", false)
            editor.apply()
            firstbuilddialog()
        }
        setContent {
            WiSe_BatteryExam2023Theme {

            }
        }



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
        //Dataaccessobject service
        hDao = hdbs.cDAO()
        cDao = db.cDAO()
        scDao = sdb.cDAO()
        hcDao = hdb.cDAO()
        lraDao = lradb.cDAO()
        sotDao = sotdb.cDAO()
        fcsDao = fcsdb.cDAO()



        sotcaller(this)
        batteryStatechecker()
        actionHealth()

    }


    private fun firstbuilddialog(){
        var year: Int
        var stringer: String
        val customTV = TextView(this)
        customTV.text = "Please enter the year you started using your device for Battery Health estimation"
        val builder = AlertDialog.Builder(this)
        val inflater: LayoutInflater = layoutInflater
        val dialogLayout: View = inflater.inflate(R.layout.insert_year_layout,null)
        val editText: EditText = dialogLayout.findViewById<EditText>(R.id.et_editText)

        with(builder){
            setCustomTitle(customTV)
            //setTitle("Please enter the year you started using your device for Battery Health estimation")
            setPositiveButton("Submit"){
                dialog, which -> stringer = editText.text.toString()
                year = Integer.parseInt(stringer)
                if(year <= getCurrentYear() && year > 2008){
                    calculatediffyear(year)
                } else {
                    firstbuilddialogerror()
                }
            }
            setNegativeButton("Cancel"){dialog, which -> calculatediffyear(getCurrentYear())}

            setView(dialogLayout)
            show()
        }
    }

    private fun firstbuilddialogerror(){

        val builder = AlertDialog.Builder(this)
        val inflater: LayoutInflater = layoutInflater
        val customTextView = TextView(this)
        customTextView.text = "The first android device existed starting 2008 and your phone can't be newer than the current date of year, please enter proper information!"
        val dialogLayout: View = inflater.inflate(R.layout.error_year_layout,null)


        with(builder){
            setCustomTitle(customTextView)
            setPositiveButton("Understood!"){
                    dialog, which -> firstbuilddialog()
            }
            setView(dialogLayout)
            show()
        }
    }


    /*unfortunately useless action, because context of getting current
    foreground app only gets the app itself and no other foreign packages*/
    private fun checkForegroundapp(): String{
        val s: String = RunningApps().getCurrentForegroundRunningApp(this)
        return s
    }
    //calculates difference of current year and year entered in inital startup
    private fun calculatediffyear(input: Int){
        val difference = getCurrentYear() - input
        if(difference == 0){
            updatefinalchargecycles(0.0)
        } else {
            updatefinalchargecycles(difference * 365.0)
        }
    }

    //saves the last ever recorded charge or updates it if it already exists
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

    //Essenstial function to calculate Charge / discharge cycles based on temperature parameters
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

                //TO DO:: newcycle never reaches below 0.1 cycles and therefore it always updates last recorded charge -> shouldn't happen
            }
        }
    }

    //Necessary for estimation of Battery Health
    private fun updatefinalchargecycles(cycles: Double){
        lifecycleScope.launch(Dispatchers.IO) {
            if (fcsDao.checkExistingFCS(1) == 0) {
                fcsDao.insertFCS(FCS(0, cycles))
            } else {
                fcsDao.updateFCS(FCS(1, fcsDao.getFCS(1) + cycles))
            }
            batteryhealthcalucation()
        }
    }

    //calculates based on practical knowledge the estimated time left before next Battery Upgrade
    private fun calclifeexpectancy(): Int{
        val life = 800.0
        val discrepancy: Double = fcsDao.getFCS(1)
        return (life-discrepancy).toInt()
    }

    //Cake chart filler for current nett battery capacity
    private fun getnetchargecapacity(): Float {
        val currentBattery = getBattery().toFloat()
        val currentHealth: Float
        if(hcDao.checkexistinghealth() == 1){
            currentHealth = hcDao.getCurrentHealth().toFloat()
            return ((currentHealth/100) * currentBattery)
        } else {
            batteryhealthcalucation()
            return getnetchargecapacity()
        }
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
                    checkBatteryCondition()
                    checkVoltageHealth()
                    checkTemperatureCondition()
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
    private fun getCurrentDayofMonth(): Int{
        val cal: Calendar = Calendar.getInstance()
        return cal.get(Calendar.DAY_OF_MONTH)
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

    private fun packageinformation(): Date {
        val pack = "com.brave.browser"
        val pm: PackageManager = this.packageManager
        val packageInfo: PackageInfo = pm.getPackageInfo(pack, PackageManager.GET_PERMISSIONS)
        val installTime = Date(packageInfo.firstInstallTime)
        var updateTime = Date(packageInfo.lastUpdateTime)

        return installTime
    }

    private fun checkBatteryCondition(){
        if(getBattery()<20) notificationHandling("Battery very low!","Please charge your battery to keep it above 20% and below 90%","Battery Warning")
    }

    private fun checkVoltageHealth(){
        if(BatteryState().getBatteryVoltage(this) < 3.8 && BatteryState().getBatteryPercentage(this) == 100 && !BatteryState().isBatterybeingcharged(this)){
            notificationHandling("Voltage Error!","Your voltage levels are way below what it should be! Its time for a new Battery","Battery Warning")
        }
    }

    private fun checkTemperatureCondition(){
        if(BatteryState().batteryTemperature(this) > 40) notificationHandling("Temperature Error!", "Your Battery temperature is way too high make sure you cool down the Battery by not using it","Battery Warning")
    }

    private fun notificationHandling(title: String, message: String, smallmessage: String){
        val b = NotificationCompat.Builder(this.applicationContext)
        b.setAutoCancel(true).
                setDefaults(NotificationCompat.DEFAULT_ALL).
                setTicker(smallmessage).
                setContentTitle(title).
                setContentText(message).
                setSmallIcon(R.drawable.large_battery_icon)

        var nm = this.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(1,b.build())
    }






}
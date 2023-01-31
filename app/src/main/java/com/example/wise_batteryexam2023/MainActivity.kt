package com.example.wise_batteryexam2023




import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import com.example.wise_batteryexam2023.ui.theme.WiSe_BatteryExam2023Theme
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.compose.ui.platform.LocalContext
import com.example.wise_batteryexam2023.data.*
import com.example.wise_batteryexam2023.ui.screens.MainScreen
import com.example.wise_batteryexam2023.ui.theme.*
import com.example.wise_batteryexam2023.methods.StandardMethods
import java.util.*


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {



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
            firstbuilddialog(this)
        }
        setContent {
            WiSe_BatteryExam2023Theme {
                MainScreen()
            }
        }



        val methods: StandardMethods = StandardMethods(this)
        methods.actionHealth()
        StandardMethods(this).sotcaller(this)
        StandardMethods(this).batteryStatechecker()
        StandardMethods(this).actionHealth()



    }


    private fun firstbuilddialog(context: Context){
        var year: Int
        var stringer: String
        val customTV = TextView(context)
        customTV.text = "Please enter the year you started using your device for Battery Health estimation"
        val builder = AlertDialog.Builder(context)
        val inflater: LayoutInflater = layoutInflater
        val dialogLayout: View = inflater.inflate(R.layout.insert_year_layout,null)
        val editText: EditText = dialogLayout.findViewById<EditText>(R.id.et_editText)

        with(builder){
            setCustomTitle(customTV)
            //setTitle("Please enter the year you started using your device for Battery Health estimation")
            setPositiveButton("Submit"){
                dialog, which -> stringer = editText.text.toString()
                year = Integer.parseInt(stringer)
                if(year <= StandardMethods(context).getCurrentYear() && year >= 2008){
                    StandardMethods(context).calculatediffyear(year)
                } else {
                    firstbuilddialogerror(context)
                }
            }
            setNegativeButton("Cancel"){dialog, which -> StandardMethods(context).calculatediffyear(StandardMethods(context).getCurrentYear())}

            setView(dialogLayout)
            show()
        }
    }

    private fun firstbuilddialogerror(context: Context){

        val builder = AlertDialog.Builder(this)
        val inflater: LayoutInflater = layoutInflater
        val customTextView = TextView(this)
        customTextView.text = "The first android device existed starting 2008 and your phone can't be newer than the current date of year, please enter proper information!"
        val dialogLayout: View = inflater.inflate(R.layout.error_year_layout,null)


        with(builder){
            setCustomTitle(customTextView)
            setPositiveButton("Understood!"){
                    dialog, which -> firstbuilddialog(context)
            }
            setView(dialogLayout)
            show()
        }
    }
    @Composable
    public fun receiveContext(): Context{
        val context: Context = LocalContext.current
        return context
    }









}

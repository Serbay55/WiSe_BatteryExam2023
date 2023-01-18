package com.example.wise_batteryexam2023.data


import android.content.Context
import android.content.Context.BATTERY_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager


class BatteryState {

    fun getBatteryPercentage(context: Context): Int{
        val bm = context.getSystemService(BATTERY_SERVICE) as BatteryManager
        return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    }

    companion object fun batteryTemperature(context: Context): Double{
        val intent: Intent? = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val temp: Int? = intent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,0)?.div(10)
        if (temp != null) {
            return temp.toDouble()
        } else {
            return 0.0
        }
    }





}
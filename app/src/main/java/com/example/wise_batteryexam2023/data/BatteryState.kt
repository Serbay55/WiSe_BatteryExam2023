package com.example.wise_batteryexam2023.data


import android.content.Context
import android.content.Context.BATTERY_SERVICE
import android.os.BatteryManager
import kotlinx.coroutines.CoroutineScope


class BatteryState {
    public fun getBatteryPercentage(context: Context): Int{
        val bm = context.getSystemService(BATTERY_SERVICE) as BatteryManager
        return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    }
}
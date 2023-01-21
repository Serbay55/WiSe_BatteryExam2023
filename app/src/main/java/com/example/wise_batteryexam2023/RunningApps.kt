package com.example.wise_batteryexam2023

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.Context

class RunningApps {
    fun getCurrentForegroundRunningApp(context: Context): String {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = am.runningAppProcesses
        for (appProcessInfo in appProcesses) {
            if (appProcessInfo.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return appProcessInfo.processName
            }
        }
        return "no app available"
    }
}
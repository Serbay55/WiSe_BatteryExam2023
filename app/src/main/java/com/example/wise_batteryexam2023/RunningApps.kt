package com.example.wise_batteryexam2023

import android.app.Activity
import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.text.format.DateUtils
import android.util.Log
import java.util.*

class RunningApps {
    fun getCurrentForegroundRunningApp(context: Context): String {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = am.runningAppProcesses
        for (appProcessInfo in appProcesses) {
            Log.i("For all::  ",""+appProcessInfo.processName)
            if (appProcessInfo.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return appProcessInfo.processName
            } else {

                return "There is no app in the foreground"
            }
        }
        return "No App in the foreground"
    }


    fun getOldestAppsAge(context: Context) : Date{
        var appsAge: Long = 0
        try{
            appsAge = System.currentTimeMillis()
            val pkgManager: PackageManager = context.packageManager
            val lsApp: List<ApplicationInfo> = pkgManager.getInstalledApplications(0)
            for (localApplicationInfo in lsApp){
                val pkgName: String = localApplicationInfo.packageName
                if(localApplicationInfo.flags == 0 && ApplicationInfo.FLAG_SYSTEM == 0){
                    val info: PackageInfo = pkgManager.getPackageInfo(pkgName, 0)
                    val firstInstallTime : Long = info.firstInstallTime
                    if(firstInstallTime < appsAge){
                        appsAge = info.firstInstallTime
                    }
                }
            }
        } catch (e: java.lang.Exception){
            e.printStackTrace()
        }
        return Date(appsAge)
    }

}
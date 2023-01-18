package com.example.wise_batteryexam2023.methods

import kotlin.Throws
import android.content.pm.PackageManager
import com.example.wise_batteryexam2023.MainActivity

class InstallTime {
    @Throws(PackageManager.NameNotFoundException::class)
    fun getInstallTime(context: MainActivity): Int {
        val pm = context.packageManager
        val info = pm.getPackageInfo("com.android.settings", 0)
        return info.firstInstallTime.toInt()
    }
}
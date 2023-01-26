package com.example.wise_batteryexam2023.methods

import kotlin.Throws
import android.content.pm.PackageManager
import com.example.wise_batteryexam2023.MainActivity
import java.util.Date

class InstallTime {
    @Throws(PackageManager.NameNotFoundException::class)
    fun getInstallTime(context: MainActivity): Date {
        val pm = context.packageManager
        val info = pm.getPackageInfo("android", 0)
        return Date(info.firstInstallTime)
    }
}
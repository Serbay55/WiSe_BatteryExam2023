package com.example.wise_batteryexam2023

import android.content.Context
import android.hardware.display.DisplayManager
import android.view.Display

class ScreenActivity {
    fun checkScreenActivity(context: Context): Boolean {
        val dm = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        for (display in dm.displays) {
            if (display.state != Display.STATE_OFF) {
                return true
            }
        }
        return false
    }
}
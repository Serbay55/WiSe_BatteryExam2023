package com.example.wise_batteryexam2023;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.view.Display;

public class ScreenActivity {
    public boolean checkScreenActivity(Context context){
        DisplayManager dm = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
        for(Display display: dm.getDisplays()){
            if(display.getState() != Display.STATE_OFF){
                return true;
            }
        }
        return false;
    }
}

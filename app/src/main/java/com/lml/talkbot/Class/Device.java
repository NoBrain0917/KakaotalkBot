package com.lml.talkbot.Class;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;

import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSStaticFunction;

//Device 라이브러리
public class Device extends ScriptableObject {

    private static Context ctx;

    //ctx 불러오기
    public static void init(Context context){
        ctx = context;
    }

    @Override
    public String getClassName() {
        return "Device";
    }
    

    @JSStaticFunction
    public static String getPhoneModel(){
        return Build.MODEL;
    }

    @JSStaticFunction
    public static int getAndroidSDKVersion(){
        return Build.VERSION.SDK_INT;
    }

    @JSStaticFunction
    public static String getAndroidVersion(){
        return Build.VERSION.RELEASE;
    }

    @JSStaticFunction
    public static int getBattery(){
        Intent intentBattery = ctx.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = intentBattery.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = intentBattery.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPct = level / (float) scale;
        return (int)(batteryPct * 100);
    }

    @JSStaticFunction
    public static boolean isCharging(){
        Intent intentBattery = ctx.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int status = intentBattery.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = false;
        if(status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL){
            isCharging = true;
        }
        return isCharging;
    }


}

package com.pab.unpar.pklmobilekelompok;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class SensorData implements SensorEventListener {
    private Activity activity;
    private SensorManager manager;
    private Sensor sensor;

    public SensorData(Activity activity, SensorManager manager){
        this.activity=activity;
        Log.d("TEST","constructor");
        this.manager=manager;
        sensor=manager.getDefaultSensor(Sensor.TYPE_LIGHT);
        manager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float val=event.values[0];
        if(val<100){
            Utils.changeToTheme(activity, Utils.THEME_DEFAULT_BLACK);
            SharedPreferences.Editor ed=activity.getSharedPreferences("dataProduk", Context.MODE_PRIVATE).edit();
            ed.putString("themeCode", Utils.THEME_DEFAULT_BLACK + "");
            ed.commit();
        }
        else{
            Utils.changeToTheme(activity, Utils.THEME_DEFAULT_WHITE);
            SharedPreferences.Editor ed=activity.getSharedPreferences("dataProduk", Context.MODE_PRIVATE).edit();
            ed.putString("themeCode", Utils.THEME_DEFAULT_WHITE + "");
            ed.commit();
        }
        Log.d("Cahaya",val+"");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void unregisterSensor() {
        manager.unregisterListener(this);
        Log.d("Sensor", "Unregistered");
    }

}

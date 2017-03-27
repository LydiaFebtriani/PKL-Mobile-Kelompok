package com.pab.unpar.pklmobilekelompok;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class SensorData{
    private Activity activity;
    private SensorManager manager;
    private Sensor sensor;
    private SharedPreferences sp;
    private SensorEventListener listener;

    public SensorData(final Activity activity, SensorManager manager){
        this.activity=activity;
        Log.d("TEST","constructor");
        this.manager=manager;
        sensor=manager.getDefaultSensor(Sensor.TYPE_LIGHT);
        listener=new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float val=event.values[0];
                if(sp.getBoolean("useSensor",false)){
                    if(val<10){
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
                }
                Log.d("Cahaya",val+"");
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        manager.registerListener(listener,sensor,SensorManager.SENSOR_DELAY_NORMAL);
        sp=activity.getSharedPreferences("dataProduk",Context.MODE_PRIVATE);
    }

    public void unregisterSensor() {
        manager.unregisterListener(listener);
        Log.d("Sensor", "Unregistered");
    }

}

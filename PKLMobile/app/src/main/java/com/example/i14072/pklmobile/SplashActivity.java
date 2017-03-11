package com.example.i14072.pklmobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Log.d("SPLASHSCREEN",findViewById(R.id.textView).isShown()+"");

        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(3000);
                }catch(InterruptedException ie){
                    ie.printStackTrace();
                }finally{
                    Intent i = new Intent(SplashActivity.this,LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        };
        timerThread.start();
    }
}

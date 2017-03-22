package com.pab.unpar.pklmobilekelompok;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Bundle;
import android.widget.EditText;

public class Login extends Activity {
    EditText edUserName;
    EditText edPassword;
    private DataManipulator dh;
    private SharedPreferences sp;
    private SensorData sensorData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sensorData=new SensorData(this,(SensorManager)getSystemService(Context.SENSOR_SERVICE));
        sp = getSharedPreferences("dataProduk", MODE_PRIVATE);
        Utils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_login);

        edUserName  = (EditText) findViewById(R.id.inputUsername);
        edPassword = (EditText) findViewById(R.id.inputPassword);
        Button btnLogin = (Button) findViewById(R.id.buttonLogin);
        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0){
                if(checkUser(edUserName.getText().toString(),edPassword.getText().toString())){
                    Toast.makeText(getApplicationContext(),"Login berhasil",Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(Login.this, Home.class);
                    startActivity(i);
                    finish();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Username dan password tidak cocok atau Registrasi bila belum terdaftar !",Toast.LENGTH_SHORT).show();
                }
            }
        });

        TextView register = (TextView) findViewById(R.id.registerLink);
        register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0){
                Intent i = new Intent(Login.this, Register.class);
                startActivity(i);
            }
        });

        TextView keluar = (TextView) findViewById(R.id.viewKeluar);
        keluar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0){
                Intent i = new Intent(Login.this, SplashKeluar.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void onBackPressed(){}

    public boolean checkUser(String email, String password){
        Soap soap = new Soap();
        String[] id =soap.login(this,email,password);

        SharedPreferences.Editor ed = sp.edit();
        ed.clear();
        ed.commit();
        if(id[0] != null) {
            if (id.length == 1) {
                ed.putString("sessionId", id[0]);
                ed.putString("user", email);
                ed.commit();
                Log.d("Session Id", id[0]);
                return true;
            } else if (id.length == 3) {
                ed.putString("idUser", id[0]);
                ed.putString("user", id[1]);
                ed.putString("password", id[2]);
                ed.commit();
                Log.d("Login no connection", id[0]+" "+id[1]+" "+id[2]);
                return true;
            }
            else{
                return false;
            }
        }
        else{
            return false;
        }
//        dh = new DataManipulator(this);
//        String[] select = dh.select1User(email,password);
//        if(select[0] != null){
//            SharedPreferences.Editor ed = sp.edit();
//            ed.putInt("idUser",Integer.parseInt(select[0]));
//            ed.commit();
//            return true;
//        }
//        else{
//            return false;
//        }
    }

}

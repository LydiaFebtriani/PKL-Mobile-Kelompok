package com.pab.unpar.pklmobilekelompok;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sp = getSharedPreferences("dataProduk", MODE_PRIVATE);

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
        String id =soap.login(email,password);
        if(id != null){
            SharedPreferences.Editor ed = sp.edit();
            ed.putString("sessionId",id);
            ed.commit();
            Log.d("Session Id",id);
            return true;
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

package com.example.i14072.pklmobile;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    EditText edUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("LOGIN");

        edUserName=(EditText)findViewById(R.id.editTextUserName);
        Button btnLogin=(Button)findViewById(R.id.buttonLogin);
        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Login berhasil ...",Toast.LENGTH_SHORT).show();

                Intent i=new Intent(LoginActivity.this,KatalogActivity.class);
//                Bundle b=new Bundle();
//                b.putString("UserName",edUserName.getText().toString());
//                i.putExtras(b);

                SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editData=sp.edit();
                editData.putString("UserName",edUserName.getText().toString());
                editData.commit();

                startActivity(i);
                finish();
            }
        });

        Button btnRegister=(Button)findViewById(R.id.buttonRegistrasi);
        btnRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent i=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(i);
            }
        });
    }
}

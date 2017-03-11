package com.example.i14072.pklmobile;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    EditText edUserName,edPassword;
    private SharedPreferences sp;
    private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("LOGIN");
        sp=getSharedPreferences("data",MODE_PRIVATE);
        db=new DatabaseHandler(this);

        edUserName=(EditText)findViewById(R.id.editTextUserName);
        edPassword=(EditText)findViewById(R.id.editTextpassword);
        Button btnLogin=(Button)findViewById(R.id.buttonLogin);
        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String username=edUserName.getText().toString();
                String password=edPassword.getText().toString();
                boolean valid=db.updateLogin(username,password);

                Log.d("BACA PASS",password);

                if(valid){
                    Toast.makeText(getApplicationContext(),"Login berhasil ...",Toast.LENGTH_SHORT).show();
                    Intent i=new Intent(LoginActivity.this,KatalogActivity.class);
                    SharedPreferences.Editor editData=sp.edit();
                    String[] temp=db.selectIdName(edUserName.getText().toString());
                    editData.putInt("idUser",Integer.parseInt(temp[0]));
                    editData.putString("Name",temp[1]);
                    editData.commit();
                    startActivity(i);
                    finish();
                }
                else{
                    new AlertDialog.Builder(LoginActivity.this).setTitle("LOGIN GAGAL").setMessage("Username atau password tidak ditemukan.\nPastikan Anda memasukkan e-mail yang didaftarkan sebagai username dan tanggal lahir sebagai password.")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();
                }
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

    @Override
    public void onBackPressed() {
        //do nothing
    }
}

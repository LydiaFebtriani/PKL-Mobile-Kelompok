package com.example.i14072.pklmobile;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {
    private SimpleDateFormat formatter;
    private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle("REGISTRASI PENGGUNA");
        db=new DatabaseHandler(this);

        formatter=new SimpleDateFormat("ddMMyyyy", Locale.US);
        final TextView edTglLahir=(TextView)findViewById(R.id.iTglLahir);
        edTglLahir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Check","EditText diklik");
                Calendar date=Calendar.getInstance();
                date.set(1995,Calendar.OCTOBER,5);
                DatePickerDialog dialog=new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate=Calendar.getInstance();
                        newDate.set(year,monthOfYear,dayOfMonth);
                        edTglLahir.setText(formatter.format(newDate.getTime()));
                    }
                },date.get(Calendar.YEAR),date.get(Calendar.MONTH),date.get(Calendar.DAY_OF_MONTH));
                Log.d("Dialog existence",""+dialog);
                dialog.show();
            }
        });

        Button btnSimpan=(Button)findViewById(R.id.btnSimpan);
        btnSimpan.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                EditText edUsername=(EditText)findViewById(R.id.iUser);
                boolean sudahAda=db.checkUsername(edUsername.getText().toString(),edTglLahir.getText().toString());

                if(!sudahAda){
                    EditText edNama=(EditText)findViewById(R.id.iNama);
                    EditText edAlamat=(EditText)findViewById(R.id.iAlamat);
                    EditText edHP=(EditText)findViewById(R.id.iNoHP);
                    EditText edProdUnggul=(EditText)findViewById(R.id.iProdukUnggul);

                    Log.d("SIMPAN PASS",edTglLahir.getText().toString());

                    db.updateRegister(edUsername.getText().toString(),edNama.getText().toString(),edAlamat.getText().toString(),edHP.getText().toString(),edTglLahir.getText().toString(),edProdUnggul.getText().toString());

                    new AlertDialog.Builder(RegisterActivity.this)
                            .setTitle("REGISTRASI BERHASIL")
                            .setMessage("Selamat Anda telah terdaftar, silakan login untuk menggunakan aplikasi ini! Saat login, gunakan e-mail yang Anda telah daftarkan sebagai User Name!")
                            .setPositiveButton("Ok",new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Thread t=new Thread(){
                                        @Override
                                        public void run() {
                                            try{
                                                sleep(3000);
                                            }
                                            catch(InterruptedException ie){
                                                ie.printStackTrace();
                                            }
                                            finally{
                                                Intent i=new Intent(RegisterActivity.this,LoginActivity.class);
                                                startActivity(i);
                                                finish();
                                            }
                                        }
                                    };
                                    t.start();
                                }
                            })
                            .show();
                }
                else{
                    new AlertDialog.Builder(RegisterActivity.this).setTitle("USERNAME SUDAH DIPAKAI").setMessage("Username yang Anda gunakan sudah pernah didaftarkan di aplikasi PKL Mobile ini. Silakan kembali ke halaman login atau gunakan username lain.")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();
                }

            }
        });

        Button btnBatal=(Button)findViewById(R.id.btnBatal);
        btnBatal.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i=new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}

package com.example.i14059.ui;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Registrasi extends AppCompatActivity implements View.OnClickListener {
    private EditText birthDate;
    private DataBase database;
    SQLiteDatabase db;
    static final int DIALOG_ID = 0;


    private DatePickerDialog birthDatePickerDialog;

    private SimpleDateFormat dateFormatter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrasi);
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        findViewsById();
        database = new DataBase(this);

        setDateTimeField();
        Button btn = (Button)findViewById(R.id.regButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText editText1 = (EditText) findViewById(R.id.emailReg);
                EditText editText2 = (EditText) findViewById(R.id.nameReg);
                EditText editText3 = (EditText) findViewById(R.id.addReg);
                EditText editText4 = (EditText) findViewById(R.id.phoneReg);
                EditText editText5 = (EditText) findViewById(R.id.tanggalLahir);
                EditText editText6 = (EditText) findViewById(R.id.prodReg);

                String myEditText1=((TextView) editText1).getText().toString();
                String myEditText2=((TextView) editText2).getText().toString();
                String myEditText3=((TextView) editText3).getText().toString();
                String myEditText4=((TextView) editText4).getText().toString();
                String myEditText5=((TextView) editText5).getText().toString();
                String myEditText6=((TextView) editText6).getText().toString();

                db = database.getWritableDatabase();
                db.execSQL("insert into lPKL(user, nama, alamat, noHP, tglLahir, produkUnggulan) values ('" + myEditText1 + "', '"+ myEditText2 + "', '"+ myEditText3 + "', '"+
                        myEditText4 + "', '"+ myEditText5 + "', '"+
                        myEditText6 + "')");
                db.execSQL("insert into lProduk(namaProduk, hargaPokok, hargaJual, user) values ('" + myEditText6 + "', 0, 0, '" + myEditText1 + "');");
                //this.db = new DataBase(this);
                //this.db.insert(myEditText1,myEditText2,myEditText3,myEditText4,myEditText5,myEditText6);
                Toast.makeText(Registrasi.this, "Selamat anda telah terdaftar, silakan login untuk menggunakan aplikasi\n" +
                                "ini! Saat login, gunakan alamat email yang anda telah daftarkan sebagai User name!",
                        Toast.LENGTH_LONG).show();

                startActivity(new Intent(Registrasi.this, Login.class));
                showDialog(DIALOG_ID);
                //break;
            }
        });

        Button btn2 = (Button)findViewById(R.id.cancelButton);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Registrasi.this, Login.class));
            }
        });
    }
    private void findViewsById() {
        birthDate = (EditText) findViewById(R.id.tanggalLahir);
        birthDate.setInputType(InputType.TYPE_NULL);

    }

    private void setDateTimeField() {
        birthDate.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        birthDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                birthDate.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));


    }
    @Override
    public void onClick(View view) {
        if(view == birthDate) {
            birthDatePickerDialog.show();
        }}


}

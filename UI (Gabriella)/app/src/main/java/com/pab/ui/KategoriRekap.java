package com.pab.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class KategoriRekap extends AppCompatActivity {

    private SharedPreferences sp;
    private SharedPreferences.Editor ed;

    private DatePicker tanggalRekap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kategorirekap);

        sp = getSharedPreferences("data", MODE_PRIVATE);
        ed = sp.edit();


        Button btnLihat = (Button) findViewById(R.id.buttonLihatRekap);
        btnLihat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                tanggalRekap = (DatePicker)findViewById(R.id.pilihTanggalRekap);

                String tanggal = convertToString(tanggalRekap);

                ed.putString("tanggal_rekap", tanggal);
                ed.commit();

                Intent i = new Intent(KategoriRekap.this, Rekap.class);
                startActivity(i);

            }
        });

        Button btnKembali = (Button) findViewById(R.id.buttonKembali_LihatRekap);
        btnKembali.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(KategoriRekap.this, Transaksi.class);
                startActivity(i);
            }
        });
    }

    private String convertToString(DatePicker dp) {
        int tanggal = dp.getDayOfMonth();
        int bulan = dp.getMonth();
        int tahun = dp.getYear();

        Calendar c = Calendar.getInstance().getInstance();
        c.set(tahun, bulan, tanggal);

        Date d = c.getTime();

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String str = new String();
        try {
            str = df.format(d);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return str;
    }

}

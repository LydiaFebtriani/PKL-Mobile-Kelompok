package com.example.i14059.ui;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Transaksi extends AppCompatActivity {
    DataBase hp;
    SQLiteDatabase db;
    String[] namaProduk ;
    boolean[] visible ;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaksi);
        hp = new DataBase(this);
        namaProduk = new String[8];
        visible = new boolean[8];
        for(int i=0;i<8;i++){
            namaProduk[i] = "";
            visible[i] = false;
        }
        TextView tv = (TextView) findViewById(R.id.textViewWelcomes);
        final Bundle b = getIntent().getExtras();
        tv.setText("TRANSAKSI PENJUALAN PKL "+b.getString("UserName"));

       db = hp.getReadableDatabase();
        String query = "select namaProduk from lProduk where user = '" + b.getString("UserName") + "'";
        Cursor cursor=db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            for(int i=0;i<cursor.getCount();i++){
                namaProduk[i] = cursor.getString(0);
                visible[i] = true;
                cursor.moveToNext();
            }
        }

        final Button btnKatalog1 = (Button) findViewById(R.id.transaksi1);
        if(visible[0]){
            btnKatalog1.setText(namaProduk[0]);
            btnKatalog1.setVisibility(View.VISIBLE);
        }
        btnKatalog1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0){
                Intent i = new Intent(Transaksi.this, Jual.class);
                b.putString("Produk", btnKatalog1.getText().toString());
                i.putExtras(b);
                startActivity(i);
            }
        });

        final Button btnKatalog2 = (Button) findViewById(R.id.transaksi2);
        if(visible[1]){
            btnKatalog2.setText(namaProduk[1]);
            btnKatalog2.setVisibility(View.VISIBLE);
        }
        btnKatalog2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0){
                Intent i = new Intent(Transaksi.this, Jual.class);
                b.putString("Produk", btnKatalog2.getText().toString());
                i.putExtras(b);
                startActivity(i);
            }
        });

        final Button btnKatalog3 = (Button) findViewById(R.id.transaksi3);
        if(visible[2]){
            btnKatalog3.setText(namaProduk[2]);
            btnKatalog3.setVisibility(View.VISIBLE);
        }
        btnKatalog3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0){
                Intent i = new Intent(Transaksi.this, Jual.class);
                b.putString("Produk", btnKatalog3.getText().toString());
                i.putExtras(b);
                startActivity(i);
            }
        });

        final Button btnKatalog4 = (Button) findViewById(R.id.transaksi4);
        if(visible[3]){
            btnKatalog4.setText(namaProduk[3]);
            btnKatalog4.setVisibility(View.VISIBLE);
        }
        btnKatalog4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0){
                Intent i = new Intent(Transaksi.this, Jual.class);
                b.putString("Produk", btnKatalog4.getText().toString());
                i.putExtras(b);
                startActivity(i);
            }
        });

        final Button btnKatalog5 = (Button) findViewById(R.id.transaksi5);
        if(visible[4]){
            btnKatalog5.setText(namaProduk[4]);
            btnKatalog5.setVisibility(View.VISIBLE);
        }
        btnKatalog4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0){
                Intent i = new Intent(Transaksi.this, Jual.class);
                b.putString("Produk", btnKatalog5.getText().toString());
                i.putExtras(b);
                startActivity(i);
            }
        });

        final Button btnKatalog6 = (Button) findViewById(R.id.transaksi6);
        if(visible[5]){
            btnKatalog6.setText(namaProduk[5]);
            btnKatalog6.setVisibility(View.VISIBLE);
        }
        btnKatalog4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0){
                Intent i = new Intent(Transaksi.this, Jual.class);
                b.putString("Produk", btnKatalog6.getText().toString());
                i.putExtras(b);
                startActivity(i);
            }
        });

        Button btnKeluar = (Button) findViewById(R.id.exitButtons);
        btnKeluar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0){
                Intent i = new Intent(Transaksi.this, Exit.class);
                i.putExtras(b);
                startActivity(i);
            }
        });

        Button btnRekap = (Button) findViewById(R.id.rekButtons);
        btnRekap.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0){
                Intent i = new Intent(Transaksi.this, Rekap.class);
                b.putString("Produk", "");
                i.putExtras(b);
                startActivity(i);
            }
        });

        Button btnKatalog = (Button) findViewById(R.id.katButtons);
        btnKatalog.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0){
                Intent i = new Intent(Transaksi.this, Katalog.class);
                i.putExtras(b);
                startActivity(i);
            }
        });
    }
    }

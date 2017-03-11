package com.example.i14059.ui;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Katalog extends Activity{

    DataBase hp;

    SQLiteDatabase db;
    String[] namaProduk;
    boolean[] visible;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_katalog);
        final Bundle b = getIntent().getExtras();
        namaProduk = new String[8];
        visible = new boolean[8];
        hp = new DataBase(this);
        for(int i=0;i<8;i++){
            namaProduk[i] = "";
            visible[i] = false;
        }
        TextView tv = (TextView) findViewById(R.id.textViewWelcome);
        tv.setText("KATALOG PRODUK PKL "+b.getString("UserName"));

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

        final Button btnKatalog1 = (Button) findViewById(R.id.katalog1);
        if(visible[0]){
            btnKatalog1.setText(namaProduk[0]);
            btnKatalog1.setVisibility(View.VISIBLE);
        }
        btnKatalog1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0){
                Intent i = new Intent(Katalog.this, Produk.class);
                b.putString("Produk", btnKatalog1.getText().toString());
                i.putExtras(b);
                startActivity(i);
            }
        });

        final Button btnKatalog2 = (Button) findViewById(R.id.katalog2);
        if(visible[1]){
            btnKatalog2.setText(namaProduk[1]);
            btnKatalog2.setVisibility(View.VISIBLE);
        }
        btnKatalog2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0){
                Intent i = new Intent(Katalog.this, Produk.class);
                b.putString("Produk", btnKatalog2.getText().toString());
                i.putExtras(b);
                startActivity(i);
            }
        });

        final Button btnKatalog3 = (Button) findViewById(R.id.katalog3);
        if(visible[2]){
            btnKatalog3.setText(namaProduk[2]);
            btnKatalog3.setVisibility(View.VISIBLE);
        }
        btnKatalog3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0){
                Intent i = new Intent(Katalog.this, Produk.class);
                b.putString("Produk", btnKatalog3.getText().toString());
                i.putExtras(b);
                startActivity(i);
            }
        });

        final Button btnKatalog4 = (Button) findViewById(R.id.katalog4);
        if(visible[3]){
            btnKatalog4.setText(namaProduk[3]);
            btnKatalog4.setVisibility(View.VISIBLE);
        }
        btnKatalog4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0){
                Intent i = new Intent(Katalog.this, Produk.class);
                b.putString("Produk", btnKatalog4.getText().toString());
                i.putExtras(b);
                startActivity(i);
            }
        });

        final Button btnKatalog5 = (Button) findViewById(R.id.katalog5);
        if(visible[4]){
            btnKatalog5.setText(namaProduk[4]);
            btnKatalog5.setVisibility(View.VISIBLE);
        }
        btnKatalog5.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0){
                Intent i = new Intent(Katalog.this, Produk.class);
                b.putString("Produk", btnKatalog5.getText().toString());
                i.putExtras(b);
                startActivity(i);
            }
        });

        final Button btnKatalog6 = (Button) findViewById(R.id.katalog6);
        if(visible[5]){
            btnKatalog6.setText(namaProduk[5]);
            btnKatalog6.setVisibility(View.VISIBLE);
        }
        btnKatalog6.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0){
                Intent i = new Intent(Katalog.this, Produk.class);
                b.putString("Produk", btnKatalog6.getText().toString());
                i.putExtras(b);
                startActivity(i);
            }
        });

        Button btnKeluar = (Button) findViewById(R.id.buttonKeluar);
        btnKeluar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0){
                Intent i = new Intent(Katalog.this, Exit.class);
                startActivity(i);
            }
        });

        Button btnTambah = (Button) findViewById(R.id.buttonTambah);
        btnTambah.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0){
                Intent i = new Intent(Katalog.this, Produk.class);
                b.putString("Produk", "");
                i.putExtras(b);
                startActivity(i);
            }
        });

        Button btnTransaksi = (Button) findViewById(R.id.buttonTransaksi);
        btnTransaksi.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0){
                Intent i = new Intent(Katalog.this, Transaksi.class);
                i.putExtras(b);
                startActivity(i);
            }
        });
    }
}
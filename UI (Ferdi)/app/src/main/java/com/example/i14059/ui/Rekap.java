package com.example.i14059.ui;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Rekap extends AppCompatActivity {

    DataBase hp = new DataBase(this);
    SQLiteDatabase db;
    TextView[] textView = new TextView[10];
    TextView[] textViewTotal = new TextView[10];
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rekap);

        final Bundle b = getIntent().getExtras();

        textView[0] = (TextView)findViewById(R.id.textView1);
        textView[1] = (TextView)findViewById(R.id.textView2);
        textView[2] = (TextView)findViewById(R.id.textView3);
        textView[3] = (TextView)findViewById(R.id.textView4);
        textView[4] = (TextView)findViewById(R.id.textView5);
        textView[5] = (TextView)findViewById(R.id.textView6);

        textViewTotal[0] = (TextView)findViewById(R.id.textView7);
        textViewTotal[1] = (TextView)findViewById(R.id.textView8);
        textViewTotal[2] = (TextView)findViewById(R.id.textView9);
        textViewTotal[3] = (TextView)findViewById(R.id.textView10);
        textViewTotal[4] = (TextView)findViewById(R.id.textView11);
        textViewTotal[5] = (TextView)findViewById(R.id.textView12);

        for(int i=0;i<5;i++){
            textView[i].setVisibility(View.INVISIBLE);
            textViewTotal[i].setVisibility(View.INVISIBLE);
        }
        db = hp.getReadableDatabase();
        String query = "select tglJual, namaProduk, qtyJual, hargaJual from lTransaksi where lTransaksi.user = '" +
                b.getString("UserName") + "'";
        cursor = db.rawQuery(query, null);
        String res = "";
        int harga = 0;
        int qty = 0;
        int total = 0;
        if(cursor.moveToFirst()){
            for(int i=0;i<cursor.getCount();i++){
                res = i+1 + ".  " + cursor.getString(0) + " " + cursor.getString(1) + " " + cursor.getString(2) + "x" +
                        cursor.getString(3);
                textView[i].setVisibility(View.VISIBLE);
                textViewTotal[i].setVisibility(View.VISIBLE);
                textView[i].setText(res);
                qty = cursor.getInt(2);
                harga = cursor.getInt(3);
                int temp = qty*harga;
                textViewTotal[i].setText("" + temp);
                total+=qty*harga;
                cursor.moveToNext();
            }
            textView[cursor.getCount()].setVisibility(View.VISIBLE);
            textViewTotal[cursor.getCount()].setVisibility(View.VISIBLE);
            textView[cursor.getCount()].setText("Total Transaksi");
            textViewTotal[cursor.getCount()].setText("" + total);

        }
        Button btnKeluar = (Button) findViewById(R.id.buttonKeluar);
        btnKeluar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0){
                Intent i = new Intent(Rekap.this, Exit.class);
                startActivity(i);
                finish();
            }
        });

        Button btnKatalog = (Button) findViewById(R.id.buttonKatalog);
        btnKatalog.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0){
                Intent i = new Intent(Rekap.this, Katalog.class);
                i.putExtras(b);
                startActivity(i);
            }
        });

        Button btnTransaksi = (Button) findViewById(R.id.buttonTransaksi);
        btnTransaksi.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0){
                Intent i = new Intent(Rekap.this, Transaksi.class);
                i.putExtras(b);
                startActivity(i);
            }
        });
    }


}

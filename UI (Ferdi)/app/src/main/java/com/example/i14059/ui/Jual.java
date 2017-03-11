package com.example.i14059.ui;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Jual extends AppCompatActivity {
    DataBase hp = new DataBase(this);
    SQLiteDatabase db;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jual);

        final TextView produk = (TextView)findViewById(R.id.namaProduk);
        final EditText qty = (EditText)findViewById(R.id.Qty);
        final TextView harga = (TextView) findViewById(R.id.hargaJual);

        TextView tv = (TextView) findViewById(R.id.namaProduk);
        final Bundle b = getIntent().getExtras();
        produk.setText(b.getString("Produk"));

        this.db = hp.getReadableDatabase();
        String query = "select hargaJual from lProduk where lProduk.user = '" + b.getString("UserName") + "' AND lProduk.namaProduk = '" + b.getString("Produk") + "';";
        cursor=db.rawQuery(query,null);
        if (cursor.moveToFirst()){
            harga.setText(cursor.getString(0));
        }

        Button btnBatal = (Button) findViewById(R.id.buttonBatal);
        btnBatal.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0){
                Intent i = new Intent(Jual.this, Transaksi.class);
                i.putExtras(b);
                startActivity(i);
            }
        });

        Button btnProses = (Button) findViewById(R.id.buttonProses);
        btnProses.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0){
                cursor = db.rawQuery("select strftime('%Y%m%d')", null);
                cursor.moveToFirst();
                String date = cursor.getString(0);
                int kuan = Integer.parseInt(qty.getText().toString());
                int harg = Integer.parseInt(harga.getText().toString());
                db = hp.getWritableDatabase();
                String query = "insert into lTransaksi(namaProduk, hargaJual, qtyJual, tglJual, user) values('" + produk.getText().toString() + "', " +
                        harg + ", " + kuan + ", '" + date + "', '" + b.getString("UserName") + "');";
                db.execSQL(query);
                Toast.makeText(getApplicationContext(), "Terima kasih anda telah bertransaksi produk " + produk.getText() + " sebanyak " + qty.getText().toString() +" seharga " + kuan*harg +"", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(Jual.this, Transaksi.class);
                i.putExtras(b);
                startActivity(i);
            }
        });
    }
}

package com.example.i14059.ui;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Produk extends AppCompatActivity {

    private DataBase database;
    SQLiteDatabase db;
    static final int DIALOG_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produk);
        final Bundle b = getIntent().getExtras();
        Button btn = (Button)findViewById(R.id.backButtons);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Produk.this, Katalog.class);
                i.putExtras(b);
                startActivity(i);
            }
        });


        //Button btn3 = (Button)findViewById(R.id.simButton);
        //btn3.setOnClickListener(new View.OnClickListener() {
          //  @Override
            //public void onClick(View v) {
              //  startActivity(new Intent(Produk.this, Katalog.class));
            //}
        //});
        database=new DataBase(this);
        Button btn3 = (Button)findViewById(R.id.simButton);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText editText1 = (EditText) findViewById(R.id.namaP);
                EditText editText2 = (EditText) findViewById(R.id.hargaPP);
                EditText editText3 = (EditText) findViewById(R.id.hargaJP);

                String myEditText1=((TextView) editText1).getText().toString();
                String myEditText2=((TextView) editText2).getText().toString();
                String myEditText3=((TextView) editText3).getText().toString();
                String user=b.getString("UserName");
                db = database.getWritableDatabase();
                db.execSQL("insert into lProduk(namaProduk,hargaPokok,hargaJual,user) values ('" + editText1.getText().toString() + "', "+ editText2.getText().toString() + ", "+ editText3.getText().toString() + ", '"+user+"')");
                //this.db = new DataBase(this);
                //this.db.insert(myEditText1,myEditText2,myEditText3,myEditText4,myEditText5,myEditText6);
                Intent i = new Intent(Produk.this, Katalog.class);
                i.putExtras(b);
                startActivity(i);
                showDialog(DIALOG_ID);
                //break;
            }
        });

    }


}

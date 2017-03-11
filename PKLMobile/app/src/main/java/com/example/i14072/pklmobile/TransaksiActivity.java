package com.example.i14072.pklmobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class TransaksiActivity extends AppCompatActivity {
    private ArrayList<String> listProduk;
    private ArrayList<String> listId;
    private ArrayAdapter<String> adapter;
    private SharedPreferences sp;
    private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaksi);
        setTitle("TRANSAKSI PENJUALAN");
        sp=getSharedPreferences("data",MODE_PRIVATE);
        db=new DatabaseHandler(this);
        listId=db.selectProductName(sp.getInt("idUser",-1))[0];
        listProduk=db.selectProductName(sp.getInt("idUser",-1))[1];

        TextView tv=(TextView)findViewById(R.id.textViewTransaksi);
        tv.setText("Transaksi penjualan "+sp.getString("Name",""));

        adapter=new ArrayAdapter<String>(this,R.layout.list,listProduk);
        ListView lv=(ListView)findViewById(R.id.transList);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor e=sp.edit();
                e.putInt("TransaksiProd",Integer.parseInt(listId.get(position)));
                e.putString("TransaksiName",((TextView)view).getText().toString());
                e.commit();
                Intent i=new Intent(TransaksiActivity.this,JualActivity.class);
                startActivity(i);
            }
        });

        Button btnRekap=(Button)findViewById(R.id.btnRekap);
        btnRekap.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i=new Intent(TransaksiActivity.this,RekapActivity.class);
                startActivity(i);
            }
        });

        Button btnKatalog=(Button)findViewById(R.id.btnKatalog);
        btnKatalog.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i=new Intent(TransaksiActivity.this,KatalogActivity.class);
                startActivity(i);
            }
        });

        Button btnKeluar=(Button)findViewById(R.id.btnKeluar2);
        btnKeluar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i=new Intent(TransaksiActivity.this,SplashKeluarActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.action_katalog){
            Intent i=new Intent(TransaksiActivity.this,KatalogActivity.class);
            startActivity(i);
            return true;
        }
        else if(id==R.id.action_transaksi){
            Intent i=new Intent(TransaksiActivity.this,TransaksiActivity.class);
            startActivity(i);
            return true;
        }
        else if(id==R.id.action_rekap){
            Intent i=new Intent(TransaksiActivity.this,RekapActivity.class);
            startActivity(i);
            return true;
        }
        else if(id==R.id.action_produk){
            SharedPreferences.Editor e=sp.edit();
            e.remove("ProductName");
            e.remove("PName");
            e.commit();
            Intent i=new Intent(TransaksiActivity.this,ProdukActivity.class);
            startActivity(i);
            return true;
        }
        else if(id==R.id.action_logout){
            SharedPreferences.Editor e=sp.edit();
            e.remove("idUser");
            e.remove("Name");
            e.commit();
            Intent i=new Intent(TransaksiActivity.this,LoginActivity.class);
            startActivity(i);
            finish();
            return true;
        }
        else if(id==R.id.action_exit){
            Intent i=new Intent(TransaksiActivity.this,SplashKeluarActivity.class);
            startActivity(i);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

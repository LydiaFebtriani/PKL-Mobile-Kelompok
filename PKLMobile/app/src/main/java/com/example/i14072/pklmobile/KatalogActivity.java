package com.example.i14072.pklmobile;

import android.app.Activity;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class KatalogActivity extends AppCompatActivity {
    private ArrayList<String> listProduk;
    private ArrayList<String> listId;
    private ArrayAdapter<String> adapter;
    private SharedPreferences sp;
    private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_katalog);
        setTitle("KATALOG");
        sp=getSharedPreferences("data",MODE_PRIVATE);
        db=new DatabaseHandler(this);
        listId=db.selectProductName(sp.getInt("idUser",-1))[0];
        listProduk=(ArrayList<String>) db.getKatalogOnline(sp.getString("SessionId",""),sp.getInt("idUser",-1));

        TextView tv=(TextView)findViewById(R.id.textViewWelcome);
        tv.setText("Selamat datang: "+sp.getString("Name",""));

        adapter=new ArrayAdapter<String>(this,R.layout.list,listProduk);
        ListView lv=(ListView)findViewById(R.id.prodList);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor e=sp.edit();
                e.putInt("ProductName",Integer.parseInt(listId.get(position)));
                e.putString("PName",((TextView)view).getText().toString());
                e.commit();
                Intent i=new Intent(KatalogActivity.this,ProdukActivity.class);
                startActivity(i);
            }
        });

        Button btnTambah=(Button)findViewById(R.id.btnTambah);
        btnTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor e=sp.edit();
                e.remove("ProductName");
                e.remove("PName");
                e.commit();
                Intent i=new Intent(KatalogActivity.this,ProdukActivity.class);
                startActivity(i);
            }
        });

        Button btnTransaksi=(Button)findViewById(R.id.btnTransaksi);
        btnTransaksi.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i=new Intent(KatalogActivity.this,TransaksiActivity.class);
                startActivity(i);
            }
        });

        Button btnKeluar=(Button)findViewById(R.id.btnKeluar);
        btnKeluar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i=new Intent(KatalogActivity.this,SplashKeluarActivity.class);
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
            Intent i=new Intent(KatalogActivity.this,KatalogActivity.class);
            startActivity(i);
            return true;
        }
        else if(id==R.id.action_transaksi){
            Intent i=new Intent(KatalogActivity.this,TransaksiActivity.class);
            startActivity(i);
            return true;
        }
        else if(id==R.id.action_rekap){
            Intent i=new Intent(KatalogActivity.this,RekapActivity.class);
            startActivity(i);
            return true;
        }
        else if(id==R.id.action_produk){
            SharedPreferences.Editor e=sp.edit();
            e.remove("ProductName");
            e.remove("PName");
            e.commit();
            Intent i=new Intent(KatalogActivity.this,ProdukActivity.class);
            startActivity(i);
            return true;
        }
        else if(id==R.id.action_logout){
            db.logout(sp.getString("SessionId",""));
            Intent i=new Intent(KatalogActivity.this,LoginActivity.class);
            startActivity(i);
            finish();
            return true;
        }
        else if(id==R.id.action_exit){
            Intent i=new Intent(KatalogActivity.this,SplashKeluarActivity.class);
            startActivity(i);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

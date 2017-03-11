package com.pab.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pab.ui.database.DBManager;
import com.pab.ui.database.ListProduk;
import com.pab.ui.database.ProdukPKL;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Transaksi extends AppCompatActivity {

    private SharedPreferences sp;
    private SharedPreferences.Editor ed;
    private DBManager db;
    private ArrayAdapter adapter;
    private List<String> idProduk, productNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaksi);

        sp = getSharedPreferences("data", MODE_PRIVATE);
        ed = sp.edit();
        db = new DBManager(this);

        TextView title = (TextView)findViewById(R.id.titleTransaksi);
        String username = sp.getString("username", "");
        String tmp = title.getText().toString() + System.getProperty("line.separator") + username;
        title.setText(tmp);

        idProduk = db.getIdProduk(username);
        productNames = db.getNamaProduk(username);

        if (idProduk != null && productNames != null) {
            adapter = new ArrayAdapter<String>(this, R.layout.list_produk, productNames);
            ListView listView = (ListView) findViewById(R.id.listTransaksi);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ed.putString("id", idProduk.get(position) + "");
                    ed.putString("nama_produk", productNames.get(position));
                    ed.commit();

                    Intent i = new Intent(Transaksi.this, Jual.class);
                    startActivity(i);
                }
            });
        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.transaksi, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_rekap) {
            Toast.makeText(Transaksi.this, "Rekap", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(Transaksi.this, KategoriRekap.class);
            startActivity(i);
            return true;
        }
        else if (id == R.id.action_transaksiKatalog) {
            Toast.makeText(Transaksi.this, "Katalog", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(Transaksi.this, Katalog.class);
            startActivity(i);
            return true;
        }
        else if (id == R.id.action_transaksiKeluar) {
            Toast.makeText(Transaksi.this, "Keluar", Toast.LENGTH_SHORT).show();

            Intent i = new Intent(Transaksi.this, SplashKeluar.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

package com.example.i14072.pklmobile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ProdukActivity extends AppCompatActivity {
    private SharedPreferences sp;
    private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produk);
        sp=getSharedPreferences("data",MODE_PRIVATE);
        db=new DatabaseHandler(this);

        final TextView tv1=(TextView)findViewById(R.id.iNamaProduk);
        final TextView tv2=(TextView)findViewById(R.id.iHargaPokok);
        final TextView tv3=(TextView)findViewById(R.id.iHargaJual);
        final int idProduk=sp.getInt("ProductName",-1);
        if (idProduk<0) {
            setTitle("TAMBAH PRODUK");
            tv1.setText("");
            tv2.setText("");
            tv3.setText("");
        }
        else{
            setTitle("DETAIL PRODUK");
            String[] temp=db.getProductOnline(sp.getString("SessionId",""),sp.getString("PName",""),idProduk);
            tv1.setText(temp[0]);
            tv2.setText(temp[1]);
            tv3.setText(temp[2]);
        }

        Button btnTambah=(Button)findViewById(R.id.btnTambahProd);
        btnTambah.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                db.addUpdateProduct(sp.getInt("idUser",-1),sp.getString("SessionId",""),-1,tv1.getText().toString(),Integer.parseInt(tv2.getText().toString()),Integer.parseInt(tv3.getText().toString()),true);
                Intent i=new Intent(ProdukActivity.this,KatalogActivity.class);
                startActivity(i);
                finish();
            }
        });

        Button btnSimpan=(Button)findViewById(R.id.btnSimpanProd);
        btnSimpan.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String prodName=tv1.getText().toString();
                int price=Integer.parseInt(tv2.getText().toString());
                int sellPrice=Integer.parseInt(tv3.getText().toString());

                db.addUpdateProduct(sp.getInt("idUser",-1),sp.getString("SessionId",""),idProduk,prodName,price,sellPrice,false);
                Intent i=new Intent(ProdukActivity.this,KatalogActivity.class);
                startActivity(i);
                finish();
            }
        });

        Button btnKembali=(Button)findViewById(R.id.btnKembali);
        btnKembali.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i=new Intent(ProdukActivity.this,KatalogActivity.class);
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
            Intent i=new Intent(ProdukActivity.this,KatalogActivity.class);
            startActivity(i);
            return true;
        }
        else if(id==R.id.action_transaksi){
            Intent i=new Intent(ProdukActivity.this,TransaksiActivity.class);
            startActivity(i);
            return true;
        }
        else if(id==R.id.action_rekap){
            Intent i=new Intent(ProdukActivity.this,RekapActivity.class);
            startActivity(i);
            return true;
        }
        else if(id==R.id.action_produk){
            SharedPreferences.Editor e=sp.edit();
            e.remove("ProductName");
            e.remove("PName");
            e.commit();
            Intent i=new Intent(ProdukActivity.this,ProdukActivity.class);
            startActivity(i);
            return true;
        }
        else if(id==R.id.action_logout){
            SharedPreferences.Editor e=sp.edit();
            e.remove("idUser");
            e.remove("Name");
            e.commit();
            Intent i=new Intent(ProdukActivity.this,LoginActivity.class);
            startActivity(i);
            finish();
            return true;
        }
        else if(id==R.id.action_exit){
            Intent i=new Intent(ProdukActivity.this,SplashKeluarActivity.class);
            startActivity(i);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

package com.example.i14072.pklmobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Iterator;
import java.util.List;

public class RekapActivity extends AppCompatActivity {
    private DatabaseHandler db;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rekap);
        setTitle("REKAP PENJUALAN");
        sp=getSharedPreferences("data",MODE_PRIVATE);
        db=new DatabaseHandler(this);

        TableLayout tl=(TableLayout)findViewById(R.id.tblRekap);
        List<String[]> listRekap=db.getTransactionOnline(sp.getString("SessionId",""),sp.getInt("idUser",-1),"");
        int totalTransaksi=0;
        Iterator<String[]> i=listRekap.iterator();
        while(i.hasNext()){
            TableRow row=new TableRow(this);
            String[] rowData=i.next();

            TextView namaProd=new TextView(this);
            namaProd.setPadding(5,5,5,5);
            namaProd.setText(rowData[0]);
            row.addView(namaProd);

            TextView jumlahJual=new TextView(this);
            jumlahJual.setPadding(5,5,5,5);
            jumlahJual.setText(rowData[2]);
            row.addView(jumlahJual);

            TextView hargaJual=new TextView(this);
            hargaJual.setPadding(5,5,5,5);
            hargaJual.setText(rowData[1]);
            row.addView(hargaJual);

            TextView totalHarga=new TextView(this);
            int hargaTotal=Integer.parseInt(rowData[2])*Integer.parseInt(rowData[1]);
            totalHarga.setPadding(5,5,5,5);
            totalHarga.setText(hargaTotal+"");
            row.addView(totalHarga);
            totalTransaksi+=hargaTotal;

            tl.addView(row);
        }

        TableRow total=new TableRow(this);
        total.setLayoutParams(new TableRow.LayoutParams());
        total.setPadding(0,20,0,0);
        TextView label=new TextView(this);
        label.setPadding(5,5,5,5);
        label.setText("Total transaksi");
        label.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        label.setTypeface(Typeface.DEFAULT_BOLD);
        label.setLayoutParams(new TableRow.LayoutParams());
        ((TableRow.LayoutParams)label.getLayoutParams()).span=3;
        total.addView(label);
        tl.addView(total);

        TextView lblPenjualan=new TextView(this);
        lblPenjualan.setPadding(5,5,5,5);
        lblPenjualan.setText(""+totalTransaksi);
        total.addView(lblPenjualan);

        Button btnTransaksi=(Button)findViewById(R.id.btnTransaksi);
        btnTransaksi.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i=new Intent(RekapActivity.this,TransaksiActivity.class);
                startActivity(i);
            }
        });

        Button btnKatalog=(Button)findViewById(R.id.btnKatalog);
        btnKatalog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(RekapActivity.this,KatalogActivity.class);
                startActivity(i);
            }
        });

        Button btnKeluar=(Button)findViewById(R.id.btnKeluar);
        btnKeluar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i=new Intent(RekapActivity.this,SplashKeluarActivity.class);
                startActivity(i);
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
            Intent i=new Intent(RekapActivity.this,KatalogActivity.class);
            startActivity(i);
            return true;
        }
        else if(id==R.id.action_transaksi){
            Intent i=new Intent(RekapActivity.this,TransaksiActivity.class);
            startActivity(i);
            return true;
        }
        else if(id==R.id.action_rekap){
            Intent i=new Intent(RekapActivity.this,RekapActivity.class);
            startActivity(i);
            return true;
        }
        else if(id==R.id.action_produk){
            SharedPreferences.Editor e=sp.edit();
            e.remove("ProductName");
            e.remove("PName");
            e.commit();
            Intent i=new Intent(RekapActivity.this,ProdukActivity.class);
            startActivity(i);
            return true;
        }
        else if(id==R.id.action_logout){
            SharedPreferences.Editor e=sp.edit();
            e.remove("idUser");
            e.remove("Name");
            e.commit();
            Intent i=new Intent(RekapActivity.this,LoginActivity.class);
            startActivity(i);
            finish();
            return true;
        }
        else if(id==R.id.action_exit){
            Intent i=new Intent(RekapActivity.this,SplashKeluarActivity.class);
            startActivity(i);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

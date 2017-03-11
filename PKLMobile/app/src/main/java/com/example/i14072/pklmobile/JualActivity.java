package com.example.i14072.pklmobile;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class JualActivity extends AppCompatActivity {
    private SharedPreferences sp;
    private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jual);
        setTitle("DETAIL TRANSAKSI PENJUALAN");
        sp=getSharedPreferences("data",MODE_PRIVATE);
        db=new DatabaseHandler(this);

        TextView tv1=(TextView)findViewById(R.id.iNamaProduk);
        final EditText tv2=(EditText)findViewById(R.id.iHargaJual);
        final EditText ed=(EditText)findViewById(R.id.iQtyJual);
        final int idProd=sp.getInt("TransaksiProd",-1);
        final String[] temp=db.getProductOnline(sp.getString("SessionId",""),sp.getString("TransaksiName",""),idProd);
        tv1.setText(temp[0]);
        tv2.setText(temp[2]);

        Button btnProses=(Button)findViewById(R.id.btnProses);
        btnProses.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                SimpleDateFormat formatter=new SimpleDateFormat("yyyyMMdd");
                Calendar cal=Calendar.getInstance();
                int qty=Integer.parseInt(ed.getText().toString());
                db.addTransactionOnline(sp.getString("SessionId",""),idProd,sp.getString("TransaksiName",""),Integer.parseInt(tv2.getText().toString()),qty,formatter.format(cal.getTime()));
                int totalHarga=qty*Integer.parseInt(tv2.getText().toString());
                new AlertDialog.Builder(JualActivity.this)
                        .setTitle("TRANSAKSI BERHASIL")
                        .setMessage("Terima kasih Anda telah bertransaksi produk '"+temp[0]+"' sebanyak "+qty+" seharga "+totalHarga)
                        .setPositiveButton("Ok",new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i=new Intent(JualActivity.this,TransaksiActivity.class);
                                startActivity(i);
                                finish();
                            }
                        })
                        .show();
            }
        });

        Button btnBatal=(Button)findViewById(R.id.btnBatal);
        btnBatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(JualActivity.this,TransaksiActivity.class);
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
            Intent i=new Intent(JualActivity.this,KatalogActivity.class);
            startActivity(i);
            return true;
        }
        else if(id==R.id.action_transaksi){
            Intent i=new Intent(JualActivity.this,TransaksiActivity.class);
            startActivity(i);
            return true;
        }
        else if(id==R.id.action_rekap){
            Intent i=new Intent(JualActivity.this,RekapActivity.class);
            startActivity(i);
            return true;
        }
        else if(id==R.id.action_produk){
            SharedPreferences.Editor e=sp.edit();
            e.remove("ProductName");
            e.remove("PName");
            e.commit();
            Intent i=new Intent(JualActivity.this,ProdukActivity.class);
            startActivity(i);
            return true;
        }
        else if(id==R.id.action_logout){
            SharedPreferences.Editor e=sp.edit();
            e.remove("idUser");
            e.remove("Name");
            e.commit();
            Intent i=new Intent(JualActivity.this,LoginActivity.class);
            startActivity(i);
            finish();
            return true;
        }
        else if(id==R.id.action_exit){
            Intent i=new Intent(JualActivity.this,SplashKeluarActivity.class);
            startActivity(i);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

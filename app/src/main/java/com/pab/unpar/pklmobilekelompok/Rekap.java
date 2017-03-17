package com.pab.unpar.pklmobilekelompok;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

public class Rekap extends AppCompatActivity {
//    private DataManipulator dh;
    private SharedPreferences sp;
//    private int idUser;
    private int totalTransaksi;
    private String sessionId;

    //TAMBAHAN PENGELOMPOKKAN REKAP
    private int idxBulan=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rekap);
        sp=getSharedPreferences("dataProduk",MODE_PRIVATE);
        this.sessionId = sp.getString("sessionId","");

        //<TAMBAHAN PENGELOMPOKKAN REKAP>
        Spinner s=(Spinner)findViewById(R.id.filterRekap);
        s.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                idxBulan=position;
                printRekap();
            }
        });
        //</TAMBAHAN PENGELOMPOKKAN REKAP>

        Log.d("Rekap","Sebelum print rekap");
        printRekap();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void printRekap(){
//        dh = new DataManipulator(this);
//        List<String[]> list = dh.selectAllTransaksi(this.idUser);
        Soap soap = new Soap();
        Log.d("Rekap","Sebelum rekap");
        //<BUAT PENGELOPOKKAN REKAP, BELUM DICOBA>
        ArrayList<String[]> list = soap.getRekap(sessionId,idxBulan);
        Log.d("Rekap","Setelah rekap");

        TableRow row;
        TableLayout tl = (TableLayout) findViewById(R.id.rekapTable);
        tl.removeAllViews();
        //</PENGELOMPOKKAN REKAP>
        tl.setGravity(Gravity.CENTER);

        for(int i=0;i<list.size();i++){
//            //Cari produk
//            String[] produkInfo = dh.select1FromProduk(Integer.parseInt(list.get(i)[2]));
            String nama = list.get(i)[0];
            String harga = list.get(i)[1];
            String kuantitas = list.get(i)[2];
            String tglJual = list.get(i)[3];
            Log.d("Rekap list",i+" "+nama+" "+harga+" "+kuantitas+" "+tglJual);

            TextView no = new TextView(this);
            TextView tgl = new TextView(this);
            TextView info = new TextView(this);
            TextView qty = new TextView(this);
            TextView total = new TextView(this);

            int hargaTotal = Integer.parseInt(harga)*Integer.parseInt(kuantitas);

            row = new TableRow(this);

            no.setText(""+(i+1)+".");
            //no.setPadding(0,5,25,5);
            no.setWidth(30);
            no.setTextSize(16);
            row.addView(no);

            tgl.setText(tglJual);
            //tgl.setPadding(0,5,25,5);
            tgl.setWidth(150);
            tgl.setTextSize(16);
            row.addView(tgl);

            info.setText(nama);
            //info.setPadding(0,5,50,5);
            info.setWidth(200);
            info.setTextSize(16);
            row.addView(info);

            qty.setText(kuantitas+" x "+harga);
            //qty.setPadding(0,5,0,5);
            qty.setWidth(150);
            qty.setTextSize(16);
            row.addView(qty);

            total.setText(hargaTotal+"");
            //total.setPadding(20,5,0,5);
            total.setWidth(120);
            total.setTextSize(16);
            total.setGravity(Gravity.RIGHT);
            row.addView(total);

            totalTransaksi += hargaTotal;

            tl.addView(row);
        }
        TextView viewTotalTransaksi = (TextView) findViewById(R.id.totalTransaksi);
        viewTotalTransaksi.setText(this.totalTransaksi+"");
    }
}
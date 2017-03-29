package com.pab.unpar.pklmobilekelompok;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
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
    private int idUser;
    private int totalTransaksi;
    private String sessionId;

    //TAMBAHAN PENGELOMPOKKAN REKAP
    private int idxBulan=0;

    private SensorData sensorData;
    SharedPreferences.Editor ed;

    private TextView viewTotalTransaksi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sp = getSharedPreferences("dataProduk", MODE_PRIVATE);
        sensorData = new SensorData(this,(SensorManager)getSystemService(Context.SENSOR_SERVICE));

        Utils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_rekap);

        viewTotalTransaksi = (TextView) findViewById(R.id.totalTransaksi);

        sp=getSharedPreferences("dataProduk",MODE_PRIVATE);
        ed = this.sp.edit();

        sessionId = sp.getString("sessionId","");
        idUser = Integer.parseInt(sp.getString("idUser",""));
        Connect con = new Connect();
        if(con.checkConnection(this)){
            //Ada koneksi
            Soap soap = new Soap();
            if(sessionId == ""){
                String[] id = soap.login(this,sp.getString("user",""),sp.getString("password",""));
                if(id[0] != null){
                    SharedPreferences.Editor ed = sp.edit();
                    ed.putString("sessionId", id[0]);
                    ed.commit();
                    sessionId = sp.getString("sessionId","");
                }
            }
            soap.sync(this,sessionId,idUser);
        }
//        con.sync(this);
//        if(!sp.getString("sessionId","").isEmpty()){
//            this.sessionId = sp.getString("sessionId","");
//        } else{
//            this.sessionId = sp.getString("idUser","");
//        }
        /*Log.d("Session Katalog",sp.getString("sessionId","").isEmpty()+"");
        if(sp.getString("sessionId","").isEmpty()){
            Connect con = new Connect();
            String temp = con.loginServer(this,sp.getString("user",""),sp.getString("password",""));
            Log.d("Katalog temp",temp);
            if(temp != ""){
                //Kalau bisa login
                ed = sp.edit();
                ed.putString("sessionId", sessionId);
                ed.commit();
                this.sessionId = sp.getString("sessionId","");
                Log.d("Connect di Katalog", sp.getString("sessionId",""));
            }
            this.sessionId = sp.getString("idUser","");
        }
        else{
            this.sessionId = sp.getString("sessionsessionId","");
        }*/

        //<TAMBAHAN PENGELOMPOKKAN REKAP>
        Spinner s=(Spinner)findViewById(R.id.filterRekap);
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                idxBulan=position;
                printRekap();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                idxBulan=0;
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
        totalTransaksi = 0;
        Soap soap = new Soap();
        Log.d("Rekap","Sebelum rekap");
        //<BUAT PENGELOPOKKAN REKAP, BELUM DICOBA>
        ArrayList<String[]> list = soap.getAllRekap(this,sessionId,idxBulan);
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
        viewTotalTransaksi.setText(this.totalTransaksi+"");
    }
}
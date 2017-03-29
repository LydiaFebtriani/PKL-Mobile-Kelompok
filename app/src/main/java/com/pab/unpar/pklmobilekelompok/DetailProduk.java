package com.pab.unpar.pklmobilekelompok;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class DetailProduk extends AppCompatActivity implements View.OnClickListener{
//    private DataManipulator dh;
    private SharedPreferences sp;
    private SharedPreferences.Editor ed;
    private int idUser;
    private String sessionId;
    private String[] produk;
    private Soap soap = new Soap();
    private SensorData sensorData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sp = getSharedPreferences("dataProduk", MODE_PRIVATE);
        sensorData = new SensorData(this,(SensorManager)getSystemService(Context.SENSOR_SERVICE));

        Utils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_detail_produk);

        ed = this.sp.edit();

        sessionId = sp.getString("idUser","");
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
            sessionId = sp.getString("sessionId","");
            soap.sync(this,sessionId,idUser);
        }
//        Log.d("Session Katalog",sp.getString("sessionId","").isEmpty()+"");
//        if(sp.getString("sessionId","").isEmpty()){
//            Connect con = new Connect();
//            String temp = con.loginServer(this,sp.getString("user",""),sp.getString("password",""));
//            Log.d("Katalog temp",temp);
//            if(temp != ""){
//                //Kalau bisa login
//                ed = sp.edit();
//                ed.putString("sessionId", sessionId);
//                ed.commit();
//                this.sessionId = sp.getString("sessionId","");
//                Log.d("Connect di Katalog", sp.getString("sessionId",""));
//            }
//            this.sessionId = sp.getString("idUser","");
//        }
//        else{
//            this.sessionId = sp.getString("sessionsessionId","");
//        }
//        this.sessionId = sp.getString("sessionId","");

        Button batal = (Button) findViewById(R.id.buttonBatal);
        batal.setOnClickListener(this);
        Button simpan = (Button) findViewById(R.id.buttonSimpan);
        simpan.setOnClickListener(this);

        viewDetail();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void viewDetail(){
//        dh = new DataManipulator(this);
//        String[] list = dh.select1FromProduk(sp.getInt("idProduk",-1));
        produk = soap.getDetailProduk(this,sessionId,sp.getString("produk",""));

        EditText nama = (EditText) findViewById(R.id.detailNamaProduk);
        EditText hargaPokok = (EditText) findViewById(R.id.detailHargaPokok);
        EditText hargaJual = (EditText) findViewById(R.id.detailHargaJual);
        nama.setText(produk[0]);
        hargaPokok.setText(produk[1]);
        hargaJual.setText(produk[2]);
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()){
            case R.id.buttonBatal:
                sensorData.unregisterSensor();
                i = new Intent(DetailProduk.this, Katalog.class);
                startActivity(i);
                finish();
                break;
            case R.id.buttonSimpan:
                sensorData.unregisterSensor();
                EditText nama = (EditText) findViewById(R.id.detailNamaProduk);
                EditText hargaPokok = (EditText) findViewById(R.id.detailHargaPokok);
                EditText hargaJual = (EditText) findViewById(R.id.detailHargaJual);

//                dh = new DataManipulator(this);
//                dh.update1FromProduk(sp.getInt("idProduk",-1),nama.getText().toString(),hargaPokok.getText().toString(),hargaJual.getText().toString(),this.idUser);
                boolean res = soap.setAddProduk(this,sessionId,idUser,nama.getText().toString(),hargaPokok.getText().toString(),hargaJual.getText().toString());
                Log.d("Update produk",res+"");
                if(res) {
                    Toast.makeText(getApplicationContext(), "Produk berhasil diupdate", Toast.LENGTH_SHORT).show();
                    sensorData.unregisterSensor();
                    i = new Intent(DetailProduk.this, Katalog.class);
                    startActivity(i);
                    finish();
                }
                break;
        }
    }
}

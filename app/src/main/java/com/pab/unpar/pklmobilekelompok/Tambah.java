package com.pab.unpar.pklmobilekelompok;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Tambah extends AppCompatActivity implements View.OnClickListener{
//    private DataManipulator dh;
    private SharedPreferences sp;
    private int idUser;
    private String sessionId;
    private SensorData sensorData;
    SharedPreferences.Editor ed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sp = getSharedPreferences("dataProduk", MODE_PRIVATE);
        sensorData = new SensorData(this,(SensorManager)getSystemService(Context.SENSOR_SERVICE));

        Utils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_tambah);

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

        Button batal = (Button) findViewById(R.id.buttonBatal);
        Button simpan = (Button) findViewById(R.id.buttonSimpan);
        simpan.setOnClickListener(this);
        batal.setOnClickListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void onClick(View view){
        Intent i;
        switch (view.getId()){
            case R.id.buttonBatal:
                sensorData.unregisterSensor();
                i = new Intent(Tambah.this, Katalog.class);
                startActivity(i);
                finish();
                break;
            case R.id.buttonSimpan:
                View inputNama = (EditText) findViewById(R.id.detailNamaProduk);
                View inputHargaPokok = (EditText) findViewById(R.id.detailHargaPokok);
                View inputHargaJual = (EditText) findViewById(R.id.detailHargaJual);

                String nama = ((TextView) inputNama).getText().toString();
                String hargaPokok = ((TextView) inputHargaPokok).getText().toString();
                String hargaJual = ((TextView) inputHargaJual).getText().toString();

//                dh = new DataManipulator(this);
//                dh.insertProduk(nama,hargaPokok,hargaJual,this.idUser);

                Soap soap = new Soap();
                boolean res = soap.setAddProduk(this,sessionId,idUser,nama,hargaPokok,hargaJual);
                if(res){
                    Toast.makeText(getApplicationContext(),"Produk berhasil ditambahkan!",Toast.LENGTH_SHORT).show();
                    sensorData.unregisterSensor();
                    i = new Intent(Tambah.this, Katalog.class);
                    startActivity(i);
                    finish();
                }
                break;
        }
    }
}
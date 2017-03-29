package com.pab.unpar.pklmobilekelompok;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

public class Home extends AppCompatActivity {

    private SharedPreferences sp;
    private SensorData sensorData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sp = getSharedPreferences("dataProduk", MODE_PRIVATE);
        sensorData = new SensorData(this,(SensorManager)getSystemService(Context.SENSOR_SERVICE));

        Utils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_home);

        SharedPreferences.Editor ed = sp.edit();

        Connect con = new Connect();
        if(con.checkConnection(this)){
            Soap soap = new Soap();
            if(sp.getString("sessionId","") == ""){
                String[] id = soap.login(this,sp.getString("user",""),sp.getString("password",""));
                if(id[0] != null){
                    ed.putString("sessionId", id[0]);
                    ed.commit();
                }
            }
            soap.sync(this,sp.getString("sessionId",""),Integer.parseInt(sp.getString("idUser","")));
        }
        /*String sessionId = null;
        int idUser = -1;
        if(!sp.getString("sessionId","").isEmpty()){
            sessionId = sp.getString("sessionId","");
        } else{
            ed.putString("sessionId",sessionId);
            ed.commit();
        }
        if(!sp.getString("idUser","").isEmpty()){
            idUser = Integer.parseInt(sp.getString("idUser",""));
        } else{
            ed.putString("idUser",idUser+"");
            ed.commit();
        }
        Log.d("Session dan idUser",sessionId+" "+idUser);
        con.sync(this,sessionId,idUser);
//        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo info = cm.getNetworkInfo(cm.TYPE_MOBILE);
//        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
//        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
//                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED){
//            Soap soap = new Soap();
//            String sessionId = null;
//            int idUser = -1;
//            if(!sp.getString("sessionId","").isEmpty()){
//                sessionId = sp.getString("sessionId","");
//            }
//            if(!sp.getString("idUser","").isEmpty()){
//                idUser = Integer.parseInt(sp.getString("idUser",""));
//            }
//            soap.sync(this,sessionId,idUser);
//        }
//        if(info!=null && info.isConnected()){
//            Soap soap = new Soap();
//            String sessionId = null;
//            int idUser = -1;
//            if(!sp.getString("sessionId","").isEmpty()){
//                sessionId = sp.getString("sessionId","");
//            }
//            if(!sp.getString("idUser","").isEmpty()){
//                idUser = Integer.parseInt(sp.getString("idUser",""));
//            }
//            soap.sync(this,sessionId,idUser);
//        }
*/
        Button katalog = (Button) findViewById(R.id.katalogButton);
        katalog.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0){
                sensorData.unregisterSensor();
                Intent i = new Intent(Home.this, Katalog.class);
                startActivity(i);
            }
        });

        Button transaksi = (Button) findViewById(R.id.transaksiButton);
        transaksi.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0){
                sensorData.unregisterSensor();
                Intent i = new Intent(Home.this, Transaksi.class);
                startActivity(i);
            }
        });
        
        Button tmbhProduk = (Button) findViewById(R.id.tmbhProdukButton);
        tmbhProduk.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0){
                sensorData.unregisterSensor();
                Intent i = new Intent(Home.this, Tambah.class);
                startActivity(i);
            }
        });

        Button rekap = (Button) findViewById(R.id.rekapButton);
        rekap.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0){
                sensorData.unregisterSensor();
                Intent i = new Intent(Home.this, Rekap.class);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.logout){
            SharedPreferences sp=getSharedPreferences("dataProduk",MODE_PRIVATE);
            String sessionId = sp.getString("sessionId","");
            Soap soap = new Soap();
            if(soap.logout(this,sessionId)){
                sensorData.unregisterSensor();
                Intent i = new Intent(Home.this, Login.class);
                startActivity(i);
                finish();
            }
            else{
                Toast.makeText(getApplicationContext(),"Maaf, proses logout gagal!\nSilahkan coba beberapa saat lagi!",Toast.LENGTH_SHORT).show();
            }
        }
        else if(item.getItemId() == R.id.keluar){
            sensorData.unregisterSensor();
            Intent i = new Intent(Home.this, SplashKeluar.class);
            startActivity(i);
            finish();
        }
        else if(item.getItemId() == R.id.settings){
            sensorData.unregisterSensor();
            Intent i = new Intent(Home.this, Pengaturan.class);
            startActivity(i);
            finish();
        }
        return true;
    }

}

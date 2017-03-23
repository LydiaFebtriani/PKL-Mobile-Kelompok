package com.pab.unpar.pklmobilekelompok;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

public class SplashKeluar extends Activity {
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_keluar);

        sp=getSharedPreferences("dataProduk",MODE_PRIVATE);
        Connect con = new Connect();
        if(con.checkConnection(this)){
            //Ada koneksi
            Soap soap = new Soap();
            soap.sync(this,sp.getString("sessionId",""),Integer.parseInt(sp.getString("idUser","")));
        }
//        Connect con = new Connect();
//        String sessionId = null;
//        int idUser = -1;
//        if(sp.getString("sessionId","") != null){
//            sessionId = sp.getString("sessionId","");
//        }
//        if(Integer.parseInt(sp.getString("idUser","")) != -1){
//            idUser = Integer.parseInt(sp.getString("idUser",""));
//        }
//        con.sync(this,sessionId,idUser);
        /*Soap soap = new Soap();
        DataManipulator dh = new DataManipulator(this);
        if(!sp.getString("sessionId","").isEmpty()){
            //Kalau uda konek sebelumnya
            String[] pkl = soap.getPklOnline(sp.getString("sessionId",""));
            if(!sp.getString("idUser","").isEmpty()){
                //Kalau user sudah masuk ke database
                soap.syncData(this,sp.getString("sessionId",""),Integer.parseInt(sp.getString("idUser","")));
            }
            else{
                dh.insertUser(pkl[0],pkl[4],pkl[1],pkl[2],pkl[3],pkl[4],pkl[5],true);
                String idUser = (dh.select1User(new String[]{"nama = \""+pkl[1]+"\""}))[0];
                soap.syncData(this,sp.getString("sessionId",""),Integer.parseInt(idUser));
            }
        }else{
            //Kalau belum konek, coba konekin lagi
            String conRes = con.loginServer(this,sp.getString("user",""),sp.getString("password",""));
            if(conRes != ""){
                //Bisa konek
                Log.d("Sync Keluar", conRes);
                soap.syncData(this,conRes,Integer.parseInt(sp.getString("idUser","")));
            }
            else{
//                dh.select1User()
//                soap.register(this,);
            }
        }*/

        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(3000);
                } catch(InterruptedException e){
                    e.printStackTrace();
                } finally{
                    Intent i =new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                    System.exit(0);
                }
            }
        };
        timerThread.start();
    }

    @Override
    protected void onPause(){
        super.onPause();
        finish();
    }
}

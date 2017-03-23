package com.pab.unpar.pklmobilekelompok;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by LENOVO Z410_W8PRO on 3/22/2017.
 */

public class Connect {
    private SharedPreferences sp;
    /**
     * Method untuk mencoba connect ke server
     * Kalau connect akan langsung login ke server
     * @param user email user
     * @param password password user
     * @return sessionId jika connect, "" jika tidak
     */
    public String loginServer(Context context, String user, String password){
        Soap soap = new Soap();
        String[] id =soap.login(context,user,password);
        if(id[0] != null) {
            if (id.length == 1) {
                return id[0];
            }
            else{
                return "";
            }
        }
        else{
            return "";
        }
    }
    public void sync(Context context,String sessionId,int idUser){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED){
            Soap soap = new Soap();
//            String sId = null;
//            int idU = -1;
//            if(sessionId != null){
//                sessionId = sp.getString("sessionId","");
//            }
//            if(idUser != -1){
//                idUser = Integer.parseInt(sp.getString("idUser",""));
//            }
            soap.sync(context,sessionId,idUser);
        }
    }
    public boolean checkConnection(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        }
        return false;
    }
}

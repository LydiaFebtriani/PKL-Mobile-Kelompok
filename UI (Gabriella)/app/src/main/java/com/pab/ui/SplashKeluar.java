package com.pab.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class SplashKeluar extends AppCompatActivity {


    String NAMESPACE = "http://schemas.xmlsoap.org/wsdl";
    String URL = "http://webtest.unpar.ac.id/pklws/pkl.php?wsdl";

    String SOAP_ACTION = "logout";
    String METHOD_NAME = "logout";

    SoapSerializationEnvelope envelope;

    private SharedPreferences sp;
    private String sid;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_splashkeluar);

        sp = getSharedPreferences("data", MODE_PRIVATE);
        sid = sp.getString("sid", "");

        processWebView();

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent i = new Intent(Intent.ACTION_MAIN)
                        .addCategory(Intent.CATEGORY_HOME)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
                System.exit(0);
            }
        }, 3000);
    }
    public void processWebView() {
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        request.addProperty("sid", sid);

        envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);

        NetworkHandler handler = new NetworkHandler();
        handler.execute();
    }



    private class NetworkHandler extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... paramas) {
            boolean connected = true;
            try {
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
                androidHttpTransport.call(SOAP_ACTION, envelope);
            } catch (Exception e) {
                e.printStackTrace();
                connected = false;
            }
            return connected;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            boolean connected = aBoolean;
            if (connected) {
                SoapObject resultRequestSOAP = (SoapObject)envelope.bodyIn;
            } else {
                Log.d("ERROR ", "Connection Error");
            }
        }


    }

}

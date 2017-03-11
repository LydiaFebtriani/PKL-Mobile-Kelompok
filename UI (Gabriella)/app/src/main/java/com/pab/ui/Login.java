package com.pab.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pab.ui.database.DBManager;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class Login extends AppCompatActivity {

	String NAMESPACE = "http://schemas.xmlsoap.org/wsdl";
	String URL = "http://webtest.unpar.ac.id/pklws/pkl.php?wsdl";
	
	String SOAP_ACTION = "login";
	String METHOD_NAME = "login";

    SoapSerializationEnvelope envelope;

    private EditText username, password;
    private SharedPreferences sp;
    private SharedPreferences.Editor ed;
    private DBManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = new DBManager(this);
        username = (EditText)findViewById(R.id.editTextUserName);
        password = (EditText)findViewById(R.id.editTextPassword);
        sp = getSharedPreferences("data", MODE_PRIVATE);
        ed = sp.edit();

        Button btnLogin = (Button) findViewById(R.id.buttonLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (username.getText().toString().length() == 0 || password.getText().toString().length() == 0) {
                    if (username.getText().toString().length() == 0) {
                        username.setError("Username harus diisi");
                    } else {
                        password.setError("Password harus diisi");
                    }
                } else {
                    if (db.isEmailExist(username.getText().toString())) {
                        if(db.getTanggalLahir(username.getText().toString())
                                .equalsIgnoreCase(password.getText().toString())) {

                            ed.putString("username", username.getText().toString());
                            ed.commit();

                            processWebView();

                            Toast.makeText(getApplicationContext(), "Login berhasil ...",
                                    Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(Login.this, Katalog.class);
                            startActivity(i);
                        } else {
                            password.setError("Password anda salah");
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Mendaftarkan penguna ...",
                                Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(Login.this, Registrasi.class);
                        startActivity(i);
                    }
                }

            }
        });

        Button btnRegister = (Button) findViewById(R.id.buttonRegistrasi);
        btnRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Toast.makeText(getApplicationContext(), "Registrasi pengguna ...",
                        Toast.LENGTH_SHORT).show();
                Intent i = new Intent(Login.this, Registrasi.class);
                startActivity(i);
            }
        });

    }

    public void processWebView() {
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        request.addProperty("user", username.getText().toString());
        request.addProperty("password", password.getText().toString());

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
                processLogin(resultRequestSOAP);
            } else {
                Log.d("ERROR ", "Connection Error");
            }
        }

        private void processLogin(SoapObject resultRequestSOAP) {
            String property[] = resultRequestSOAP.getProperty(0).toString().
                    replaceAll("\\(", "").replaceAll("\\)", "").
                    replaceAll("\"", "").split(",");

            Log.d("SID", property[1]);

            ed.putString("sid", property[1]);
            ed.commit();
        }

    }

}


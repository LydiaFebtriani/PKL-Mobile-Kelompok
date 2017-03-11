package com.example.i14059.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity {
    EditText edUserName;
    EditText edPassword;
    DataBase db;

    String NAMESPACE = "http://schemas.xmlsoap.org/wsdl";
    String URL = "http://webtest.unpar.ac.id/pklws/pkl.php?wsdl";

    String SOAP_ACTION = "login";
    String METHOD_NAME = "login";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db = new DataBase(this);

        edUserName = (EditText) findViewById(R.id.editTextUserName);
        edUserName.setText("");
        edPassword = (EditText) findViewById(R.id.editTextPassword);
        edPassword.setText("");
        Button btnLogin = (Button) findViewById(R.id.buttonLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v){

                SoapObject request = new SoapObject(NAMESPACE,METHOD_NAME);

                request.addProperty("User",edUserName.getText().toString());
                request.addProperty("Password",edPassword.getText().toString());

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvlope.VER11);

                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

                try{
                    androidHttpTransport.call(SOAP_ACTION,envelope);
                    SoapOnject resultsRequestSOAP = (SoapObject) envelope.bodyIn();
                    //textview01.setText(resultsRequestSOAP.toString());
                }catch(Exception e){
                    e.printStackTrace();
                }

                if (v.getId()==R.id.buttonLogin){
                    String str=edUserName.getText().toString();
                    String pass=edPassword.getText().toString();
                    String password=db.searchPass(str);

                    if (pass.equals(password)){
                        Toast.makeText(getApplicationContext(), "Login berhasil ...", Toast.LENGTH_SHORT).show();
                        Intent i=new Intent(Login.this, Katalog. class);
                        Bundle b = new Bundle();
                        b.putString("UserName", edUserName.getText().toString());
                        i.putExtras(b);
                        startActivity(i);
                    }
                    else{
                        Toast.makeText(Login.this, "Username atau password salah!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        Button bReg = (Button) findViewById(R.id.buttonRegistrasi);
        bReg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                onButtonRegist(v);
            }
        });
    }




    public void onButtonRegist(View v){
        Intent newAct = new Intent(this, Registrasi.class);
        startActivity(newAct);
    }
    }



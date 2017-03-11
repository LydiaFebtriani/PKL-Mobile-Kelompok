package com.pab.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.pab.ui.database.DBManager;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Registrasi extends AppCompatActivity {

    String NAMESPACE = "http://schemas.xmlsoap.org/wsdl";
    String URL = "http://webtest.unpar.ac.id/pklws/pkl.php?wsdl";

    String SOAP_ACTION = "regpkl";
    String METHOD_NAME = "regpkl";

    SoapSerializationEnvelope envelope;

    private EditText email, nama, alamat, noHP, produkUnggul;
    private DatePicker tglLahir;
    private boolean allFilled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrasi);

        Button btnBatal = (Button) findViewById(R.id.buttonBatal);
        btnBatal.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(Registrasi.this, Login.class);
                startActivity(i);
            }
        });

        Button btnSimpan = (Button) findViewById(R.id.buttonSimpan);
        btnSimpan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                email = (EditText)findViewById(R.id.editEmail);
                nama = (EditText)findViewById(R.id.editNama);
                alamat = (EditText)findViewById(R.id.editLapak);
                noHP = (EditText)findViewById(R.id.editHP);
                produkUnggul = (EditText)findViewById(R.id.editProduk);

                if( (email.getText().toString().length() == 0) || (nama.getText().toString().length() == 0)
                        || (alamat.getText().toString().length() == 0) || noHP.getText().toString().length() == 0
                        || (produkUnggul.getText().toString().length() == 0)) {
                    if (email.getText().toString().length() == 0) {
                        email.setError("Alamat email tidak boleh kosong");
                    } else if (nama.getText().toString().length() == 0) {
                        nama.setError("Nama lengkap tidak boleh kosong");
                    } else if (alamat.getText().toString().length() == 0) {
                        alamat.setError("Alamat lapak tidak boleh kosong");
                    } else if (noHP.getText().toString().length() == 0) {
                        noHP.setError("Nomor HP tidak boleh kosong");
                    } else {
                        produkUnggul.setError("Produk unggul tidak boleh kosong");
                    }
                } else {
                    DBManager db = new DBManager(Registrasi.this);
                    if (db.isEmailExist(email.getText().toString())) {
                        email.setError("Email ini telah terdaftar");
                    } else {
                        tglLahir = (DatePicker)findViewById(R.id.datePicker);
                        String tanggalLahir = convertToString(tglLahir);

                        allFilled = true;

                        db.insertPengguna(email.getText().toString(), nama.getText().toString(),
                                alamat.getText().toString(), noHP.getText().toString(),
                                tanggalLahir, produkUnggul.getText().toString());

                        processWebService();

                        AlertDialog alertDialog = new AlertDialog.Builder(Registrasi.this).create();
                        alertDialog.setTitle("REGISTRASI BERHASIL");
                        alertDialog.setMessage("Selamat anda telah terdaftar, " +
                                "silakan login untuk menggunakan aplikasi ini! Saat login, " +
                                "gunakan alamat email yang anda telah daftarkan sebagai User name!");
                        alertDialog.show();

                        new Handler().postDelayed(new Runnable(){
                            @Override
                            public void run() {
                                Intent i = new Intent(Registrasi.this, Login.class);
                                startActivity(i);
                            }
                        }, 3000);
                    }
                }
            }
        });
    }

    private String convertToString(DatePicker dp) {
        int tanggal = dp.getDayOfMonth();
        int bulan = dp.getMonth();
        int tahun = dp.getYear();

        Calendar c = Calendar.getInstance().getInstance();
        c.set(tahun, bulan, tanggal);

        Date d = c.getTime();

        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        String str = new String();
        try {
            str = df.format(d);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return str;
    }

    public void processWebService() {
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        request.addProperty("user", email.getText().toString());
        request.addProperty("nama", nama.getText().toString());
        request.addProperty("alamat", alamat.getText().toString());
        request.addProperty("nohp", noHP.getText().toString());
        request.addProperty("tgllahir", convertToString(tglLahir));
        request.addProperty("produkunggulan", produkUnggul.getText().toString());


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
                Log.d("Response", resultRequestSOAP.getProperty(0) + "");
            } else {
                Log.d("ERROR ", "Connection Error");
            }
        }

    }

}

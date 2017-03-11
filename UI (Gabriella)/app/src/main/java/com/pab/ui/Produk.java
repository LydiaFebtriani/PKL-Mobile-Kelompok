package com.pab.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pab.ui.database.DBManager;
import com.pab.ui.database.ProdukPKL;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class Produk extends AppCompatActivity {

    String NAMESPACE = "http://schemas.xmlsoap.org/wsdl";
    String URL = "http://webtest.unpar.ac.id/pklws/pkl.php?wsdl";

    String SOAP_ACTION;
    String METHOD_NAME;

    SoapSerializationEnvelope envelope;


    private SharedPreferences sp;
    private SharedPreferences.Editor ed;
    private DBManager db;
    private String command, id, sid, namaProduk;
    private EditText nama, hargaPokok, hargaJual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produk);


        sp = getSharedPreferences("data", MODE_PRIVATE);
        ed = sp.edit();
        db = new DBManager(this);

        nama = (EditText)findViewById(R.id.editNamaProduk);
        hargaPokok = (EditText)findViewById(R.id.editHargaPokok);
        hargaJual = (EditText)findViewById(R.id.editHargaJual);

        command = sp.getString("katalog_command", "");
        sid = sp.getString("sid", "");

        if (command.equals("klik_produk")) {
            id = sp.getString("id", "");

            ProdukPKL p = db.showDetailProduk(id);

            nama.setText(p.getNama());
            hargaPokok.setText(p.getHargaPokok() + "");
            hargaJual.setText(p.getHargaJual() + "");

            SOAP_ACTION = "getproduk";
            METHOD_NAME = "getproduk";
        } else if (command.equals("klik_produk_ws")) {

            SOAP_ACTION = "getproduk";
            METHOD_NAME = "getproduk";


            namaProduk = sp.getString("nama_produk", "");
            processWebService();
        } else {
            SOAP_ACTION = "regproduk";
            METHOD_NAME = "regproduk";
        }


        Button btnKembali = (Button) findViewById(R.id.buttonKembali);
        btnKembali.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(Produk.this, Katalog.class);
                startActivity(i);
            }
        });

        Button btnSimpan = (Button) findViewById(R.id.buttonSimpanProduk);
        btnSimpan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (nama.getText().toString() == "") {
                    nama.setError("Nama tidak boleh kosong");
                } else if (hargaPokok.getText().toString() == "") {
                    hargaPokok.setError("Harga Pokok tidak boleh kosong");
                } else if (hargaJual.getText().toString() == "") {
                    hargaJual.setError("Harga Jual tidak boleh kosong");
                } else {

                    processWebService();

                    if (command.equals("tambah_produk")) {
                        db.insertProduk(nama.getText().toString(),
                                Integer.parseInt(hargaPokok.getText().toString()),
                                Integer.parseInt(hargaJual.getText().toString()),
                                sp.getString("username", "" ));
                    } else {
                        db.updateProduk(id, nama.getText().toString(),
                                hargaPokok.getText().toString(),
                                hargaJual.getText().toString());
                    }
                    Toast.makeText(getApplicationContext(), "Menyimpan produk ...",
                            Toast.LENGTH_SHORT).show();



                    new Handler().postDelayed(new Runnable(){
                        @Override
                        public void run() {
                            Intent i = new Intent(Produk.this, Katalog.class);
                            startActivity(i);
                        }
                    }, 3000);
                }


            }
        });

        Button btnTambah = (Button) findViewById(R.id.buttonTambahProduk);
        btnTambah.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (nama.getText().toString() == "") {
                    nama.setError("Nama tidak boleh kosong");
                } else if (hargaPokok.getText().toString() == "") {
                    hargaPokok.setError("Harga Pokok tidak boleh kosong");
                } else if (hargaJual.getText().toString() == "") {
                    hargaJual.setError("Harga Jual tidak boleh kosong");
                } else {

                    processWebService();

                    if (command.equals("tambah_produk")) {
                        db.insertProduk(nama.getText().toString(),
                                Integer.parseInt(hargaPokok.getText().toString()),
                                Integer.parseInt(hargaJual.getText().toString()),
                                sp.getString("username", ""));
                    } else {
                        db.updateProduk(id, nama.getText().toString(),
                                hargaPokok.getText().toString(),
                                hargaJual.getText().toString());
                    }



                    ed.putString("katalog_command", "tambah_produk");
                    ed.commit();
                    Toast.makeText(getApplicationContext(), "Menambahkan produk ...",
                            Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(Produk.this, Produk.class);
                    startActivity(i);
                }

            }
        });

        Button btnHapus = (Button) findViewById(R.id.buttonHapusProduk);
        btnHapus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if(!command.equals("tambah_produk")) {
                    command = "hapus_produk";
                    SOAP_ACTION = "delproduk";
                    METHOD_NAME = "delproduk";
                    processWebService();

                    db.deleteProduk(id);
                    Toast.makeText(getApplicationContext(), "Produk dihapus",
                            Toast.LENGTH_SHORT).show();



                    new Handler().postDelayed(new Runnable(){
                        @Override
                        public void run() {
                            Intent i = new Intent(Produk.this, Katalog.class);
                            startActivity(i);
                        }
                    }, 3000);

                }


            }
        });

    }

    public void processWebService() {
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        request.addProperty("sid", sid);

        if (command.equals("klik_produk_ws") || command.equals("hapus_produk")) {
            request.addProperty("namaproduk", namaProduk);
        } else {
            request.addProperty("namaproduk", nama.getText().toString());
            request.addProperty("hargapokok", hargaPokok.getText().toString());
            request.addProperty("hargajual", hargaJual.getText().toString());
        }

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

                processProductDetails(resultRequestSOAP);

            } else {
                Log.d("ERROR ", "Connection Error");
            }
        }

        private void processProductDetails(SoapObject resultRequestSOAP) {

            String property[] = resultRequestSOAP.getProperty(0).toString().
                    replaceAll("\\(", "").replaceAll("\\)", "").
                    replaceAll("\"", "").split(",");

            if(command.equals("klik_produk_ws")) {
                nama.setText(property[0]);
                hargaPokok.setText(property[1]);
                hargaJual.setText(property[2]);
            } else {
                Toast.makeText(getApplicationContext(), property[0] + " sudah " + property[1],
                        Toast.LENGTH_LONG).show();
            }


        }

    }


}
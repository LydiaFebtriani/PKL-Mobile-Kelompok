package com.pab.ui;

import android.content.SharedPreferences;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pab.ui.database.DBManager;
import com.pab.ui.database.ListProduk;
import com.pab.ui.database.ProdukPKL;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;

public class Katalog extends AppCompatActivity {

    String NAMESPACE = "http://schemas.xmlsoap.org/wsdl";
    String URL = "http://webtest.unpar.ac.id/pklws/pkl.php?wsdl";

    String SOAP_ACTION = "getkatalog";
    String METHOD_NAME = "getkatalog";

    SoapSerializationEnvelope envelope;

    private SharedPreferences sp;
    private SharedPreferences.Editor ed;
    private DBManager db;
    private ArrayAdapter adapter;
    private List<String> idProduk, productNames, wsProduk;
    private String sid;
    private boolean wsConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_katalog);

        db = new DBManager(this);
        sp = getSharedPreferences("data", MODE_PRIVATE);
        ed = sp.edit();

        TextView title = (TextView)findViewById(R.id.titleKatalog);
        String username = sp.getString("username", "");
        String tmp = title.getText().toString() + System.getProperty("line.separator") + username;
        title.setText(tmp);

        wsProduk = new ArrayList<String>();

        sid = sp.getString("sid", "");
        processWebService();

        if (wsConnected) {
            if (wsProduk != null) {
                adapter = new ArrayAdapter<String>(this, R.layout.list_produk, wsProduk);
                ListView listView = (ListView) findViewById(R.id.listKatalog);
                listView.setAdapter(adapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        ed.putString("katalog_command", "klik_produk_ws");
                        ed.putString("nama_produk", wsProduk.get(position) + "");
                        ed.commit();

                        Intent i = new Intent(Katalog.this, Produk.class);
                        startActivity(i);
                    }
                });
            }
        } else {
            idProduk = db.getIdProduk(username);
            productNames = db.getNamaProduk(username);

            if(idProduk != null && productNames != null) {

                adapter = new ArrayAdapter<String>(this, R.layout.list_produk, productNames);
                ListView listView = (ListView) findViewById(R.id.listKatalog);
                listView.setAdapter(adapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        ed.putString("katalog_command", "klik_produk");
                        ed.putString("id", idProduk.get(position) + "");
                        ed.commit();

                        Intent i = new Intent(Katalog.this, Produk.class);
                        startActivity(i);
                    }
                });
            }
        }





    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.katalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_katalogTambah) {
            ed.putString("katalog_command", "tambah_produk");
            ed.commit();
            Toast.makeText(Katalog.this, "Tambah", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(Katalog.this, Produk.class);
            startActivity(i);
            return true;
        }
        else if (id == R.id.action_katalogTransaksi) {
            Toast.makeText(Katalog.this, "Transaksi", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(Katalog.this, Transaksi.class);
            startActivity(i);
            return true;
        }
        else if (id == R.id.action_katalogKeluar) {
            Toast.makeText(Katalog.this, "Keluar", Toast.LENGTH_SHORT).show();

            Intent i = new Intent(Katalog.this, SplashKeluar.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void processWebService() {
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        request.addProperty("sid", sid);

        envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);

        NetworkHandler handler = new NetworkHandler();
        handler.execute();

        wsConnected = true;
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

                processWSList(resultRequestSOAP);

            } else {
                Log.d("ERROR ", "Connection Error");
            }
        }

        private void processWSList(SoapObject resultRequestSOAP) {

            if (!resultRequestSOAP.getProperty(0).toString().equals("("+"\""+"\""+")")) {

                for (int i = 0; i < resultRequestSOAP.getPropertyCount(); i++) {
                    String namaProduk = resultRequestSOAP.getProperty(i).toString().
                            replaceAll("\\(", "").replaceAll("\\)", "").
                            replaceAll("\"", "");

                    Log.d("Nama", namaProduk);

                    wsProduk.add(namaProduk);
                }

            }

        }

    }


}

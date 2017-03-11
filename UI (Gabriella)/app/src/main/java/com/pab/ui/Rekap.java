package com.pab.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.pab.ui.database.DBManager;
import com.pab.ui.database.ListTransaksi;
import com.pab.ui.database.TransaksiPenjualan;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class Rekap extends AppCompatActivity {

    String NAMESPACE = "http://schemas.xmlsoap.org/wsdl";
    String URL = "http://webtest.unpar.ac.id/pklws/pkl.php?wsdl";

    String SOAP_ACTION = "getransaksi";
    String METHOD_NAME = "gettransaksi";

    SoapSerializationEnvelope envelope;

    private SharedPreferences sp;
    private SharedPreferences.Editor ed;
    private DBManager db;
    private String tanggalRekap, sid;
    private ListTransaksi list_ws;
    private int total_ws;
    private boolean wsConneted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rekap);

        db = new DBManager(this);
        sp = getSharedPreferences("data", MODE_PRIVATE);
        ed = sp.edit();

        String username = sp.getString("username", "");
        tanggalRekap = sp.getString("tanggal_rekap", "");
        sid = sp.getString("sid", "");

        list_ws = new ListTransaksi();
        processWebService();

        ListTransaksi list;
        int count;

        if(wsConneted) {
            list = list_ws;
            count = total_ws;
        } else {
            list = db.getAllTransaksi(username, tanggalRekap);
            count = db.countTotalTransaksi(username, tanggalRekap);
        }

        TextView title = (TextView)findViewById(R.id.titleRekap);
        String tmp = title.getText().toString() + System.getProperty("line.separator")
                + System.getProperty("line.separator") + "Total: " + count;
        title.setText(tmp);

        TableLayout layout = (TableLayout)findViewById(R.id.fRekap);

        for (int i = 0; i<list.getSize(); i++) {
            TableRow row = new TableRow(this);
            TextView tanggal = new TextView(this);
            TextView nama = new TextView(this);
            TextView harga = new TextView(this);
            TextView kuantitas = new TextView(this);
            TextView total = new TextView(this);

            tanggal.setText(list.getTransaksiAt(i).getTglTransaksi());
            row.addView(tanggal);
            nama.setText(list.getTransaksiAt(i).getNama());
            row.addView(nama);
            harga.setText(list.getTransaksiAt(i).getHargaJual() + "");
            row.addView(harga);
            kuantitas.setText(list.getTransaksiAt(i).getKuantitas() + "");
            row.addView(kuantitas);
            total.setText(list.getTransaksiAt(i).getTotalHarga() + "");
            row.addView(total);

            layout.addView(row);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rekap, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_rekapTransaksi) {
            Toast.makeText(Rekap.this, "Transaksi", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(Rekap.this, Transaksi.class);
            startActivity(i);
            return true;
        }
        else if (id == R.id.action_rekapKatalog) {
            Toast.makeText(Rekap.this, "Katalog", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(Rekap.this, Katalog.class);
            startActivity(i);
            return true;
        }
        else if (id == R.id.action_rekapKeluar) {
            Toast.makeText(Rekap.this, "Keluar", Toast.LENGTH_SHORT).show();

            Intent i = new Intent(Rekap.this, SplashKeluar.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void processWebService() {
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        request.addProperty("sid", sid);
        request.addProperty("tglrekap", tanggalRekap);

        envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);

        NetworkHandler handler = new NetworkHandler();
        handler.execute();

        wsConneted = true;
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

            String response[] = resultRequestSOAP.getProperty(0).toString().
                    replaceAll("\\(", "").replaceAll("\\)", "").
                    replaceAll("\"", "").split(",");

            if (!response[1].equals("tidak ditemukan")) {

                for (int i = 0; i < resultRequestSOAP.getPropertyCount(); i++) {
                    String property[] = resultRequestSOAP.getProperty(i).toString().
                            replaceAll("\\(", "").replaceAll("\\)", "").
                            replaceAll("\"", "").split(",");

                    int totalHarga = Integer.parseInt(property[1]) * Integer.parseInt(property[2]);
                    total_ws += totalHarga;

                    TransaksiPenjualan t = new TransaksiPenjualan(property[3], property[0],
                            Integer.parseInt(property[1]), Integer.parseInt(property[2]), totalHarga);

                    list_ws.addTransaksi(t);
                }

            }

        }

    }

}

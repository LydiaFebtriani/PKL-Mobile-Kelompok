package com.pab.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Jual extends AppCompatActivity {

    String NAMESPACE = "http://schemas.xmlsoap.org/wsdl";
    String URL = "http://webtest.unpar.ac.id/pklws/pkl.php?wsdl";

    String SOAP_ACTION = "regtransaksi";
    String METHOD_NAME = "regtransaksi";

    SoapSerializationEnvelope envelope;

    private SharedPreferences sp;
    private DBManager db;
    private String id, tglTransaksi, sid, namaProduk;
    private TextView nama, harga;
    private int hargaJual, kuantitas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jual);

        sp = getSharedPreferences("data", MODE_PRIVATE);
        db = new DBManager(this);

        sid = sp.getString("sid", "");
        namaProduk = sp.getString("nama_produk", "");
        id = sp.getString("id", "");
        ProdukPKL p = db.showDetailProduk(id);

        nama = (TextView)findViewById(R.id.jual_editNamaProduk);
        nama.setText(p.getNama());

        harga = (TextView)findViewById(R.id.jual_editHargaJual);
        harga.setText(p.getHargaJual() + "");
        hargaJual = p.getHargaJual();

        Button btnSimpan = (Button) findViewById(R.id.buttonProses);
        btnSimpan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                EditText et = (EditText)findViewById(R.id.editKuantitas);
                if (et.getText().toString() == "") {
                    et.setError("Kuantitas tidak boleh kosong");
                }
                else {
                    kuantitas = Integer.parseInt(et.getText().toString());
                    int hargaTotal = hargaJual * kuantitas;

                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = new Date();
                    tglTransaksi = dateFormat.format(date);

                    db.insertTransaksi(tglTransaksi, Integer.parseInt(id), kuantitas, hargaTotal);

                    processWebService();

                    AlertDialog alertDialog = new AlertDialog.Builder(Jual.this).create();
                    alertDialog.setTitle("TRANSAKSI BERHASIL");
                    alertDialog.setMessage("Terima kasih anda telah bertransaksi produk " +
                            nama.getText().toString() + " sebanyak " + kuantitas +
                            " seharga " + hargaTotal);
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent i = new Intent(Jual.this, Transaksi.class);
                                    startActivity(i);
                                }
                            });
                    alertDialog.show();
                }
            }
        });

        Button btnBatal = (Button) findViewById(R.id.buttonBatalJual);
        btnBatal.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(Jual.this, Transaksi.class);
                startActivity(i);
            }
        });
    }


    public void processWebService() {

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        request.addProperty("sid", sid);
        request.addProperty("namaproduk", namaProduk);
        request.addProperty("hargajual", hargaJual + "");
        request.addProperty("qtyjual", kuantitas + "");
        request.addProperty("tgljual", tglTransaksi);

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
//                process(resultRequestSOAP);
            } else {
                Log.d("ERROR ", "Connection Error");
            }
        }

//        private void process(SoapObject resultRequestSOAP) {
//            String property[] = resultRequestSOAP.getProperty(0).toString().
//                    replaceAll("\\(", "").replaceAll("\\)", "").
//                    replaceAll("\"", "").split(",");
//
//            Toast.makeText(getApplicationContext(), "Transaksi " + property[4],
//                    Toast.LENGTH_SHORT).show();
//        }

    }

}

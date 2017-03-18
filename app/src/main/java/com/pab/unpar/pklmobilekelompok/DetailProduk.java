package com.pab.unpar.pklmobilekelompok;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class DetailProduk extends AppCompatActivity implements View.OnClickListener{
//    private DataManipulator dh;
    private SharedPreferences sp;
    //private int idUser;
    private String sessionId;
    private String[] produk;
    private Soap soap = new Soap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_detail_produk);
        sp=getSharedPreferences("dataProduk",MODE_PRIVATE);
        this.sessionId = sp.getString("sessionId","");

        Button batal = (Button) findViewById(R.id.buttonBatal);
        batal.setOnClickListener(this);
        Button simpan = (Button) findViewById(R.id.buttonSimpan);
        simpan.setOnClickListener(this);

        viewDetail();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void viewDetail(){
//        dh = new DataManipulator(this);
//        String[] list = dh.select1FromProduk(sp.getInt("idProduk",-1));
        produk = soap.getDetailProduk(sessionId,sp.getString("produk",""));

        EditText nama = (EditText) findViewById(R.id.detailNamaProduk);
        EditText hargaPokok = (EditText) findViewById(R.id.detailHargaPokok);
        EditText hargaJual = (EditText) findViewById(R.id.detailHargaJual);
        nama.setText(produk[0]);
        hargaPokok.setText(produk[1]);
        hargaJual.setText(produk[2]);
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()){
            case R.id.buttonBatal:
                i = new Intent(DetailProduk.this, Katalog.class);
                startActivity(i);
                finish();
                break;
            case R.id.buttonSimpan:
                EditText nama = (EditText) findViewById(R.id.detailNamaProduk);
                EditText hargaPokok = (EditText) findViewById(R.id.detailHargaPokok);
                EditText hargaJual = (EditText) findViewById(R.id.detailHargaJual);

//                dh = new DataManipulator(this);
//                dh.update1FromProduk(sp.getInt("idProduk",-1),nama.getText().toString(),hargaPokok.getText().toString(),hargaJual.getText().toString(),this.idUser);
                boolean res = soap.setAddProduk(sessionId,nama.getText().toString(),hargaPokok.getText().toString(),hargaJual.getText().toString());
                Log.d("Update produk",res+"");
                if(res) {
                    Toast.makeText(getApplicationContext(), "Produk berhasil diupdate", Toast.LENGTH_SHORT).show();

                    i = new Intent(DetailProduk.this, Katalog.class);
                    startActivity(i);
                    finish();
                }
                break;
        }
    }
}

package com.pab.unpar.pklmobileapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class Transaksi extends AppCompatActivity {
//    private DataManipulator dh;
    private ListView listView;
    //private int[] valuesId;
    private SharedPreferences sp;
    //private int idUser;
    private String[] values;
    private String sessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaksi);
        sp=getSharedPreferences("dataProduk",MODE_PRIVATE);
        this.sessionId = sp.getString("sessionId","");

        Button rekap = (Button) findViewById(R.id.buttonRekap);
        rekap.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0){
                Intent i = new Intent(Transaksi.this, Rekap.class);
                startActivity(i);
            }
        });

        listView = (ListView) findViewById(R.id.produklist);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int itemPosition = position;
                String itemValue = (String) listView.getItemAtPosition(itemPosition);
                List<String> list = Arrays.asList(values);
                if(list.contains(itemValue)){
                    SharedPreferences.Editor ed = sp.edit();
                    ed.putString("produk",values[itemPosition]);
                    ed.commit();
                    Log.d("Transaksi, produk",values[itemPosition]);

                    Intent i = new Intent(Transaksi.this, Jual.class);
                    startActivity(i);
                }
            }
        });

        printProduk();
    }

    /* Print produk */
    private void printProduk(){
//        dh = new DataManipulator(this);
//        List<String[]> list = new ArrayList<String[]>();
//        list = dh.selectAllProductName(this.idUser);
//
//        //Print
//        valuesId = new int[list.size()];
//        String[] values = new String[list.size()];
//        for(int i=0;i<list.size();i++){
//            valuesId[i] = Integer.parseInt(list.get(i)[0]);
//            values[i] = list.get(i)[1];
//        }
        Soap soap = new Soap();
        values = soap.getKatalog(sessionId);
        if(values!=null){
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,values);
            listView.setAdapter(adapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.logout){
            Intent i = new Intent(Transaksi.this, Login.class);
            startActivity(i);
            finish();
        }
        else if(item.getItemId() == R.id.menuhome){
            Intent i = new Intent(Transaksi.this, Home.class);
            startActivity(i);
            finish();
        }
        else{
            Intent i = new Intent(Transaksi.this, SplashKeluar.class);
            startActivity(i);
            finish();
        }
        return true;
    }


}

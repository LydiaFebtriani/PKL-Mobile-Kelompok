package com.pab.unpar.pklmobilekelompok;

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
import android.widget.ListView;

import java.util.Arrays;
import java.util.List;

public class Katalog extends AppCompatActivity {
    private DataManipulator dh;
    private ListView listView;
    //private int[] valuesId;
    private SharedPreferences sp;
    //private int idUser;
    private String[] values;
    private String sessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_katalog);
        sp=getSharedPreferences("dataProduk",MODE_PRIVATE);
        this.sessionId = sp.getString("sessionId","");

        Button tambah = (Button) findViewById(R.id.buttonTambah);
        tambah.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Katalog.this, Tambah.class);
                startActivity(intent);
            }
        });

        listView = (ListView) findViewById(R.id.produklistKatalog);
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
                    Log.d("Katalog, produk",values[itemPosition]);

                    Intent i = new Intent(Katalog.this, DetailProduk.class);
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
//        //list = dh.selectAllProductName(this.idUser);
//
//        //Print
//        valuesId = new int[list.size()];
//        ArrayList<String> values = new ArrayList<String>();
//        for(int i=0;i<list.size();i++){
//            valuesId[i] = Integer.parseInt(list.get(i)[0]);
//            values.add(list.get(i)[1]);
//        }
        Soap soap = new Soap();
        values = soap.getKatalog(sessionId);
        if(values!=null && values.length>0){
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,values);
            this.listView.setAdapter(adapter);
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
            Intent i = new Intent(Katalog.this, Login.class);
            startActivity(i);
            finish();
        }
        else if(item.getItemId() == R.id.menuhome){
            Intent i = new Intent(Katalog.this, Home.class);
            startActivity(i);
            finish();
        }
        else{
            Intent i = new Intent(Katalog.this, SplashKeluar.class);
            startActivity(i);
            finish();
        }
        return true;
    }


}

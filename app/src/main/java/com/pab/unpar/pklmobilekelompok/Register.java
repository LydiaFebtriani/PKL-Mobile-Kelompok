package com.pab.unpar.pklmobilekelompok;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import java.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import static java.lang.Thread.sleep;

public class Register extends Activity implements View.OnClickListener{
    private Calendar calendar;
    private TextView dateView;
    private int year, month, day;
    private String password;

    private DataManipulator dh;
    static final int DIALOG_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_register);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        dateView = (TextView) findViewById(R.id.inputTglLahir);
        dateView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0){
                setDate();
            }
        });

        View simpan = findViewById(R.id.buttonSimpanRegister);
        simpan.setOnClickListener(this);
        View batal = findViewById(R.id.buttonBatalRegister);
        batal.setOnClickListener(this);
    }

    public void onClick(View v){
        Intent i;
        switch(v.getId()){
            case R.id.buttonBatalRegister:
                i = new Intent(this, Login.class);
                startActivity(i);
                finish();
                break;
            case R.id.buttonSimpanRegister:
                View inputEmail = (EditText) findViewById(R.id.inputNewEmail);
                View inputNama = (EditText) findViewById(R.id.inputNewNama);
                View inputAlamat = (EditText) findViewById(R.id.inputNewAlamat);
                View inputHP = (EditText) findViewById(R.id.inputNoHP);
                View inputTgl = (Button) findViewById(R.id.inputTglLahir);
                View inputProduk = (EditText) findViewById(R.id.inputProdukUnggul);

                String email = ((TextView) inputEmail).getText().toString();
                String nama = ((TextView) inputNama).getText().toString();
                String alamat = ((TextView) inputAlamat).getText().toString();
                String hp = ((TextView) inputHP).getText().toString();
                String tgl = ((TextView) inputTgl).getText().toString();
                String produk = ((TextView) inputProduk).getText().toString();

                /* Menangani tanggal untuk password */
                String[] str = tgl.split("/");
                Log.d("Register str before",str[0]+""+str[1]+""+str[2]+" "+password);
                if(str[2].length() == 1){
                    char temp = str[2].charAt(0);
                    str[2] = "0"+temp;
                }
                if(str[1].length() == 1){
                    char temp = str[1].charAt(0);
                    str[1] = "0"+temp;
                }
                password = str[0]+str[1]+str[2];
                Log.d("Register str",str[0]+""+str[1]+""+str[2]+" "+password);

                Soap soap = new Soap();
                soap.register(this,email,nama,alamat,hp,password,produk);
                Log.d("Register password",tgl+" "+password);

//                this.dh = new DataManipulator(this);
//                this.dh.insertUser(email,password,nama,alamat,hp,tgl,produk);

//                Toast.makeText(getApplicationContext(), "Register berhasil", Toast.LENGTH_LONG).show();
//                Toast.makeText(getApplicationContext(), "Silahkan login dengan tanggal lahir: "+tgl+" sebagai password!", Toast.LENGTH_LONG).show();
                showDialog(DIALOG_ID);
                break;
        }
    }

    @SuppressWarnings("deprecation")
    public void setDate(){
        showDialog(999);
        //Toast.makeText(getApplicationContext(), null, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected final Dialog onCreateDialog(final int id){
        Dialog dialog = null;
        switch(id){
            case DIALOG_ID:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Selamat anda telah terdaftar, silahkan login untuk menggunakan aplikasi ini! Saat login, gunakan alamat email sebagai username dan tanggal lahir ("+this.password+") sebagai password!")
                        .setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent i = new Intent(Register.this, Login.class);
                                startActivity(i);
                                finish();
                            }
                        });
                AlertDialog alert = builder.create();
                dialog = alert;
                return dialog;
            case 999:
                return new DatePickerDialog(this, myDateListener, year, month, day);
            default:

        }
        return dialog;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener(){
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3){
            //TODO set database dan password
            showDate(arg1,arg2+1,arg3);
        }
    };

    private void showDate(int year, int month, int day){
        dateView.setText(new StringBuilder().append(year).append("/").append(month).append("/").append(day));
    }
}

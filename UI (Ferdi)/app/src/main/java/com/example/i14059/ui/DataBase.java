package com.example.i14059.ui;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Ferdi on 22/02/2017.
 */

public class DataBase extends SQLiteOpenHelper{
    public static final String DATABASE_NAME = "PKL.db";
    private static final String TABLE_NAME = "PKL";
    private static final int DATABASE_VERSION = 1;
    SQLiteDatabase db;

    public DataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase dba) {
        // TODO Auto-generated method stub
        String sql = "create table lPKL(user text primary key, nama text null, alamat text null, noHP text null, tglLahir text null, produkUnggulan text null);";
        Log.d("Data", "onCreate: " + sql);
        dba.execSQL(sql);
        String sql1 = "create table lProduk(namaProduk text null, hargaPokok integer null, hargaJual integer null, user text null, foreign key(user) references lPKL(user));";
        Log.d("Data", "onCreate: " + sql1);
        dba.execSQL(sql1);
        String sql2 = "create table lTransaksi(namaProduk text null, hargaJual integer null, qtyJual integer null, tglJual text null, user text null, foreign key(user) references lPKL(user));";
        Log.d("Data", "onCreate: " + sql2);
        dba.execSQL(sql2);
        this.db=dba;
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    public String searchPass(String user){
        db = this.getReadableDatabase();
        String query="select tglLahir from lPKL where lPKL.user = '"+user+"'";
        Cursor cursor=db.rawQuery(query,null);
        String a;
        a="zxcv";
        if (cursor.moveToFirst()){
            a=cursor.getString(0);
        }
        return a;
    }

}

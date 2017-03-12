package com.pab.unpar.pklmobilekelompok;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LENOVO Z410_W8PRO on 2/16/2017.
 */

public class DataManipulator {
    private static final String DATABASE_NAME = "mydatabase.db";
    private static final int DATABASE_VERSION = 1;
    static final String USER_TABLE = "user";
    static final String PRODUCT_TABLE = "produk";
    static final String TRANSACTION_TABLE = "transaksi";
    private static Context context;
    static SQLiteDatabase db;

    private SQLiteStatement insertStmtUser;
    private SQLiteStatement insertStmtProduk;
    private SQLiteStatement insertStmtTransaksi;

    private static final String INSERT_USER = "insert into "+USER_TABLE+" (email,password,nama,alamat,nohp,tgllahir,produkunggul) values (?,?,?,?,?,?,?)";
    private static final String INSERT_PRODUK = "insert into "+PRODUCT_TABLE+" (namaProduk, hargaPokok, hargaJual, idUser) values(?,?,?,?)";
    private static final String INSER_TRANSAKSI = "insert into "+TRANSACTION_TABLE+" (idUser,idProduk,kuantitas,harga,tglJual) values(?,?,?,?,?)";

    public DataManipulator(Context context){
        DataManipulator.context = context;
        OpenHelper openHelper = new OpenHelper(DataManipulator.context);
        DataManipulator.db = openHelper.getWritableDatabase();
        this.insertStmtUser = DataManipulator.db.compileStatement(INSERT_USER);
        this.insertStmtProduk = DataManipulator.db.compileStatement(INSERT_PRODUK);
        this.insertStmtTransaksi = DataManipulator.db.compileStatement(INSER_TRANSAKSI);
    }

    /******************* USER *******************/
    /* INSERT */
    public long insertUser(String email, String password, String nama, String alamat, String nohp, String tgllahir, String produkunggul){
        this.insertStmtUser.bindString(1,email);
        this.insertStmtUser.bindString(2,password);
        this.insertStmtUser.bindString(3,nama);
        this.insertStmtUser.bindString(4,alamat);
        this.insertStmtUser.bindString(5,nohp);
        this.insertStmtUser.bindString(6,tgllahir);
        this.insertStmtUser.bindString(7,produkunggul);
        return this.insertStmtUser.executeInsert();
    }
    /* DELETE */
    public void deleteUserTable(){
        db.delete(USER_TABLE,null,null);
    }
    /* SELECT ALL */
    public List<String[]> selectAllUser() {
        List<String[]> list = new ArrayList<String[]>();
        Cursor cursor = db.query(USER_TABLE,
                new String[]{
                        "idUser","email","password","nama","alamat","nohp","tgllahir","produkunggul"
                },
                null,null,null,null,"idUser asc");
        int x=0;
        if(cursor.moveToFirst()){
            do{
                String[] b1 = new String[]{cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7)};
                list.add(b1);

                x=x+1;
            }while(cursor.moveToNext());
        }
        if(cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        cursor.close();
        return list;
    }
    /* SELECT EMAIL AND PASSWORD ALL USER */
    public String[] select1User(String email, String password){
        Cursor cursor = null;
        String[] list  = new String[3];
        try{
            cursor = db.rawQuery("SELECT idUser,email,password FROM "+USER_TABLE+" WHERE email like \""+email+"\" AND password like \""+password+"\"",null);
            if(cursor.moveToFirst()){
                //do{
                    list = new String[]{cursor.getString(0),cursor.getString(1),cursor.getString(2)};
                //}while(cursor.moveToNext());
            }
            return list;
        }finally {
            cursor.close();
        }
    }

    /******************* PRODUK *******************/
    /* INSERT */
    public long insertProduk(String namaProduk,String hargaPokok,String hargaJual,int idUser){
        this.insertStmtProduk.bindString(1,namaProduk);
        this.insertStmtProduk.bindString(2,hargaPokok);
        this.insertStmtProduk.bindString(3,hargaJual);
        this.insertStmtProduk.bindLong(4,idUser);
        return this.insertStmtProduk.executeInsert();
    }
    /* DELETE */
    public void deleteProdukTable(){
        db.delete(PRODUCT_TABLE,null,null);
    }
    /* SELECT ALL */
    public List<String[]> selectAllProduk(int idUser) {
        List<String[]> list = new ArrayList<String[]>();
//        Cursor cursor = db.query(PRODUCT_TABLE,
//                new String[]{
//                        "idProduk", "namaProduk", "hargaPokok", "hargaJual"
//                },
//                null,null,null,null,"idProduk asc");
        Cursor cursor = db.rawQuery("SELECT idProduk, namaProduk, hargaPokok, hargaJual FROM "+PRODUCT_TABLE+" WHERE idUser=\""+(idUser+"")+"\"", null);
        int x=0;
        if(cursor.moveToFirst()){
            do{
                String[] b1 = new String[]{cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3)};
                list.add(b1);

                x=x+1;
            }while(cursor.moveToNext());
        }
        if(cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        cursor.close();
        return list;
    }
    /* SELECT ALL PRODUCT NAME */
    public List<String[]> selectAllProductName(int idUser){
        List<String[]> list = new ArrayList<String[]>();
        //Cursor cursor = db.query(PRODUCT_TABLE,new String[]{"idProduk","namaProduk"},null,null,null,null,"idProduk asc");
        Cursor cursor = db.rawQuery("SELECT idProduk, namaProduk FROM "+PRODUCT_TABLE+" WHERE idUser=\""+(idUser+"")+"\"", null);
        int x=0;
        if(cursor.moveToFirst()){
            do{
                String[] b1 = new String[]{cursor.getString(0),cursor.getString(1)};
                list.add(b1);

                x=x+1;
            }while(cursor.moveToNext());
        }
        if(cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        cursor.close();
        return list;
    }
    /* SELECT 1 FROM TABLE */
    public String[] select1FromProduk(int idProduk){
        Cursor cursor = null;
        String[] list  = new String[3];
        try{
            cursor = db.rawQuery("SELECT namaProduk,hargaPokok,hargaJual FROM "+PRODUCT_TABLE+" WHERE idProduk=\""+(idProduk+"")+"\"", null);
            if(cursor.moveToFirst()){
                //do{
                    list = new String[]{cursor.getString(0),cursor.getString(1),cursor.getString(2)};
                //}while(cursor.moveToNext());
            }
            return list;
        }finally {
            cursor.close();
        }
    }
    /* SELECT 1 FROM TABLE */
    public String select1NamaProduk(int idProduk){
        Cursor cursor = null;
        String list  = new String();
        try{
            cursor = db.rawQuery("SELECT namaProduk FROM "+PRODUCT_TABLE+" WHERE idProduk=?", new String[]{idProduk+""});
            if(cursor.moveToFirst()){
                //do{
                    list = cursor.getString(0);
                //}while(cursor.moveToNext());
            }
            return list;
        }finally {
            cursor.close();
        }
    }
    /* UPDATE 1 RECORD FROM PRODUCT TABLE */
    public void update1FromProduk(int idProduk, String namaProduk,String hargaPokok,String hargaJual,int idUser){
        Cursor cursor = db.rawQuery("UPDATE "+PRODUCT_TABLE+" SET namaProduk=\""+namaProduk+"\", hargaPokok=\""+hargaPokok+"\", hargaJual=\""+hargaJual+"\" WHERE idUser=\""+(idUser+"")+"\" AND idProduk=\""+(idProduk+"")+"\"",null);
        cursor.moveToFirst();
        cursor.close();
    }

    /******************* TRANSAKSI *******************/
    /* INSERT */
    public long insertTransaksi(int idUser,int idProduk, int kuantitas,String harga,String tglJual){
        this.insertStmtTransaksi.bindLong(1,idUser);
        this.insertStmtTransaksi.bindLong(2,idProduk);
        this.insertStmtTransaksi.bindLong(3,kuantitas);
        this.insertStmtTransaksi.bindString(4,harga);
        this.insertStmtTransaksi.bindString(5,tglJual);
        return this.insertStmtTransaksi.executeInsert();
    }
    /* DELETE */
    public void deleteTransaksiTable(){
        db.delete(TRANSACTION_TABLE,null,null);
    }
    /* SELECT ALL */
    public List<String[]> selectAllTransaksi(int idUser) {
        List<String[]> list = new ArrayList<String[]>();
//        Cursor cursor = db.query(TRANSACTION_TABLE,
//                new String[]{
//                        "idTransaksi","idUser","idProduk","kuantitas","harga"
//                },
//                null,null,null,null,"idTransaksi asc");
        Log.d("Select All Transaksi","Sebelum rawQuery");
        Cursor cursor = db.rawQuery("SELECT idTransaksi, idUser, idProduk, kuantitas, harga, tglJual FROM "+TRANSACTION_TABLE+" WHERE idUser=\""+(idUser+"")+"\"", null);
        int x=0;
        if(cursor.moveToFirst()){
            do{
                String[] b1 = new String[]{cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5)};
                list.add(b1);

                x=x+1;
            }while(cursor.moveToNext());
        }
        if(cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        cursor.close();
        Log.d("Select All Transaksi","Setelah rawQuery");
        return list;
    }
    /* SELECT SEMUA TRANSAKSI DARI 1 USER */
    public String[] select1FromTransaksi(int idUser){
        Cursor cursor = null;
        String[] list  = new String[5];
        try{
            cursor = db.rawQuery("SELECT idTransaksi,idProduk,kuantitas,harga,tglJual FROM "+TRANSACTION_TABLE+" WHERE idUser=?", new String[]{idUser+""});
            if(cursor.moveToFirst()){
                do{
                    list = new String[]{cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4)};
                }while(cursor.moveToNext());
            }
            return list;
        }finally {
            cursor.close();
        }
    }
    /* UPDATE 1 RECORD FROM TRANSACTION TABLE */
    public void update1FromTransaksi(int idTransaksi, int idProduk, int kuantitas, String harga, String tglJual){
        Cursor cursor = db.rawQuery("UPDATE "+TRANSACTION_TABLE+" SET kuantitas=\""+(kuantitas+"")+"\", harga=\""+harga+"\", tglJual=\""+tglJual+"\" WHERE idTransaksi=\""+(idTransaksi+"")+"\" AND idProduk=\""+(idProduk+"")+"\"",null);
        cursor.moveToFirst();
        cursor.close();
    }


    private static class OpenHelper extends SQLiteOpenHelper{
        public OpenHelper(Context context){
            super(context,DATABASE_NAME,null,DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE "+USER_TABLE+" (idUser INTEGER PRIMARY KEY autoincrement, email TEXT,password TEXT,nama TEXT,alamat TEXT,nohp TEXT,tgllahir TEXT,produkunggul TEXT)");
            db.execSQL("CREATE TABLE "+PRODUCT_TABLE+" (idProduk INTEGER PRIMARY KEY autoincrement, namaProduk TEXT, hargaPokok INTEGER, hargaJual INTEGER, idUser INTEGER, FOREIGN KEY(idUser) REFERENCES "+USER_TABLE+"(idUser))");
            db.execSQL("CREATE TABLE "+TRANSACTION_TABLE+" (idTransaksi INTEGER PRIMARY KEY autoincrement, idUser INTEGER, idProduk INTEGER, kuantitas INTEGER, harga TEXT, tglJual TEXT,FOREIGN KEY (idUser) REFERENCES "+USER_TABLE+"(idUser), FOREIGN KEY (idProduk) REFERENCES "+PRODUCT_TABLE+"(idProduk))");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+USER_TABLE);
            db.execSQL("DROP TABLE IF EXISTS "+PRODUCT_TABLE);
            db.execSQL("DROP TABLE IF EXISTS "+TRANSACTION_TABLE);
            onCreate(db);
        }

    }
}

package com.example.i14072.pklmobile;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 14/02/2017.
 */

public class DatabaseHandler {
    private static final String DATABASE_NAME="pkldatabase.db";
    private static int DATABASE_VERSION=1;
    static final String USER_TABLE="users";
    static final String PRODUCT_TABLE="products";
    static final String TRANSACTION_TABLE="prod_transaction";
    private static Context context;
    static SQLiteDatabase db;

    private SQLiteStatement insertUserStatement;
    private SQLiteStatement insertProductStatement;
    private SQLiteStatement insertTransactionStatement;

    private static final String INSERT_USER="insert into "+USER_TABLE+" (username,name,address,phone,birthDate,topProd,syncStatus) values (?,?,?,?,?,?,?)";
    private static final String INSERT_PRODUCT="insert into "+PRODUCT_TABLE+" (prodName,price,sellPrice,idUser,syncStatus) values (?,?,?,?,?)";
    private static final String INSERT_TRANSACTION="insert into "+TRANSACTION_TABLE+" (idProd,qty,saleDate,syncStatus) values (?,?,?,?)";

    String NAMESPACE="http://schemas.xmlsoap.org/wsdl";
    String URL="http://webtest.unpar.ac.id/pklws/pkl.php?wsdl";

    String ACTION_LOGIN="login";
    String METHOD_LOGIN="login";
    String ACTION_REGISTER="regpkl";
    String METHOD_REGISTER="regpkl";
    String ACTION_GETPROFILE="getpkl";
    String METHOD_GETPROFILE="getpkl";
    String ACTION_LOGOUT="logout";
    String METHOD_LOGOUT="logout";
    String ACTION_ADDPRODUCT="addproduk";
    String METHOD_ADDPRODUCT="regproduk";
    String ACTION_GETPRODUCT="getproduk";
    String METHOD_GETPRODUCT="getproduk";
    String ACTION_DELETEPRODUCT="delproduk";
    String METHOD_DELETEPRODUCT="delproduk";
    String ACTION_GETKATALOG="getkatalog";
    String METHOD_GETKATALOG="getkatalog";
    String ACTION_ADDTRANSAKSI="addtransaksi";
    String METHOD_ADDTRANSAKSI="regtransaksi";
    String ACTION_GETTRANSAKSI="gettransaksi";
    String METHOD_GETTRANSAKSI="gettransaksi";

    SoapSerializationEnvelope envelope;
    HttpTransportSE transport;
    String resultString;

    private static final int TIMEOUT=3000;

    public DatabaseHandler(Context context){
        DatabaseHandler.context=context;
        OpenHelper openHelper=new OpenHelper(DatabaseHandler.context);
        DatabaseHandler.db=openHelper.getWritableDatabase();
        this.insertUserStatement=DatabaseHandler.db.compileStatement(INSERT_USER);
        this.insertProductStatement=DatabaseHandler.db.compileStatement(INSERT_PRODUCT);
        this.insertTransactionStatement=DatabaseHandler.db.compileStatement(INSERT_TRANSACTION);

        transport=new HttpTransportSE(URL);
        envelope=new SoapSerializationEnvelope(SoapEnvelope.VER11);
    }

    //Bagian 'CREATE' dari CRUD
    private long insertUser(String username,String name,String address, String phone,String birthDate,String topProd,boolean syncStatus){
        this.insertUserStatement.bindString(1,username);
        this.insertUserStatement.bindString(2,name);
        this.insertUserStatement.bindString(3,address);
        this.insertUserStatement.bindString(4,phone);
        this.insertUserStatement.bindString(5,birthDate);
        this.insertUserStatement.bindString(6,topProd);
        this.insertUserStatement.bindLong(7,syncStatus?1:0);
        return this.insertUserStatement.executeInsert();
    }

    private long insertProduct(String prodName,int price,int sellPrice,int idUser,boolean syncStatus){
        this.insertProductStatement.bindString(1,prodName);
        this.insertProductStatement.bindLong(2,price);
        this.insertProductStatement.bindLong(3,sellPrice);
        this.insertProductStatement.bindLong(4,idUser);
        this.insertProductStatement.bindLong(5,syncStatus?1:0);
        return this.insertProductStatement.executeInsert();
    }

    private long insertTransaction(int idProd,int qty,String saleDate,boolean syncStatus){
        this.insertTransactionStatement.bindLong(1,idProd);
        this.insertTransactionStatement.bindLong(2,qty);
        this.insertTransactionStatement.bindString(3,saleDate);
        this.insertTransactionStatement.bindLong(4,syncStatus?1:0);
        return this.insertTransactionStatement.executeInsert();
    }

    //Bagian 'READ' dari CRUD
    public List<String[]> selectAllUser(){
        List<String[]> list=new ArrayList<String[]>();
        Cursor c=db.query(USER_TABLE,new String[]{"idUser","username","name","address","phone","birthDate","topProd"},
                null,null,null,null,"idUser asc");
        int x=0;
        if(c.moveToFirst()){
            do{
                String[] row=new String[]{c.getString(0),c.getString(1),c.getString(2),c.getString(3),c.getString(4),c.getString(5),c.getString(6)};
                list.add(row);
                x=x+1;
            }
            while (c.moveToNext());
        }
        if(c!=null&&!c.isClosed()){
            c.close();
        }
        c.close();
        return list;
    }

    public String[] selectIdName(String username){
        Cursor c=db.rawQuery("select idUser,name from "+USER_TABLE+" where username=?",new String[]{username+""});
        c.moveToFirst();
        return new String[]{c.getString(0),c.getString(1)};
    }

    public ArrayList<String>[] selectProductName(int idUser){
        ArrayList<String> listId=new ArrayList<String>();
        ArrayList<String> listName=new ArrayList<String>();
        Cursor c=db.rawQuery("select idProd,prodName from "+PRODUCT_TABLE+" where idUser=? order by idProd asc",new String[]{idUser+""});
        if(c.moveToFirst()){
            do{
                String id=c.getString(0);
                listId.add(id);
                String name=c.getString(1);
                listName.add(name);
            }
            while(c.moveToNext());
        }
        if(c!=null&&!c.isClosed()){
            c.close();
        }
        c.close();
        return new ArrayList[]{listId,listName};
    }

    private String[] selectProductById(int id){
        Cursor c=db.rawQuery("select prodName,price,sellPrice from "+PRODUCT_TABLE+" where idProd=?",new String[]{id+""});
        if(c.moveToFirst()){
            return new String[]{c.getString(0),c.getString(1),c.getString(2)};
        }
        return null;
    }

    public List<String[]> selectAllProduct(){
        List<String[]> list=new ArrayList<String[]>();
        Cursor c=db.query(PRODUCT_TABLE,new String[]{"idUser","idProd","prodName","price","sellPrice"},
                null,null,null,null,"idProd asc");
        int x=0;
        if(c.moveToFirst()){
            do{
                String[] row=new String[]{c.getString(0),c.getString(1),c.getString(2),c.getString(3),c.getString(4)};
                list.add(row);
                x=x+1;
            }
            while (c.moveToNext());
        }
        if(c!=null&&!c.isClosed()){
            c.close();
        }
        c.close();
        return list;
    }

    private List<String[]> selectReport(int idUser){
        List<String[]> list=new ArrayList<String[]>();
        Cursor c=db.rawQuery("select prodName,qty,sellPrice from "+PRODUCT_TABLE+" join "+TRANSACTION_TABLE+" on "+TRANSACTION_TABLE+".idProd="+PRODUCT_TABLE+".idProd where idUser=?",new String[]{idUser+""});
        if(c.moveToFirst()){
            do{
                String[] row=new String[]{c.getString(0),c.getString(2),c.getString(1)};
                list.add(row);
            }
            while(c.moveToNext());
        }
        if(c!=null&&!c.isClosed()){
            c.close();
        }
        c.close();
        return list;
    }

    public boolean checkUsername(String username,String password){
        Cursor c=db.rawQuery("select birthDate from "+USER_TABLE+" where username=?",new String[]{username});
        if(c.getCount()>0){
            c.moveToFirst();
            String temp=c.getString(0);
            if(temp.equals(password)){
                return true;
            }
        }
        return false;
    }

    //Bagian 'UPDATE' dari CRUD
    private boolean updateProduct(int idProd,String prodName,int price,int sellPrice,boolean syncStatus){
        ContentValues cv=new ContentValues();
        cv.put("prodName",prodName);
        cv.put("price",price);
        cv.put("sellPrice",sellPrice);
        cv.put("syncStatus",syncStatus);
        return db.update(PRODUCT_TABLE,cv,"idProd="+idProd+" or prodName="+prodName,null)>0;
    }

    //Bagian 'DELETE' dari CRUD
    public void deleteAllUser(){
        db.delete(USER_TABLE,null,null);
    }

    public void deleteAllProduct(){
        db.delete(PRODUCT_TABLE,null,null);
    }

    public void deleteUser(int row){
        db.delete(USER_TABLE,null,null);
    }

    public void deleteProduct(int row){
        db.delete(PRODUCT_TABLE,null,null);
    }

    //Bagian untuk koneksi DB ke web service
    public void updateRegister(String username,String name,String address, String phone,String birthDate,String topProd){
        SoapObject request=new SoapObject(NAMESPACE,METHOD_REGISTER);
        int wait=0;
        request.addProperty("user",username);
        request.addProperty("nama",name);
        request.addProperty("alamat",address);
        request.addProperty("nohp",phone);
        request.addProperty("tgllahir",birthDate);
        request.addProperty("produkunggulan",topProd);

        envelope=new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);

        NetworkHandler handler=new NetworkHandler(ACTION_REGISTER);
        handler.execute();

        while(resultString==null&&wait<TIMEOUT){
            //tunggu
            try{
                wait+=1;
                Thread.sleep(1);
            }
            catch(InterruptedException ie){}
        }

        Log.d("Cek status",resultString);

        if(wait<TIMEOUT&&resultString.split(",")[0].equalsIgnoreCase("sukses")){
            resultString=null;
            insertUser(username,name,address,phone,birthDate,topProd,true);
        }
        else{
            resultString=null;
            insertUser(username,name,address,phone,birthDate,topProd,false);
        }
    }

    public boolean updateLogin(String username,String password){
        SoapObject request=new SoapObject(NAMESPACE,METHOD_LOGIN);
        int wait=0;

        request.addProperty("user",username);
        request.addProperty("password",password);
        envelope=new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);

        Log.d("Before handler",resultString+"");
        NetworkHandler handler=new NetworkHandler(ACTION_LOGIN);
        handler.execute();
        Log.d("After handler",resultString+"");

        while(resultString==null&&wait<TIMEOUT){
            //tunggu
            try{
                wait+=1;
                Thread.sleep(1);
            }
            catch(InterruptedException ie){}
        }

        if(wait<TIMEOUT){
            //koneksi berhasil
            String[] temp=resultString.split(",");
            if(temp[0].equalsIgnoreCase("OK")){
                SharedPreferences.Editor e=context.getSharedPreferences("data",Context.MODE_PRIVATE).edit();
                e.putString("SessionId",temp[1]);
                e.commit();
                Log.d("Session ID",resultString);
                resultString=null;
                return true;
            }
            else{
                resultString=null;
                return false;
            }
        }
        else{
            resultString=null;
            return checkUsername(username, password);
        }
    }

    public void logout(String sessionId){
        SoapObject request=new SoapObject(NAMESPACE,METHOD_LOGOUT);
        int wait=0;

        request.addProperty("sid",sessionId);

        envelope=new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);

        NetworkHandler handler=new NetworkHandler(ACTION_LOGOUT);
        handler.execute();

        while(resultString==null&&wait<TIMEOUT){
            //tunggu
            try{
                wait+=1;
                Thread.sleep(1);
            }
            catch(InterruptedException ie){}
        }

        Log.d("Cek status",resultString);

        if(wait<TIMEOUT&&!sessionId.equals("")){
            if(resultString.split(",")[0].equalsIgnoreCase("OK")){
                //logout berhasil
                resultString=null;
                SharedPreferences.Editor e=context.getSharedPreferences("data",Context.MODE_PRIVATE).edit();
                e.clear();
                e.commit();
            }
        }
        else{
            SharedPreferences.Editor e=context.getSharedPreferences("data",Context.MODE_PRIVATE).edit();
            e.clear();
            e.commit();
        }
        resultString=null;
    }

    public void addUpdateProduct(int idUser,String sessionId,int idProd,String prodName,int price, int sellPrice,boolean isAdd){
        SoapObject request=new SoapObject(NAMESPACE,METHOD_ADDPRODUCT);
        int wait=0;

        request.addProperty("sid",sessionId);
        request.addProperty("namaproduk",prodName);
        request.addProperty("hargapokok",price+"");
        request.addProperty("hargajual",sellPrice+"");

        envelope=new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);

        NetworkHandler handler=new NetworkHandler(ACTION_ADDPRODUCT);
        handler.execute();

        while(resultString==null&&wait<TIMEOUT){
            //tunggu
            try{
                wait+=1;
                Thread.sleep(1);
            }
            catch(InterruptedException ie){}
        }

        Log.d("Cek status",resultString);

        if(wait<TIMEOUT&&!sessionId.equals("")){
            if(resultString.split(",")[1].equalsIgnoreCase("diregistrasi")){
                //registrasi/update berhasil
                resultString=null;
            }
            if(isAdd){
                //lokal, jika menambah
                insertProduct(prodName,price,sellPrice,idUser,true);
            }
            else{
                //lokal, jika update
                updateProduct(idProd,prodName,price,sellPrice,true);
            }
        }
        else{
            if(isAdd){
                //lokal, jika menambah
                insertProduct(prodName,price,sellPrice,idUser,false);
            }
            else{
                //lokal, jika update
                updateProduct(idProd,prodName,price,sellPrice,false);
            }
        }
        resultString=null;
    }

    public String[] getProductOnline(String sessionId,String prodName, int idProd){
        SoapObject request=new SoapObject(NAMESPACE,METHOD_GETPRODUCT);
        int wait=0;

        request.addProperty("sid",sessionId);
        request.addProperty("namaproduk",prodName);

        envelope=new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);

        NetworkHandler handler=new NetworkHandler(ACTION_GETPRODUCT);
        handler.execute();

        while(resultString==null&&wait<TIMEOUT){
            //tunggu
            try{
                wait+=1;
                Thread.sleep(1);
            }
            catch(InterruptedException ie){}
        }

        Log.d("Cek status",resultString);

        if(wait<TIMEOUT&&!sessionId.equals("")){
            if(!resultString.split(",")[1].equalsIgnoreCase("tidak ditemukan")){
                String[] temp=resultString.split(",");
                resultString=null;
                return temp;
            }
            resultString=null;
            return null;
        }
        resultString=null;
        return selectProductById(idProd);
    }

    public List<String> getKatalogOnline(String sessionId,int idUser){
        SoapObject request=new SoapObject(NAMESPACE,METHOD_GETKATALOG);
        int wait=0;

        request.addProperty("sid",sessionId);

        envelope=new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);

        NetworkHandler handler=new NetworkHandler(ACTION_GETKATALOG);
        handler.execute();

        while(resultString==null&&wait<TIMEOUT){
            //tunggu
            try{
                wait+=1;
                Thread.sleep(1);
            }
            catch(InterruptedException ie){}
        }

        Log.d("Cek status",resultString);

        if(wait<TIMEOUT&&!sessionId.equals("")){
            List<String> list=new ArrayList<String>();
            if(!resultString.equals("")){
                String[] temp=resultString.split(",");
                for(int i=0;i<temp.length;i++){
                    list.add(temp[i]);
                }
            }
            resultString=null;
            return list;
        }
        resultString=null;
        return selectProductName(idUser)[1];
    }

    public void addTransactionOnline(String sessionId,int idProd,String prodName,int sellPrice,int qty,String saleDate){
        SoapObject request=new SoapObject(NAMESPACE,METHOD_ADDTRANSAKSI);
        int wait=0;

        request.addProperty("sid",sessionId);
        request.addProperty("namaproduk",prodName);
        request.addProperty("hargajual",sellPrice+"");
        request.addProperty("qtyjual",qty+"");
        request.addProperty("tgljual",saleDate);

        envelope=new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);

        NetworkHandler handler=new NetworkHandler(ACTION_ADDTRANSAKSI);
        handler.execute();

        while(resultString==null&&wait<TIMEOUT){
            //tunggu
            try{
                wait+=1;
                Thread.sleep(1);
            }
            catch(InterruptedException ie){}
        }

        Log.d("Cek status",resultString);

        resultString=null;
        if(wait<TIMEOUT){
            insertTransaction(idProd,qty,saleDate,true);
        }
        else{
            insertTransaction(idProd,qty,saleDate,false);
        }
    }

    public List<String[]> getTransactionOnline(String sessionId,int idUser,String saleDate){
        SoapObject request=new SoapObject(NAMESPACE,METHOD_GETTRANSAKSI);
        int wait=0;

        request.addProperty("sid",sessionId);
        request.addProperty("tgldari",saleDate);

        envelope=new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);

        NetworkHandler handler=new NetworkHandler(ACTION_GETTRANSAKSI);
        handler.execute();

        while(resultString==null&&wait<TIMEOUT){
            //tunggu
            try{
                wait+=1;
                Thread.sleep(1);
            }
            catch(InterruptedException ie){}
        }

        Log.d("Cek status",resultString);

        if(wait<TIMEOUT&&!sessionId.equals("")){
            List<String[]> list=new ArrayList<String[]>();
            if(!resultString.equals("")){
                String[] temp=resultString.split(",");
                for(int i=0;i<temp.length;i+=4){
                    String[] temp2=new String[4];
                    for(int j=0;j<temp2.length;j++){
                        temp2[j]=temp[i+j];
                    }
                    list.add(temp2);
                }
            }
            resultString=null;
            return list;
        }
        resultString=null;
        return selectReport(idUser);
    }

    public void syncData(String sessionId,int idUser){
        int wait;
        SoapObject request;
        NetworkHandler handler;
        Cursor c;

        //bagian untuk meload data user ke web service
        c=db.rawQuery("select username,name,address,phone,birthDate,topProd from "+USER_TABLE+" where syncStatus=?",new String[]{"0"});
        if(c.getCount()>0){
            if(c.moveToFirst()){
                do{
                    wait=0;
                    request=new SoapObject(NAMESPACE,METHOD_REGISTER);
                    request.addProperty("user",c.getString(0));
                    request.addProperty("nama",c.getString(1));
                    request.addProperty("alamat",c.getString(2));
                    request.addProperty("nohp",c.getString(3));
                    request.addProperty("tgllahir",c.getString(4));
                    request.addProperty("produkunggulan",c.getString(5));

                    envelope=new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.setOutputSoapObject(request);

                    handler=new NetworkHandler(ACTION_REGISTER);
                    handler.execute();

                    while(resultString==null&&wait<TIMEOUT){
                        //tunggu
                        try{
                            wait+=1;
                            Thread.sleep(1);
                        }
                        catch(InterruptedException ie){}
                    }
                    resultString=null;
                }
                while(c.moveToNext());
            }
        }

        //bagian untuk reload data produk
        c=db.rawQuery("select prodName,price,sellPrice from "+PRODUCT_TABLE+" where idUser=? and syncStatus=?",new String[]{""+idUser,"0"});
        if(c.getCount()>0){
            //update ke web service
            if(c.moveToFirst()){
                do{
                    wait=0;
                    request=new SoapObject(NAMESPACE,METHOD_ADDPRODUCT);
                    request.addProperty("sid",sessionId);
                    request.addProperty("namaproduk",c.getString(0));
                    request.addProperty("hargapokok",c.getString(1));
                    request.addProperty("hargajual",c.getString(2));

                    envelope=new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.setOutputSoapObject(request);

                    handler=new NetworkHandler(ACTION_ADDPRODUCT);
                    handler.execute();

                    while(resultString==null&&wait<TIMEOUT){
                        //tunggu
                        try{
                            wait+=1;
                            Thread.sleep(1);
                        }
                        catch(InterruptedException ie){}
                    }
                    resultString=null;
                }
                while(c.moveToNext());
            }
        }
        c=db.rawQuery("select idProd where idUser=?",new String[]{idUser+""});
        wait=0;
        request=new SoapObject(NAMESPACE,METHOD_GETKATALOG);
        request.addProperty("sid",sessionId);

        envelope=new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);

        handler=new NetworkHandler(ACTION_GETKATALOG);
        handler.execute();

        while(resultString==null&&wait<TIMEOUT){
            //tunggu
            try{
                wait+=1;
                Thread.sleep(1);
            }
            catch(InterruptedException ie){}
        }
        String[] temp=resultString.split(",");
        resultString=null;
        if(c.getCount()<temp.length){
            //ada data baru di web service
            //TODO
            for(int i=0;i<temp.length;i++){
                request=new SoapObject(NAMESPACE,METHOD_GETPRODUCT);
                wait=0;

                request.addProperty("sid",sessionId);
                request.addProperty("namaproduk",temp[i]);

                envelope=new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.setOutputSoapObject(request);

                handler=new NetworkHandler(ACTION_GETPRODUCT);
                handler.execute();

                while(resultString==null&&wait<TIMEOUT){
                    //tunggu
                    try{
                        wait+=1;
                        Thread.sleep(1);
                    }
                    catch(InterruptedException ie){}
                }
                String[] temp2=resultString.split(",");
                resultString=null;
                if(!updateProduct(-1,temp2[0],Integer.parseInt(temp2[1]),Integer.parseInt(temp2[2]),true)){
                    insertProduct(temp2[0],Integer.parseInt(temp2[1]),Integer.parseInt(temp2[2]),idUser,true);
                }
            }
        }

        //bagian untuk reload data transaksi user yang sedang login
        //TODO sinkronisasi data
        c=db.rawQuery("select prodName,price,sellPrice from "+TRANSACTION_TABLE+" where idUser=? and syncStatus=?",new String[]{""+idUser,"0"});
        if(c.getCount()>0){
            //update ke web service
            if(c.moveToFirst()){
                do{
                    wait=0;
                    request=new SoapObject(NAMESPACE,METHOD_ADDPRODUCT);
                    request.addProperty("sid",sessionId);
                    request.addProperty("namaproduk",c.getString(0));
                    request.addProperty("hargapokok",c.getString(1));
                    request.addProperty("hargajual",c.getString(2));

                    envelope=new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.setOutputSoapObject(request);

                    handler=new NetworkHandler(ACTION_ADDPRODUCT);
                    handler.execute();

                    while(resultString==null&&wait<TIMEOUT){
                        //tunggu
                        try{
                            wait+=1;
                            Thread.sleep(1);
                        }
                        catch(InterruptedException ie){}
                    }
                    resultString=null;
                }
                while(c.moveToNext());
            }
        }
        c=db.rawQuery("select idProd where idUser=?",new String[]{idUser+""});
        wait=0;
        request=new SoapObject(NAMESPACE,METHOD_GETKATALOG);
        request.addProperty("sid",sessionId);

        envelope=new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);

        handler=new NetworkHandler(ACTION_GETKATALOG);
        handler.execute();

        while(resultString==null&&wait<TIMEOUT){
            //tunggu
            try{
                wait+=1;
                Thread.sleep(1);
            }
            catch(InterruptedException ie){}
        }
        temp=resultString.split(",");
        resultString=null;
        if(c.getCount()<temp.length){
            //ada data baru di web service
            //TODO
            for(int i=0;i<temp.length;i++){
                request=new SoapObject(NAMESPACE,METHOD_GETPRODUCT);
                wait=0;

                request.addProperty("sid",sessionId);
                request.addProperty("namaproduk",temp[i]);

                envelope=new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.setOutputSoapObject(request);

                handler=new NetworkHandler(ACTION_GETPRODUCT);
                handler.execute();

                while(resultString==null&&wait<TIMEOUT){
                    //tunggu
                    try{
                        wait+=1;
                        Thread.sleep(1);
                    }
                    catch(InterruptedException ie){}
                }
                String[] temp2=resultString.split(",");
                resultString=null;
                if(!updateProduct(-1,temp2[0],Integer.parseInt(temp2[1]),Integer.parseInt(temp2[2]),true)){
                    insertProduct(temp2[0],Integer.parseInt(temp2[1]),Integer.parseInt(temp2[2]),idUser,true);
                }
            }
        }
    }

    private static class OpenHelper extends SQLiteOpenHelper{
        OpenHelper(Context context){
            super(context,DATABASE_NAME,null,DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d("Current ver",""+db.getVersion());
            db.execSQL("CREATE TABLE IF NOT EXISTS "+USER_TABLE+" (idUser INTEGER PRIMARY KEY, username VARCHAR(128), name VARCHAR(50), address VARCHAR(255), phone VARCHAR(15), birthDate VARCHAR(8), topProd VARCHAR(50), syncStatus BOOLEAN)");
            db.execSQL("CREATE TABLE IF NOT EXISTS "+PRODUCT_TABLE+" (idProd INTEGER PRIMARY KEY, idUser INTEGER, prodName VARCHAR(50), price INTEGER, sellPrice INTEGER, syncStatus BOOLEAN)");
            db.execSQL("CREATE TABLE IF NOT EXISTS "+TRANSACTION_TABLE+" (idTrans INTEGER PRIMARY KEY, idProd INTEGER, qty INTEGER, saleDate VARCHAR(8), syncStatus BOOLEAN)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if(oldVersion<newVersion){
                onCreate(db);
            }
        }
    }

    private class NetworkHandler extends AsyncTask<Void,Void,Boolean>{
        private String action;
        public NetworkHandler(String action){
            this.action=action;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean connected=true;
            try{
                transport.call(action,envelope);
                SoapObject result=(SoapObject)envelope.bodyIn;
                String temp=result.getProperty("return").toString();
                temp=temp.replace("(","").replace(")","").replaceAll("\"","");
                resultString=temp;
            }
            catch(Exception e){
                e.printStackTrace();
                connected=false;
            }
            return connected;
        }
    }
}

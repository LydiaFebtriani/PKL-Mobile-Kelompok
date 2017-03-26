package com.pab.unpar.pklmobilekelompok;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LENOVO Z410_W8PRO on 3/4/2017.
 */

public class Soap extends Activity {
    String NAMESPACE = "http://schemas.xmlsoap.org/wsdl";
    String URL = "http://webtest.unpar.ac.id/pklws/pkl.php?wsdl";
    SoapObject request;
    SoapSerializationEnvelope envelope;

    private DataManipulator dh;

    String result;
    int TIMEOUT = 500;

//    private SharedPreferences sp;
//    private SharedPreferences.Editor se;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        sp = getSharedPreferences("dataProduk", MODE_PRIVATE);
//        Log.d("SharedPreferences",sp.contains("sessionId")+"");
//        se = sp.edit();
    }
    /*Output: user, nama, alamat, nohp, tgllahir, produkunggulan*/
    public String[] getPklOnline(String sessionId){
        String[] pkl = new String[6];

        request = new SoapObject(NAMESPACE,"getpkl");
        request.addProperty("sid",sessionId);
        envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);

        NetworkHandler handler = new NetworkHandler("getpkl");
        handler.execute();
        int time=tunggu();
        Log.d("Get PKL Result",result);

        if(time<TIMEOUT){
            if(!result.contains("NOK")){
                String[] temp = result.split(",");
                Log.d("Get PKL Result",result);
                Log.d("Get PKL",temp[0]+" "+temp[1]+" "+temp[2]);
                pkl[0] = temp[0].substring(2,temp[0].length()-1);
                pkl[1] = temp[1].substring(1,temp[1].length()-1);
                pkl[2] = temp[2].substring(1,temp[2].length()-1);
                pkl[3] = temp[3].substring(1,temp[3].length()-1);
                pkl[4] = temp[4].substring(1,temp[4].length()-1);
                pkl[5] = temp[5].substring(1,temp[5].length()-2);
            }
        }

        result = null;
        return pkl;
    }

    //******************** METHOD UNTUK MENUNGGU KONEKSI ********************//
    private int tunggu(){
        int time=0;
        while(result==null && time<TIMEOUT){
            try{
                time+=1;
                Thread.sleep(1);
            } catch (InterruptedException e){}
        }
        return time;
    }

    //******************** METHOD UNTUK LOGIN ********************//
    /* OUTPUT: ['sessionId','idUser','user','password'] */
    public String[] login(Context context, String username, String password){
        String[] sessionId = null;

        request = new SoapObject(NAMESPACE, "login");
        request.addProperty("user",username);
        request.addProperty("password",password);

        envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        Log.d("Before handler",result+"");
        NetworkHandler handler = new NetworkHandler("login");
        handler.execute();
        Log.d("After handler",result+"");
        int time = tunggu();
        Log.d("After while",result+"");

        sessionId = new String[5];

        dh = new DataManipulator(context);
        if(time<TIMEOUT){
            String[] soapResult = result.split(",");
            Log.d("If time<timeout","-"+soapResult[0]+"-"+soapResult[1]+"-");
            if(soapResult[0].contains("OK") && !soapResult[0].contains("NOK")){
                sessionId[0] = soapResult[1].substring(1,soapResult[1].length()-2);
                Log.d("Session id",soapResult[1]+" "+sessionId);
            }
        }
        result = null;
//        else {
            String[] select =  dh.select1User(new String[]{"email = \""+username+"\" AND","password = \""+password+"\""});
            Log.d("Login",select[0]+" "+sessionId[0]);
            if(select[0] == null){
//                sessionId = null;
                //Kalau tidak ada user di database lokal, register ke lokal
                if(sessionId[0] != null){
                    //Kalau sudah bisa konek, insert
                    String[] pkl = getPklOnline(sessionId[0]);
                    Log.d("Login getPkl",pkl[0]);
                    Long insert = dh.insertUser(pkl[0],pkl[4],pkl[1],pkl[2],pkl[3],pkl[4],pkl[5],true);
                    Log.d("Login insert db",insert+"");
                }
            }else{
//                String[] select = dh.select1User(new String[]{"email = \""+username+"\" AND","password = \""+password+"\""});
//                sessionId = new String[3];
                sessionId[1] = select[0];
                sessionId[2] = select[1];
                sessionId[3] = select[2];
            }
//        }
        return sessionId;
    }

    //******************** METHOD UNTUK LOGOUT ********************//
    /* OUTPUT: true = berhasil */
    public boolean logout(Context context,String sessionId){
        request = new SoapObject(NAMESPACE, "logout");
        request.addProperty("sid",sessionId);

        envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        NetworkHandler handler = new NetworkHandler("logout");
        handler.execute();
        int time = tunggu();
        Log.d("Logout time",time+" "+TIMEOUT);

        if(time<TIMEOUT){
            String[] soapResult = result.split(",");
            if(soapResult[0].equalsIgnoreCase("OK")){
                return true;
            }
        }
        return true;
    }

    //******************** METHOD UNTUK REGISTER PKL ********************//
    /* OUTPUT: true = berhasil */
    public boolean register(Context context,String email, String nama, String alamat, String noHp, String tgl, String produkUnggul){
        boolean res = false;
        boolean sync = false;

        request = new SoapObject(NAMESPACE, "regpkl");
        request.addProperty("user",email);
        request.addProperty("nama",nama);
        request.addProperty("alamat",alamat);
        request.addProperty("nohp",noHp);
        request.addProperty("tgllahir",tgl);
        request.addProperty("produkunggulan",produkUnggul);

        envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        Log.d("before handler register",result+"");
        NetworkHandler handler = new NetworkHandler("regpkl");
        handler.execute();
        Log.d("After handler register",result+"");
        int time = tunggu();
        Log.d("After while register",result+""+time+" "+TIMEOUT);

        if(time<TIMEOUT){
            result = result.substring(1,result.length()-1);
            if(result.contains("sukses")){
                res = true;
                Log.d("Register",result);
                sync = true;
            }
            Log.d("Time",time+"");
        }
        //else{
        dh = new DataManipulator(context);
        if(dh.select1User(new String[]{"email = \""+email+"\""})[0] == null){
            //Kalau belum ada pkl di lokal
            if(dh.insertUser(email,tgl,nama,alamat,noHp,tgl,produkUnggul,sync) == -1){
                //Jika insert tidak berhasil
                res = false;
            }
            else{
                res = true;
            }
            Log.d("Register",res+" "+email+" "+tgl);
        }
        //}
        result = null;
        return res;
    }

    //******************** METHOD UNTUK MENGAMBIL KATALOG ********************//
    /* OUTPUT: nama-nama produk */
    public String[] getKatalog(Context context,String sessionId){
        //Param sessionId berisi sessionId jika ada koneksi
        //Jika tidak berisi idUser
        String[] list=null;

        request = new SoapObject(NAMESPACE, "getkatalog");
        request.addProperty("sid",sessionId);

        envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        Log.d("before handler katalog",result+"");
        NetworkHandler handler = new NetworkHandler("getkatalog");
        handler.execute();
        Log.d("After handler katalog",result+"");
        int time = tunggu();

        if(time<TIMEOUT){
            Log.d("Katalog",result);
            if(result != "" && result.length()>4){
                list = result.split(",");
                Log.d("Katalog",list[0]);
//                if(list[1].contains("tidak ditemukan")){
//                    //Kalau sessionId tidak ditemukan atau berupa idUser
////                    dh = new DataManipulator(context);
////                    List<String[]> prodName = dh.selectAllProductName(Integer.parseInt(sessionId));
////                    for(int i=0;i<prodName.size();i++){
////                        list[i] = prodName.get(i)[1];
////                    }
//                }
//                else{
                    for(int i=0;i<list.length;i++){
                        list[i] = list[i].substring(2,list[i].length()-2);
                        Log.d("Katalog list",list[i]);
                    }
//                }
            }
        }
        else{
            //Kalau tidak ada koneksi langsung ambil dari database lokal
            dh = new DataManipulator(context);
            List<String[]> prodName = dh.selectAllProductName(Integer.parseInt(sessionId));
            list = new String[prodName.size()];
            for(int i=0;i<prodName.size();i++){
                list[i] = prodName.get(i)[1];
            }
            Log.d("Katalog Size",prodName.size()+"");
        }
        result = null;
        return list;
    }

    //******************** METHOD UNTUK MENGAMBIL DETAIL PRODUK TERTENTU ********************//
    /* OUTPUT: String[]{"namaproduk","hargapokok","hargajual"}*/
    public String[] getDetailProduk(Context context,String sessionId, String namaProduk){
        String[] list=null;

        request = new SoapObject(NAMESPACE, "getproduk");
        request.addProperty("sid",sessionId);
        request.addProperty("namaproduk",namaProduk);

        envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        NetworkHandler handler = new NetworkHandler("getproduk");
        handler.execute();
        int time = tunggu();

        if(time<TIMEOUT){
            result = result.substring(1,result.length()-1);
            list = result.split(",");
            if(!list[1].contains("tidak ditemukan")){
                for(int i=0;i<list.length;i++){
                    list[i] = list[i].substring(1,list[i].length()-1);
                    Log.d("Detail produk",list[i]);
                }
            }
            else{
                //Kalau sessionId tidak ditemukan atau berupa idUser
//                dh = new DataManipulator(context);
//                list = dh.select1FromProduk(new String[]{"namaProduk = \""+namaProduk+"\""});
            }
        }
        else{
            //Kalau tidak ada koneksi, ambil dari database lokal
            dh = new DataManipulator(context);
            list = dh.select1FromProduk(new String[]{"namaProduk = \""+namaProduk+"\""});
        }
        result = null;
        return list;
    }

    //******************** METHOD UNTUK MENAMBAH DAN MENGUPDATE PRODUK ********************//
    /* OUTPUT: true=berhasil */
    public boolean setAddProduk(Context context, String sessionId, int idUser, String namaProduk, String hargaPokok, String hargaJual){
        String[] list=null;

        request = new SoapObject(NAMESPACE, "regproduk");
        request.addProperty("sid",sessionId);
        request.addProperty("namaproduk",namaProduk);
        request.addProperty("hargapokok",hargaPokok);
        request.addProperty("hargajual",hargaJual);

        envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        NetworkHandler handler = new NetworkHandler("regproduk");
        handler.execute();
        int time = tunggu();

        if(time<TIMEOUT){
            if(result!=null){
                result = result.substring(1,result.length()-1);
                list = result.split(",");
                list[1] = list[1].substring(1,list[1].length()-1);
                System.out.println(list[1]);
                if(list[1].equalsIgnoreCase("diregistrasi")){
                    return true;
                }
            }
        }
//        else{
        dh = new DataManipulator(context);
        String[] produk = dh.select1FromProduk(new String[]{"namaProduk = \""+namaProduk+"\""});
        if(produk[0] == null){
            //Kalau tidak ada produk, insert
            if(dh.insertProduk(namaProduk,hargaPokok,hargaJual,idUser,false) != -1) return true;
        } else{
            //Kalau produk sudah ada, update
            dh.update1Produk(Integer.parseInt(produk[0]),namaProduk,hargaPokok,hargaJual,idUser,true);
            return true;
        }
//        }
        result = null;
        return false;
    }

    //******************** METHOD UNTUK MENAMBAH TRANSAKSI ********************//
    /* OUTPUT: true=berhasil */
    public boolean setAddTransaksi(Context context, String sessionId, int idUser, String namaProduk, String hargaJual, String qty, String tglJual){
        String[] list=null;

        request = new SoapObject(NAMESPACE, "regtransaksi");
        request.addProperty("sid",sessionId);
        request.addProperty("namaproduk",namaProduk);
        request.addProperty("hargajual",hargaJual);
        request.addProperty("qtyjual",qty);
        request.addProperty("tgljual",tglJual);

        envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        NetworkHandler handler = new NetworkHandler("regtransaksi");
        handler.execute();
        int time = tunggu();

        if(time<TIMEOUT){
            if(result!=null){
                result = result.substring(1,result.length()-1);
                list = result.split(",");
                if(list[1].substring(1,list[1].length()-1).equalsIgnoreCase("ditambahkan")){
                    return true;
                }
            }
        }
//        else{
            dh = new DataManipulator(context);
            int idProduk = Integer.parseInt(dh.select1FromProduk(new String[]{"idUser = \""+idUser+"\" AND","namaProduk = \""+namaProduk+"\""})[0]);
            if(dh.insertTransaksi(idUser, idProduk, Integer.parseInt(qty),hargaJual, tglJual, false) != -1) return true;
//        }
        result = null;
        return false;
    }

    //******************** METHOD UNTUK MENDAPATKAN REKAP TRANSAKSI ********************//
    /* OUTPUT: list=(String[]{"namaproduk","hargajual","qtyjual","tgljual"}),(...) */
    public ArrayList<String[]> getAllRekap(Context context, String sessionId, int bulan){
        ArrayList<String[]> list = new ArrayList<String[]>();
        String strBulan = String.format("%02d",bulan);

        request = new SoapObject(NAMESPACE, "gettransaksi");
        request.addProperty("sid",sessionId);
        if(bulan>0) request.addProperty("tgldari","2017"+strBulan+"01");
        else request.addProperty("tgldari","20170101");
        Log.d("Rekap bulan",strBulan+" "+bulan);

        envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        NetworkHandler handler = new NetworkHandler("gettransaksi");
        handler.execute();
        int time = tunggu();

        if(time<TIMEOUT){
            Log.d("Rekap result",result);
            if(result!=null){
                String[] temp = result.split(",");
                String[] res = new String[4];
                int idx =0 ;
                int ct =0;
                for(int i=0;i<temp.length;i++){
                    //temp[i] = temp[i].substring(1,temp[i].length()-1);
                    if(i%4 == 0){
                        temp[i] = temp[i].substring(1);
                    } else if((i+1)%4 == 0){
                        temp[i] = temp[i].substring(0,temp[i].length()-1);
                    }
                    Log.d("Rekap 1",temp[i]);
                    temp[i] = temp[i].substring(1,temp[i].length()-1);
                    Log.d("Rekap 2",temp[i]);

                    if(ct<3){
                        res[ct] = temp[i];
                        ct++;
                    } else{
                        res[ct] = temp[i];
                        ct=0;
                        //<TAMBAHAN PENGELOMPOKKAN REKAP>
                        if(bulan>0){
                            //String blnTemp= bulan+"";//String.format("%02d",bulan);
                            if(res[3]!=null&&res[3].length()>6&&res[3].substring(4,6).equals(strBulan)){
                                list.add(idx,res);
                            }
                            else if(Integer.parseInt(res[3].substring(4,6))>bulan){
                                //Kalau sudah melewati bulan langsung keluar
                                break;
                            }
                        }
                        else{
                            list.add(idx,res);
                        }
                        //</TAMBAHAN PENGELOMPOKKAN REKAP>
                        idx++;
                        res = new String[4];

                        Log.d("Rekap Array",res[0]+" "+res[1]+" "+res[2]+" "+res[3]);
                        Log.d("Rekap List",list.get(idx-1)[0]);
                    }
                }
            }
        }
        else{
            dh = new DataManipulator(context);
            list = (ArrayList<String[]>) dh.selectAllTransaksi(new String[]{"idUser = \""+sessionId+"\""});
            Log.d("Rekap dh size",list.size()+"");
        }
        result = null;
        return list;
    }

    public void syncData(Context context, String sessionId, int idUser){
        NetworkHandler handler;
        int time;
        List<String[]> list;

        /*BAGIAN REGISTER USER BARU*/
        list = dh.selectAllUser(new String[]{"syncStatus = \"0\""});
        for(int i=0;i<list.size();i++){
            request = new SoapObject(NAMESPACE,"regpkl");
            request.addProperty("user",list.get(i)[1]);
            request.addProperty("nama",list.get(i)[3]);
            request.addProperty("alamat",list.get(i)[4]);
            request.addProperty("nohp",list.get(i)[5]);
            request.addProperty("tgllahir",list.get(i)[6]);
            request.addProperty("produkunggulan",list.get(i)[7]);

            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            handler = new NetworkHandler("regpkl");
            handler.execute();
            time = tunggu();
            if(time<TIMEOUT){
                dh.update1UserStatus(idUser,true);
            }
            result = null;
        }
        time = 0;

        /*BAGIAN DATA PRODUK*/
        //*Bagian memasukkan data produk dari database ke webserver*//
        list = dh.selectAllProduk(new String[]{"idUser = \""+ (idUser+"") +"\" AND","syncStatus = \"0\""});
        for(int i=0;i<list.size();i++){
            request = new SoapObject(NAMESPACE,"regproduk");
            request.addProperty("sid",sessionId);
            request.addProperty("namaproduk",list.get(i)[1]);
            request.addProperty("hargapokok",list.get(i)[2]);
            request.addProperty("hargajual",list.get(i)[3]);

            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            handler = new NetworkHandler("addproduk");
            handler.execute();
            time = tunggu();
            if(time<TIMEOUT){
                dh.update1ProdukStatus(Integer.parseInt(list.get(i)[0]),true);
            }
            result = null;
        }
        //*Bagian memasukkan data produk dari webserver ke database*//
        list = dh.selectAllProduk(new String[]{"idUser = \""+ (idUser+"") +"\""});
        //Ambil produk dari webserver
        String[] katalog = getKatalog(context,sessionId);
        if(katalog.length > list.size()){
            for(int i=0;i<katalog.length;i++){
                //Ambil detail produk dari webserver
                String[] produk = getDetailProduk(context,sessionId, katalog[i]);
                boolean isTersedia = false;
                for(int j=0;j<list.size();j++){
                    //Loop untuk membandingkan
                    if(list.get(j)[1].equals(produk[0])){
                        isTersedia = true;
                        break;
                    }
                }

                if(!isTersedia){
                    //Produk di webserver tidak ada database
                    dh.insertProduk(produk[0],produk[1],produk[2],idUser,true);
                }
            }
        }
        time = 0;

        /*BAGIAN DATA TRANSAKSI*/
        //*Bagian memasukkan data transaksi dari database ke webserver*//
        list = dh.selectAllTransaksi(new String[]{"idUser = \""+ (idUser+"") +"\" AND", "syncStatus = \"0\""});
        for(int i=0;i<list.size();i++){
            //Ambil data produk di transaksi tersebut
            String produk = dh.select1NamaProduk(Integer.parseInt(list.get(i)[2]));

            request = new SoapObject(NAMESPACE,"regtransaksi");
            request.addProperty("sid",sessionId);
            request.addProperty("namaproduk",produk);
            request.addProperty("hargajual",list.get(i)[4]);
            request.addProperty("qtyjual",list.get(i)[3]);
            request.addProperty("tgljual",list.get(i)[5]);

            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            handler = new NetworkHandler("addtransaksi");
            handler.execute();
            time = tunggu();
            if(time<TIMEOUT){
                dh.update1TransaksiStatus(Integer.parseInt(list.get(i)[0]),true);
            }
            result = null;
        }
        //*Bagian memasukkan data transaksi dari webserver ke database*//
        list = dh.selectAllTransaksi(new String[]{"idUser = \""+ (idUser+"") +"\""});
        //Ambil transaksi dari webserver
        ArrayList<String[]> transaksi = getAllRekap(context,sessionId,0);
        if(transaksi.size() > list.size()){
            for(int i=0;i<transaksi.size();i++){
                //Ambil detail produk dari webserver
                String[] produk = getDetailProduk(context,sessionId, transaksi.get(i)[0]);
                boolean isTersedia = false;
                for(int j=0;j<list.size();j++){
                    //Loop untuk membandingkan
                    if(list.get(j)[1].equals(produk[0])){
                        isTersedia = true;
                        break;
                    }
                }

                if(!isTersedia){
                    String[] dhProduk = dh.select1FromProduk(new String[]{"namaProduk = \""+produk[0]+"\""});
                    //Transaksi di webserver tidak ada database
                    dh.insertTransaksi(idUser,Integer.parseInt(dhProduk[0]),Integer.parseInt(transaksi.get(i)[2]),transaksi.get(i)[1],transaksi.get(i)[3],true);
                }
            }
        }
        time = 0;
    }
    public void sync(Context context, String sessionId, int idUser){
//        syncDariWeb(context,sessionId,idUser);
//        syncDariDB(context,sessionId,idUser);
        dh = new DataManipulator(context);

        //Search user baru / syncStatus == 0
        List<String[]> allUser = dh.selectAllUser(new String[]{"syncStatus = \""+0+"\""});
        Log.d("Sync Register",allUser.isEmpty()+"");
        if(!allUser.isEmpty()){
            //Kalau ada user baru
            for(int i=0;i<allUser.size();i++){
                String[] temp = allUser.get(i);
                register(context,temp[1],temp[3],temp[4],temp[5],temp[6],temp[7]);
                Log.d("Sync Register",temp[0]+" "+temp[1]);
            }
        }

        //Search produk baru / syncStatus == 0
        List<String[]> allProduk = dh.selectAllProduk(new String[]{"idUser = \""+idUser+"\" AND", "syncStatus = \""+0+"\""});
        if(!allProduk.isEmpty()){
            //Kalau ada produk baru
            for(int i=0;i<allProduk.size();i++){
                String[] temp = allProduk.get(i);
                setAddProduk(context,sessionId,idUser,temp[1],temp[2],temp[3]);
            }
        }

        //Search transaksi baru / syncStatus == 0
        List<String[]> allTransaksi = dh.selectAllTransaksi(new String[]{"idUser = \""+idUser+"\" AND", "syncStatus = \""+0+"\""});
        if(!allTransaksi.isEmpty()){
            //Kalau ada transaksi bar
            for (int i=0;i<allTransaksi.size();i++){
                String[] temp = allTransaksi.get(i);
                //idTransaksi, idUser, idProduk, kuantitas, harga, tglJual,syncStatus
                String namaProduk = dh.select1FromProduk(new String[]{"idProduk = \""+temp[2]+"\""})[1];
                setAddTransaksi(context,sessionId,idUser,namaProduk,temp[4],temp[3],temp[5]);
            }
        }

        //Masukin produk dari webserver ke database lokal
        String[] namaProdukWeb = getKatalog(context,sessionId);
        //Ambil semua produk dari lokal
        allProduk = dh.selectAllProduk(new String[]{"idUser = \""+idUser+"\""});
        if(namaProdukWeb.length > allProduk.size()){
            int idxNext = allProduk.size();
            Log.d("Sync Produk Length",namaProdukWeb.length+" "+idxNext);
            while(idxNext<namaProdukWeb.length){
                String[] detailProduk = getDetailProduk(context,sessionId,namaProdukWeb[idxNext]);
                Long insert = dh.insertProduk(detailProduk[0],detailProduk[1],detailProduk[2],idUser,true);
                Log.d("Sync Insert Produk",insert+"");
                idxNext++;
            }
        }

        //Masukin transaksi dari webserver ke database lokal
        List<String[]> rekapWeb = getAllRekap(context,sessionId,0);
        List<String[]> rekapDB = dh.selectAllTransaksi(new String[]{"idUser = \""+idUser+"\""});
        Log.d("Sync rekap size",idUser+" "+rekapDB.size()+" "+rekapWeb.size());
        if(rekapWeb.size() > rekapDB.size()){
            int idxNext = rekapDB.size();
            Log.d("Sync rekap size",rekapDB.size()+" "+rekapWeb.size());
            while(idxNext<rekapWeb.size()){
                String[] transaksi = rekapWeb.get(idxNext);
                String idProduk = dh.select1FromProduk(new String[]{"namaProduk = \""+transaksi[0]+"\""})[0];
                dh.insertTransaksi(idUser,Integer.parseInt(idProduk),Integer.parseInt(transaksi[2]),transaksi[1],transaksi[3],true);
                idxNext++;
            }
        }
    }
//    public void syncDariWeb(Context context, String sessionId, int idUser){
//        String[] katalog = getKatalog(context,sessionId);
//        ArrayList<String[]> rekap = getAllRekap(context,sessionId);
//        dh = new DataManipulator(context);
//        boolean userBaru = false;
//
//        Log.d("SyncWeb",sessionId+" "+idUser);
//        //Kalo user blm ada d database
//        if(idUser == -1){
//            userBaru = true;
//            //Ambil data pkl dari webserver
//            String[] pkl = getPklOnline(sessionId);
//            Log.d("PKL",pkl[0]);
//            //Masukkan pkl ke database
//            long insert = dh.insertUser(pkl[0],pkl[4],pkl[1],pkl[2],pkl[3],pkl[4],pkl[5],true);
//            idUser = Integer.parseInt((dh.select1User(new String[]{"nama = \""+pkl[1]+"\""}))[0]);
//            Log.d("Insert Web, idUser",insert+" "+idUser+" "+pkl[0]+" "+pkl[4]);
//        }
//
//        //Masukkan atau update produk ke database
//        for(int i=0;i<katalog.length;i++){
//            String[] detail = getDetailProduk(context,sessionId,katalog[i]);
//            if(userBaru) dh.insertProduk(detail[0],detail[1],detail[2],idUser,true);
//            else{
//                int idProduk = Integer.parseInt(dh.select1FromProduk(new String[]{"idUser = \""+idUser+"\" AND","namaProduk = \""+detail[0]+"\""})[0]);
//                dh.update1Produk(idProduk,detail[0],detail[1],detail[2],idUser,true);
//            }
//        }
//
//        //Masukkan dan update transaksi ke database
//        for(int i=0;i<rekap.size();i++){
//            int idProduk = Integer.parseInt((dh.select1FromProduk(new String[]{"namaProduk = \""+rekap.get(i)[0]+"\""}))[0]);
//            if(userBaru) dh.insertTransaksi(idUser,idProduk,Integer.parseInt(rekap.get(i)[2]),rekap.get(i)[1],rekap.get(i)[3],true);
//            else{
//                int idTransaksi = Integer.parseInt((dh.select1FromTransaksi(new String[]{"idUser = \""+idUser+"\" AND","idProduk = \""+idProduk+"\""}))[0]);
//                dh.update1Transaksi(idTransaksi,idUser,idProduk,Integer.parseInt(rekap.get(i)[2]),rekap.get(i)[1],rekap.get(i)[3],true);
//            }
//        }
//    }
//    public void syncDariDB(Context context, String sessionId, int idUser){
//        dh = new DataManipulator(context);
//        List<String[]> allProduk = dh.selectAllProduk(new String[]{"idUser = \""+idUser+"\""});
//        List<String[]> allTransaksi = dh.selectAllTransaksi(new String[]{"idUser = \""+idUser+"\""});
//
//        Log.d("SyncDB",sessionId+" "+idUser);
//        //Kalau ada user baru di DB dan belum ada di webserver
//        if(sessionId == null){
//            //Ambil info user sesuai dengan idUser
//            String[] user = dh.select1User(new String[]{"idUser = \""+idUser+"\""});
//            boolean res = register(context,user[1],user[3],user[4],user[5],user[6],user[7]);
//            sessionId = login(context,user[1],user[2])[0];
//            Log.d("Insert DB, session",res+" "+sessionId);
//        }
//
//        //Masukkan atau update produk
//        for(int i=0;i<allProduk.size();i++){
//            setAddProduk(context,sessionId,allProduk.get(i)[1],allProduk.get(i)[2],allProduk.get(i)[3]);
//        }
//        //Masukkan atau update transaksi
//        for(int i=0;i<allTransaksi.size();i++){
//            String namaProduk = dh.select1FromProduk(new String[]{"idTransaksi = \""+allTransaksi.get(i)[0]+"\""})[1];
//            setAddTransaksi(context,sessionId,namaProduk,allTransaksi.get(i)[4],allTransaksi.get(i)[3],allTransaksi.get(i)[5]);
//        }
//    }
//    public void syncWithConnection(Context context, String sessionId, String idUser){
//        String[] katalog = getKatalog(context,sessionId);
//        ArrayList<String[]> rekap = getAllRekap(context,sessionId);
//        String[] pkl = getPklOnline(sessionId);
//        dh = new DataManipulator(context);
//
//        /* AMBIL DARI WEBSERVER (DOWNLOAD) */
//        String[] user = dh.select1User(new String[]{"nama = \""+pkl[1]+"\""});
//        int idUser = Integer.parseInt(user[0]);
//        //Kalo user belum ada di database lokal
//        if(user[0] == null){
//            //Insert ke database lokal
//            long insert = dh.insertUser(pkl[0],pkl[4],pkl[1],pkl[2],pkl[3],pkl[4],pkl[5],true);
//            idUser = Integer.parseInt((dh.select1User(new String[]{"nama = \""+pkl[1]+"\""}))[0]);
//        }
//        //Masukkan atau update produk ke database
//        for(int i=0;i<katalog.length;i++){
//            String[] detail = getDetailProduk(context,sessionId,katalog[i]);
//            if(user[0] == null){
//                //Insert produk kalau user baru
//                dh.insertProduk(detail[0],detail[1],detail[2],idUser,true);
//            }
//            else{
//                String[] produk = dh.select1FromProduk(new String[]{"idUser = \""+idUser+"\" AND","namaProduk = \""+detail[0]+"\""});
//                if(produk[0] == null){
//                    //Produk belum ada di database, insert langsung
//                    dh.insertProduk(detail[0],detail[1],detail[2],idUser,true);
//                } else{
//                    //Produk sudah ada, update produk
//                    int idProduk = Integer.parseInt(produk[0]);
//                    dh.update1Produk(idProduk,detail[0],detail[1],detail[2],idUser,true);
//                }
//            }
//        }
//        //Masukkan atau update transaksi ke database
//        for(int i=0;i<rekap.size();i++){
//            int idProduk = Integer.parseInt((dh.select1FromProduk(new String[]{"namaProduk = \""+rekap.get(i)[0]+"\""}))[0]);
//            if(user[0] == null){
//                //Insert transaksi kalau user baru
//                dh.insertTransaksi(idUser,idProduk,Integer.parseInt(rekap.get(i)[2]),rekap.get(i)[1],rekap.get(i)[3],true);
//            }
//            else{
//                String[] transaksi = dh.select1FromTransaksi(new String[]{"idUser = \""+idUser+"\" AND","idProduk = \""+idProduk+"\""});
//                if(transaksi[0] == null){
//                    //Kalau bukan user baru tapi berupa transaksi baru
//                    dh.insertTransaksi(idUser,idProduk,Integer.parseInt(rekap.get(i)[2]),rekap.get(i)[1],rekap.get(i)[3],true);
//                }
////                int idTransaksi = Integer.parseInt((dh.select1FromTransaksi(new String[]{"idUser = \""+idUser+"\" AND","idProduk = \""+idProduk+"\""}))[0]);
////                dh.update1Transaksi(idTransaksi,idUser,idProduk,Integer.parseInt(rekap.get(i)[2]),rekap.get(i)[1],rekap.get(i)[3],true);
//            }
//        }
//
//        /* AMBIL DARI DATABASE (UPLOAD) */
//
//    }

    /* KELAS HANDLER */
    private class NetworkHandler extends AsyncTask<Void,Void,Boolean>{
        private String SOAP_ACTION;

        protected NetworkHandler (String action){
            this.SOAP_ACTION = action;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean res = false;
            try{
                HttpTransportSE transport = new HttpTransportSE(URL);
                transport.call(SOAP_ACTION,envelope);
                result = ((SoapObject) envelope.bodyIn).getProperty("return").toString();
                res=true;
            } catch (Exception e){
                e.printStackTrace();
            }
            return res;
        }

        @Override
        protected void onPostExecute(Boolean a){
            boolean res = a;
            if(res){

            } else{
                Log.d("Error","Soap connection");
            }
        }
    }
}

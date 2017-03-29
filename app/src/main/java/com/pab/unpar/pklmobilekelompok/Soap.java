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
    int TIMEOUT = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        sessionId = new String[4];

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
                //Kalau tidak ada user di database lokal, register ke lokal
                //id == null
                if(sessionId[0] != null){
                    //Kalau sudah bisa konek, insert
                    String[] pkl = getPklOnline(sessionId[0]);
                    Log.d("Login getPkl",pkl[0]);
                    Long insert = dh.insertUser(pkl[0],pkl[4],pkl[1],pkl[2],pkl[3],pkl[4],pkl[5],true);
                    Log.d("Login insert db",insert+"");
                    //user, nama, alamat, nohp, tgllahir, produkunggulan
                    String[] infoUserDB = dh.select1User(new String[]{"nama = \""+pkl[0]+"\""});
                    sessionId[1] = infoUserDB[0];
                    sessionId[2] = infoUserDB[1];
                    sessionId[3] = infoUserDB[2];
                }
            }
            else{
                sessionId[1] = select[0];
                sessionId[2] = select[1];
                sessionId[3] = select[2];
            }
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
                for(int i=0;i<list.length;i++){
                    list[i] = list[i].substring(2,list[i].length()-2);
                    Log.d("Katalog list",list[i]);
                }
            }
        }
        else{
            //Kalau tidak ada koneksi langsung ambil dari database lokal
            dh = new DataManipulator(context);
            List<String[]> prodName = dh.selectAllProductName(Integer.parseInt(sessionId));
            if(prodName.size() > 0){
                list = new String[prodName.size()];
                for(int i=0;i<prodName.size();i++){
                    list[i] = prodName.get(i)[1];
                    Log.d("Katalog list",list[i]);
                }
                Log.d("Katalog Size",prodName.size()+"");
            }
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
            list = new String[3];
            String[] produk = dh.select1FromProduk(new String[]{"idUser = \""+sessionId+"\" AND","namaProduk = \""+namaProduk+"\""});
            Log.d("Detail Produk",sessionId+" "+namaProduk+" "+produk[1]);
            list[0] = produk[1];
            list[1] = produk[2];
            list[2] = produk[3];
        }
        result = null;
        return list;
    }

    //******************** METHOD UNTUK MENAMBAH DAN MENGUPDATE PRODUK ********************//
    /* OUTPUT: true=berhasil */
    public boolean setAddProduk(Context context, String sessionId, int idUser, String namaProduk, String hargaPokok, String hargaJual){
        boolean res = false;
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

        boolean sync = false;
        if(time<TIMEOUT){
            if(result!=null){
                result = result.substring(1,result.length()-1);
                list = result.split(",");
                list[1] = list[1].substring(1,list[1].length()-1);
                System.out.println(list[1]);
                if(list[1].equalsIgnoreCase("diregistrasi")){
                    res = true;
                }
                sync = true;
            }
        }

        //BAGIAN DATABASE LOKAL
        dh = new DataManipulator(context);
        String[] produk = dh.select1FromProduk(new String[]{"idUser = \""+idUser+"\" AND", "namaProduk = \""+namaProduk+"\""});
        Log.d("Tambah produk",produk[0]+"");
        if(produk[0] == null){
            //Kalau tidak ada produk, insert
            if(dh.insertProduk(namaProduk,hargaPokok,hargaJual,idUser,sync) != -1){
                res = true;
                Log.d("Insert produk ke DB",res+"");
            }
        } else{
            //Kalau produk sudah ada, update
            dh.update1Produk(Integer.parseInt(produk[0]),namaProduk,hargaPokok,hargaJual,idUser,sync);
            Log.d("Update produk ke DB", "Berhasil "+produk[0]+" "+produk[1]+" "+idUser+" "+sync);
            res = true;
        }
        result = null;
        return res;
    }

    //******************** METHOD UNTUK MENAMBAH TRANSAKSI ********************//
    /* OUTPUT: true=berhasil */
    public boolean setAddTransaksi(Context context, String sessionId, int idUser, String namaProduk, String hargaJual, String qty, String tglJual){
        boolean res = false;
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

        boolean sync = false;
        if(time<TIMEOUT){
            if(result!=null){
                result = result.substring(1,result.length()-1);
                list = result.split(",");
                if(list[1].substring(1,list[1].length()-1).equalsIgnoreCase("ditambahkan")){
                    res = true;
                }
                sync = true;
            }
        }

        //BAGIAN DATABASE LOKAL
        dh = new DataManipulator(context);
        int idProduk = Integer.parseInt(dh.select1FromProduk(new String[]{"idUser = \""+idUser+"\" AND","namaProduk = \""+namaProduk+"\""})[0]);
        //Jika ada koneksi, langsung insert dengan syncStatus true
        if(dh.insertTransaksi(idUser, idProduk, Integer.parseInt(qty),hargaJual, tglJual, sync) != -1) res = true;

        result = null;
        return res;
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
                            Log.d("Filter check",""+res[3].substring(4,6).equals(strBulan));
                            if(res[3]!=null&&res[3].length()>6&&res[3].substring(4,6).equals(strBulan)){
                                list.add(idx,res);
                                idx++;
                            }
                        }
                        else{
                            list.add(idx,res);
                            idx++;
                        }
                        //</TAMBAHAN PENGELOMPOKKAN REKAP>
                        res = new String[4];

                        Log.d("Rekap Array",res[0]+" "+res[1]+" "+res[2]+" "+res[3]);
//                        Log.d("Rekap List",list.get(idx-1)[0]);
                    }
                }
            }
        }
        else{
            dh = new DataManipulator(context);
            //idTransaksi, idUser, idProduk, kuantitas, harga, tglJual
            List<String[]> tempList = dh.selectAllTransaksi(new String[]{"idUser = \""+sessionId+"\""});
            for(int i=0;i<tempList.size();i++){
                String tempBln = tempList.get(i)[5].substring(4,6);
                Log.d("Rekap tempBln",tempBln+" "+strBulan);
                if(bulan==0 || tempBln.equals(strBulan)){
                    String[] transaksi = new String[4];
                    String nama = dh.select1NamaProduk(Integer.parseInt(tempList.get(i)[2]));
                    transaksi[0] = nama;
                    transaksi[1] = tempList.get(i)[4];
                    transaksi[2] = tempList.get(i)[3];
                    transaksi[3] = tempList.get(i)[5];
                    list.add(i,transaksi);
                }
            }
            Log.d("Rekap dh size",sessionId+" "+list.size()+"");
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
        dh = new DataManipulator(context);

        /* SYNC DARI DATABASE LOKAL KE WEBSERVER */
        //Search user baru / syncStatus == 0
        List<String[]> allUser = dh.selectAllUser(new String[]{"syncStatus = \""+0+"\""});
        Log.d("Sync Register",allUser.isEmpty()+"");
        if(!allUser.isEmpty()){
            //Kalau ada user baru
            for(int i=0;i<allUser.size();i++){
                String[] temp = allUser.get(i);
                register(context,temp[1],temp[3],temp[4],temp[5],temp[6],temp[7]);
                dh.update1UserStatus(Integer.parseInt(temp[0]),true);
                Log.d("Sync Register",temp[0]+" "+temp[1]);
            }
        }
        //Search produk baru / syncStatus == 0
        List<String[]> allProduk = dh.selectAllProduk(new String[]{"idUser = \""+idUser+"\""});
        Log.d("Sync produk",idUser+" "+allProduk.isEmpty());
        if(!allProduk.isEmpty()){
            //Kalau ada produk baru
            for(int i=0;i<allProduk.size();i++){
                String[] temp = allProduk.get(i);
                if(temp[4].equals("false")){
                    setAddProduk(context,sessionId,idUser,temp[1],temp[2],temp[3]);
                    dh.update1ProdukStatus(Integer.parseInt(temp[0]),true);
                    Log.d("Sync Produk",temp[0]+" "+temp[1]+" "+temp[2]+" "+temp[3]);
                }
            }
        }
        //Search transaksi baru / syncStatus == 0
        List<String[]> allTransaksi = dh.selectAllTransaksi(new String[]{"idUser = \""+idUser+"\""});
        if(!allTransaksi.isEmpty()){
            //Kalau ada transaksi bar
            for (int i=0;i<allTransaksi.size();i++){
                String[] temp = allTransaksi.get(i);
                if(temp[6].equals("false")){
                    //idTransaksi, idUser, idProduk, kuantitas, harga, tglJual,syncStatus
                    String namaProduk = dh.select1FromProduk(new String[]{"idProduk = \""+temp[2]+"\""})[1];
                    setAddTransaksi(context,sessionId,idUser,namaProduk,temp[4],temp[3],temp[5]);
                    dh.update1TransaksiStatus(Integer.parseInt(temp[0]),true);
                }
            }
        }

        /* SYNC DARI WEBSERVER KE DATABASE LOKAL */
        //Masukin produk
        String[] namaProdukWeb = getKatalog(context,sessionId);
        allProduk = dh.selectAllProduk(new String[]{"idUser = \""+idUser+"\""});
        if(namaProdukWeb != null && !allProduk.isEmpty()){
            Log.d("Sync produk length",namaProdukWeb.length+" "+allProduk.size());
            Log.d("Sync produk 2",allProduk.get(1)[0]+" "+allProduk.get(1)[4]);
            int iWeb = 0;
            int iDB = 0;
            while(iWeb < namaProdukWeb.length && iDB < allProduk.size()){
                String[] detailWeb = getDetailProduk(context,sessionId,namaProdukWeb[iWeb]);
                String[] detailDB = allProduk.get(iDB);

                Log.d("Sync produk web",detailWeb[0]+" "+detailWeb[1]);
                Log.d("Sync produk DB",detailDB[1]+" "+detailDB[2]+" "+detailDB[3]);
                if(!detailWeb[0].equals(detailDB[1]) || !detailWeb[1].equals(detailDB[2]) || !detailWeb[2].equals(detailDB[3])){
                    //Nama, harga pokok, harga jual ada yang tidak sama
                    dh.update1Produk(Integer.parseInt(detailDB[0]),detailWeb[0],detailWeb[1],detailWeb[2],idUser,true);
                }
                iWeb++;
                iDB++;
            }
            if(iWeb != namaProdukWeb.length){
                while(iWeb < namaProdukWeb.length){
                    String[] detailWeb = getDetailProduk(context,sessionId,namaProdukWeb[iWeb]);
                    dh.insertProduk(detailWeb[0],detailWeb[1],detailWeb[2],idUser,true);
                    iWeb++;
                }
            }
        }
        //Masukin transaksi
        ArrayList<String[]> transaksi = getAllRekap(context,sessionId,0);
        allTransaksi = dh.selectAllTransaksi(new String[]{"idUser = \""+idUser+"\""});
        Log.d("Sync transaksi",!transaksi.isEmpty()+" "+!allTransaksi.isEmpty());
        if(!transaksi.isEmpty() && !allTransaksi.isEmpty()){
            int iWeb = 0;
            int iDB = 0;
            Log.d("Sync transaksi",transaksi.size()+" "+allTransaksi.size());
            while(iWeb < transaksi.size() && iDB < allTransaksi.size()){
                String[] transaksiWeb = transaksi.get(iWeb);
                String[] transaksiDB = allTransaksi.get(iDB);
                String namaProduk = dh.select1FromProduk(new String[]{"idProduk = \""+transaksiDB[2]+"\""})[1];

                Log.d("Sync transaksi web",transaksiWeb[0]+" "+transaksiWeb[1]+" "+transaksiWeb[2]);
                Log.d("Sync transaksi DB",namaProduk+" "+transaksiDB[4]+" "+transaksiDB[3]+" "+transaksiDB[5]);
                if(transaksiWeb[0].equals(namaProduk) || transaksiWeb[1].equals(transaksiDB[4]) || transaksiWeb[2].equals(transaksiDB[5])){
                    dh.update1Transaksi(Integer.parseInt(transaksiDB[0]),idUser,Integer.parseInt(transaksiDB[2]),Integer.parseInt(transaksiDB[3]),transaksiDB[4],transaksiDB[5],true);
                    Log.d("Sync transaksi","Berhasil");
                }
                iWeb++;
                iDB++;
            }
            if(iWeb != transaksi.size()){
                while(iWeb < transaksi.size()){
                    String[] transaksiWeb = transaksi.get(iWeb);
                    String idProduk = dh.select1FromProduk(new String[]{"namaProduk = \""+transaksiWeb[0]+"\""})[0];
                    Long insert = dh.insertTransaksi(idUser,Integer.parseInt(idProduk),Integer.parseInt(transaksiWeb[2]),transaksiWeb[1],transaksiWeb[3],true);
                    Log.d("Sync transaksi insert",insert+"");
                    iWeb++;
                }
            }
        }
//
//        if(namaProdukWeb != null && !allProduk.isEmpty()){
//            if(namaProdukWeb.length > allProduk.size()){
//                int idxNext = allProduk.size();
//                Log.d("Sync Produk Length",namaProdukWeb.length+" "+idxNext);
//                while(idxNext<namaProdukWeb.length){
//                    String[] detailProduk = getDetailProduk(context,sessionId,namaProdukWeb[idxNext]);
//                    Long insert = dh.insertProduk(detailProduk[0],detailProduk[1],detailProduk[2],idUser,true);
//                    Log.d("Sync Insert Produk",insert+"");
//                    idxNext++;
//                }
//            }
//        }


        /*//Masukin transaksi dari webserver ke database lokal
        List<String[]> rekapWeb = getAllRekap(context,sessionId,0);
        //namaproduk","hargajual","qtyjual","tgljual"}
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
        }*/
    }

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

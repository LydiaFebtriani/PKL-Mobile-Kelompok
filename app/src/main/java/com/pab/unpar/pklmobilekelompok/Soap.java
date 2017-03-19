package com.pab.unpar.pklmobilekelompok;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

/**
 * Created by LENOVO Z410_W8PRO on 3/4/2017.
 */

public class Soap extends Activity {
    String NAMESPACE = "http://schemas.xmlsoap.org/wsdl";
    String URL = "http://webtest.unpar.ac.id/pklws/pkl.php?wsdl";
    SoapObject request;
    SoapSerializationEnvelope envelope;

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
    /* OUTPUT: session id */
    public String login(String username, String password){
        String sessionId = null;

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

        if(time<TIMEOUT){
            String[] soapResult = result.split(",");
            Log.d("If time<timeout","-"+soapResult[0]+"-"+soapResult[1]+"-");
            if(soapResult[0].contains("OK") && !soapResult[0].contains("NOK")){
                sessionId = soapResult[1].substring(1,soapResult[1].length()-2);
                Log.d("Session id",soapResult[1]+" "+sessionId);
            }
        }
        result = null;
        return sessionId;
    }

    //******************** METHOD UNTUK LOGOUT ********************//
    /* OUTPUT: true = berhasil */
    public boolean logout(String sessionId){
        request = new SoapObject(NAMESPACE, "logout");
        request.addProperty("sid",sessionId);

        envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        NetworkHandler handler = new NetworkHandler("logout");
        handler.execute();
        int time = tunggu();

        if(time<TIMEOUT){
            String[] soapResult = result.split(",");
            if(soapResult[0].equalsIgnoreCase("OK")){
                return true;
            }
        }
        return false;
    }

    //******************** METHOD UNTUK REGISTER PKL ********************//
    /* OUTPUT: true = berhasil */
    public boolean register(String email, String nama, String alamat, String noHp, String tgl, String produkUnggul){
        boolean res = false;

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
            }
            Log.d("Time",time+"");
        }
        result = null;
        return res;
    }

    //******************** METHOD UNTUK MENGAMBIL KATALOG ********************//
    /* OUTPUT: nama-nama produk */
    public String[] getKatalog(String sessionId){
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
        result = null;
        return list;
    }

    //******************** METHOD UNTUK MENGAMBIL DETAIL PRODUK TERTENTU ********************//
    /* OUTPUT: String[]{"namaproduk","hargapokok","hargajual"}*/
    public String[] getDetailProduk(String sessionId, String namaProduk){
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
        }
        result = null;
        return list;
    }

    //******************** METHOD UNTUK MENAMBAH DAN MENGUPDATE PRODUK ********************//
    /* OUTPUT: true=berhasil */
    public boolean setAddProduk(String sessionId, String namaProduk, String hargaPokok, String hargaJual){
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
        return false;
    }

    //******************** METHOD UNTUK MENAMBAH TRANSAKSI ********************//
    /* OUTPUT: true=berhasil */
    public boolean setAddTransaksi(String sessionId, String namaProduk, String hargaJual, String qty, String tglJual){
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
        return false;
    }

    //******************** METHOD UNTUK MENDAPATKAN REKAP TRANSAKSI ********************//
    /* OUTPUT: list=(String[]{"namaproduk","hargajual","qtyjual","tgljual"}),(...) */
    //Ada tambahan untuk pengelompokkan rekap
    public ArrayList<String[]> getRekap(String sessionId,int bulan){
        ArrayList<String[]> list = new ArrayList<String[]>();

        request = new SoapObject(NAMESPACE, "gettransaksi");
        request.addProperty("sid",sessionId);
        request.addProperty("tgldari","20160101");

        envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        NetworkHandler handler = new NetworkHandler("gettransaksi");
        handler.execute();
        int time = tunggu();

        if(time<TIMEOUT){
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
                            String blnTemp=String.format("%02d",bulan);
                            if(res[3]!=null&&res[3].length()>6&&res[3].substring(4,6).equals(blnTemp)){
                                list.add(idx,res);
                            }
                        }
                        else{
                            list.add(idx,res);
                        }
                        //</TAMBAHAN PENGELOMPOKKAN REKAP>
                        idx++;
                        res = new String[4];

                        Log.d("Array rekap",res[0]+" "+res[1]+" "+res[2]+" "+res[3]);
                        Log.d("List rekap",list.get(idx-1)[0]);
                    }
                }
            }
        }
        return list;
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

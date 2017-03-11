package com.example.i14059.ui;

import android.os.StrictMode;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ferdi on 05/03/2017.
 */

public class SoapImplementation {
    String NAMESPACE = "http://schemas.xmlsoap.org/wsdl/";
    String URL = "http://webtest.unpar.ac.id/pklws/pkl.php";
    //http://webtest.unpar.ac.id/pklws/pkltest.php?name=login
    HttpTransportSE androidHttpTransport;
    String SOAP_ACTIONLOGIN = "urn:pkl#getpkl";
    String SOAP_ACTIONPRODUK = "urn:pkl#getproduk";
    String SOAP_ACTIONREGISTRASIUSER = "urn:pkl#regpkl";
    String SOAP_ACTIONGETKATALOG = "urn:pkl#getkatalog";
    String SOAP_ACTIONINSPRODUK = "urn:pkl#addproduk";
    String SOAP_ACTIONDELPRODUK = "urn:pkl#delproduk";
    String SOAP_ACTIONREGTRANS = "urn:pkl#addtransaksi";
    String SOAP_ACTIONLOGOUT = "urn:pkl#logout";
    String METHOD_MEGETTRANS ="gettransaksi";
    String METHOD_NAMELOGIN = "login";
    String METHOD_NAMEREGISTRASI = "regpkl";
    String METHOD_NAMEKATALOG = "getkatalog";
    String METHOD_NAMEPRODUK = "getproduk";
    String METHOD_NAMEINSPRODUK ="regproduk";
    String METHOD_NAMEDELPRODUK ="delproduk";
    String METHOD_NAMEREGTRANS = "regtransaksi";
    String SOAP_ACTIONGETTRANS ="urn:pkl#gettransaksi";
    String METHOD_NAMELOGOUT = "logout";
    SoapSerializationEnvelope envelope;
    private String SID;
    private ArrayList<produk> kumpulanProduk;
    private int hargaTotal;

    public String getHargaTotal() {

        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

        formatRp.setCurrencySymbol("Rp. ");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');
        kursIndonesia.setDecimalFormatSymbols(formatRp);

        return kursIndonesia.format(hargaTotal);

    }

    public String getSID() {
        return SID;
    }

    public ArrayList<produk> getKumpulanProduk() {
        return kumpulanProduk;
    }

    public void setSID(String SID) {
        this.SID = SID;
    }

    public SoapImplementation() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        androidHttpTransport = new HttpTransportSE(URL);

    }

    //insemail,insnama,insalamat,insnohp,instanggal,insprodUnggul
    //nama, alamat, nohp, tgllahir, produkunggulan
    public boolean insertProfil(String email, String nama, String alamat, String noHp, String tanggal, String produkUnggul) {
        boolean res = true;
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAMEREGISTRASI);
        request.addProperty("user", email);
        request.addProperty("nama", nama);
        request.addProperty("alamat", alamat);
        request.addProperty("nohp", noHp);
        request.addProperty("tgllahir", tanggal);
        request.addProperty("produkunggulan", produkUnggul);

        envelope.setOutputSoapObject(request);

        try {
            androidHttpTransport.call(SOAP_ACTIONREGISTRASIUSER, envelope);
            SoapObject resultRequestSOAP = (SoapObject) envelope.bodyIn;
            String a = resultRequestSOAP.toString().substring(24, 29);
            System.out.println(a);
            if (a.equals("sukses")) {
                res = true;
                System.out.println(resultRequestSOAP);
            } else {
                res = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public boolean login(String username, String password) {
        boolean res = true;

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAMELOGIN);
        request.addProperty("user", username);
        request.addProperty("password", password);

        envelope.setOutputSoapObject(request);
        try {
            androidHttpTransport.call(SOAP_ACTIONLOGIN, envelope);
            String sid = envelope.getResponse().toString();
            String value = sid.substring(7, sid.length() - 2);
            String penentuan = sid.substring(2, 4);
            if (penentuan.equals("NOK")) {
                res = false;
            } else {
                res = true;
                this.setSID(value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;

    }

    public boolean logout(String id){
        boolean res = true;

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAMELOGOUT);
        request.addProperty("sid", id);
        envelope.setOutputSoapObject(request);
        try{
            androidHttpTransport.call(SOAP_ACTIONLOGOUT,envelope);
            res=true;
        }catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }

    public List<String> getKatalog(String id){

        ArrayList<String> katalog = new ArrayList<String>();
        kumpulanProduk=new ArrayList<produk>();
        SoapObject request = new SoapObject(NAMESPACE,METHOD_NAMEKATALOG);
        request.addProperty("sid",id);
        envelope.setOutputSoapObject(request);
        try{
            androidHttpTransport.call(SOAP_ACTIONGETKATALOG,envelope);
            String res = envelope.getResponse().toString();

            String [] jawaban = res.split(",");
            String [] tanpaKurungdanPetik = new String[jawaban.length];

            for (int i = 0;i<tanpaKurungdanPetik.length;i++) {

                tanpaKurungdanPetik[i] = jawaban[i].substring(2, jawaban[i].length()-2);
            }


            for (int i = 0;i<tanpaKurungdanPetik.length;i++){

                if(tanpaKurungdanPetik[i].equals("")){
                    break;
                }
                else{
                    katalog.add(tanpaKurungdanPetik[i]);
                    SoapObject requestProduk = new SoapObject(NAMESPACE,METHOD_NAMEPRODUK);

                    requestProduk.addProperty("sid",id);
                    requestProduk.addProperty("namaproduk",tanpaKurungdanPetik[i]);
                    envelope.setOutputSoapObject(requestProduk);
                    try{
                        androidHttpTransport.call(SOAP_ACTIONPRODUK,envelope);
                        String res2 = envelope.getResponse().toString();

                        String res3 = res2.substring(1,res2.length()-1);

                        String[] arrProduk = res3.split(",");
                        int ct = 1;
                        produk pro = new produk();
                        for (int j = 0;j<arrProduk.length;j++){
                            String a = (arrProduk[j].substring(1,arrProduk[j].length()-1));
                            if(ct==1){

                                pro.setNamaProduk(a);
                            }
                            else if(ct==2){

                                pro.setHargaProduk(Integer.parseInt(a));
                            }
                            else if(ct==3){

                                pro.setHargaJual(Integer.parseInt(a));
                            }
                            ct+=1;
                        }
                        pro.setIdOwner((id));
                        this.kumpulanProduk.add(pro);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return katalog;
    }

    public boolean insertProduk(produk pro){
        String namaProduk = pro.getNamaProduk();
        System.out.println("a"+namaProduk);
        String hargaProduk = pro.getHargaProduk()+"";
        System.out.println("b"+hargaProduk);
        String hargaJual = pro.getHargaJual()+"";
        System.out.println("c"+hargaJual);
        String id = pro.getIdOwner()+"";
        System.out.println("d"+id);

        SoapObject request = new SoapObject(NAMESPACE,METHOD_NAMEINSPRODUK);
        request.addProperty("sid",id);
        request.addProperty("namaproduk",namaProduk);
        request.addProperty("hargapokok",hargaProduk);
        request.addProperty("hargajual",hargaJual);
        envelope.setOutputSoapObject(request);
        try{

            androidHttpTransport.call(SOAP_ACTIONINSPRODUK, envelope);


        }catch (Exception e){
            e.printStackTrace();
        }

        return true;
    }


    public boolean updateProduk(String namaDulu,produk pro){
        boolean resVal=true;
        String namaProduk = pro.getNamaProduk();
        System.out.println("a"+namaProduk);
        String hargaProduk = pro.getHargaProduk()+"";
        System.out.println("b"+hargaProduk);
        String hargaJual = pro.getHargaJual()+"";
        System.out.println("c"+hargaJual);
        String id = pro.getIdOwner()+"";
        System.out.println("d"+id);

        SoapObject request = new SoapObject(NAMESPACE,METHOD_NAMEINSPRODUK);
        request.addProperty("sid",id);
        request.addProperty("namaproduk",namaProduk);
        request.addProperty("hargapokok",hargaProduk);
        request.addProperty("hargajual",hargaJual);
        envelope.setOutputSoapObject(request);
        try{
            androidHttpTransport.call(SOAP_ACTIONINSPRODUK, envelope);
            deleteProduk(id,namaDulu);

            resVal=true;



        }catch (Exception e){
            e.printStackTrace();
        }

        return resVal;
    }

    public boolean deleteProduk(String id, String namaProduk){
        boolean resVal=true;
        SoapObject request = new SoapObject(NAMESPACE,METHOD_NAMEDELPRODUK);
        request.addProperty("sid",id);
        request.addProperty("namaproduk",namaProduk);
        envelope.setOutputSoapObject(request);
        try{
            androidHttpTransport.call(SOAP_ACTIONDELPRODUK,envelope);
            String res = envelope.getResponse().toString();
            String res2 = res.substring(1,res.length()-1);
            String[]arr = res2.split(",");
            String res3 = arr[1].substring(1,arr[1].length()-1);
            System.out.println(res3);
            if(res3.equals("dihapus")){
                resVal=true;
            }
            else{
                resVal=false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return resVal;
    }

    public boolean insertTransaksi(transaksiProduk trans){
        String id = trans.getIdOwner();
        String namaPro = trans.getNamaProduk();
        String hargaJual = trans.getHargaSatuan()+"";
        String kuantitas = trans.getKuantitas()+"";
        String tanggal = trans.getTanggalTransaksi();

        SoapObject request = new SoapObject(NAMESPACE,METHOD_NAMEREGTRANS);
        request.addProperty("sid",id);
        request.addProperty("namaproduk",namaPro);
        request.addProperty("hargajual",hargaJual);
        request.addProperty("qtyjual",kuantitas);
        request.addProperty("tgljual",tanggal);

        envelope.setOutputSoapObject(request);
        try{
            androidHttpTransport.call(SOAP_ACTIONREGTRANS,envelope);
        }catch (Exception e){
            e.printStackTrace();
        }

        return true;
    }

    public ArrayList<transaksiProduk> getTransaksi(String id,String tanggal){
        ArrayList <transaksiProduk> trans = new ArrayList<transaksiProduk>();
        SoapObject request = new SoapObject(NAMESPACE,METHOD_MEGETTRANS);
        request.addProperty("sid",id);
        request.addProperty("tgldari",tanggal);
        envelope.setOutputSoapObject(request);

        try{
            androidHttpTransport.call(SOAP_ACTIONGETTRANS,envelope);
            String res = envelope.getResponse().toString();
            String []arr = res.split(",");

            for (int i = 0;i<arr.length;i++){
                if(arr[i].contains("(")){
                    String temp = arr[i].substring(1,arr[i].length());
                    arr[i] = temp;
                }
                else if(arr[i].contains(")")){
                    String temp = arr[i].substring(0,arr[i].length()-1);
                    arr[i]=temp;
                }
            }
            for (int i = 0;i<arr.length;i++){
                arr[i] = arr[i].substring(1,arr[i].length()-1);
            }
            int j =0;
            int tempNilai = 4;
            for (int i =0;i<arr.length/4;i++){
                transaksiProduk tr = new transaksiProduk();
                int kuantitas=0,harga=0;
                int ct=1;

                while(j<tempNilai){
                    if(ct==1){
                        tr.setNamaProduk(arr[j]);
                    }
                    else if(ct==2){

                        harga=Integer.parseInt(arr[j]);
                        tr.setHargaSatuan(harga);
                    }
                    else if(ct==3){

                        kuantitas=Integer.parseInt(arr[j]);
                        tr.setKuantitas(kuantitas);
                    }
                    else if(ct==4){

                        tr.setTanggalTransaksi(arr[j]);
                    }

                    ct+=1;
                    j+=1;
                }
                tempNilai+=4;
                int temp= kuantitas*harga;
                hargaTotal +=temp;
                tr.setSubtotal(temp);
                tr.setIdOwner(id);
                trans.add(tr);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return trans;
    }

}

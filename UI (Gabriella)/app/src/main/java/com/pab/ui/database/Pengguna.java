package com.pab.ui.database;

import android.widget.DatePicker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Pengguna {

    private String email, nama, alamat, noHP, produkUnggul, password, tglLahir;

    public Pengguna(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public Pengguna(String email, String nama, String alamat, String noHP, String produkUnggul, DatePicker tglLahir) {
        this.email = email;
        this.nama = nama;
        this.alamat = alamat;
        this.noHP = noHP;
        this.produkUnggul = produkUnggul;
        this.tglLahir = convertToString(convertToDate(tglLahir));
        setPassword(this.tglLahir);
    }

    private Date convertToDate(DatePicker dp) {
        int tanggal = dp.getDayOfMonth();
        int bulan = dp.getMonth();
        int tahun = dp.getYear();

        Calendar c = Calendar.getInstance().getInstance();
        c.set(tahun, bulan, tanggal);

        return c.getTime();
    }

    private String convertToString(Date d) {
        DateFormat df = new SimpleDateFormat("YYYYMMDD");
        String str = new String();
        try {
            str = df.format(d);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return str;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getNoHP() {
        return noHP;
    }

    public void setNoHP(String noHP) {
        this.noHP = noHP;
    }

    public String getProdukUnggul() {
        return produkUnggul;
    }

    public void setProdukUnggul(String produkUnggul) {
        this.produkUnggul = produkUnggul;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTglLahir() {
        return tglLahir;
    }

    public void setTglLahir(String tglLahir) {
        this.tglLahir = tglLahir;
    }

}

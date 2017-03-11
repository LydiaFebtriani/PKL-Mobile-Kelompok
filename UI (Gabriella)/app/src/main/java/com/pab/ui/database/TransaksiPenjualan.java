package com.pab.ui.database;

import android.widget.DatePicker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TransaksiPenjualan {

    private String nama, tglTransaksi;
    private int hargaJual, kuantitas, totalHarga;

    public TransaksiPenjualan(String tglTransaksi, String nama, int hargaJual, int kuantitas, int totalHarga) {
        this.tglTransaksi = tglTransaksi;
        this.nama = nama;
        this.hargaJual = hargaJual;
        this.kuantitas = kuantitas;
        this.totalHarga = totalHarga;
    }

    public String getNama() {
        return nama;
    }

    public String getTglTransaksi() {
        return tglTransaksi;
    }

    public int getHargaJual() {
        return hargaJual;
    }

    public int getKuantitas() {
        return kuantitas;
    }

    public int getTotalHarga() {
        return totalHarga;
    }
}

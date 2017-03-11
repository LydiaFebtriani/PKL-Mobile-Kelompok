package com.pab.ui.database;

public class ProdukPKL {

    private String nama, id;
    private int hargaPokok, hargaJual;

    public ProdukPKL(String id, String nama, int hargaPokok, int hargaJual) {
        this.id = id;
        this.nama = nama;
        this.hargaPokok = hargaPokok;
        this.hargaJual = hargaJual;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public int getHargaPokok() {
        return hargaPokok;
    }

    public int getHargaJual() {
        return hargaJual;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

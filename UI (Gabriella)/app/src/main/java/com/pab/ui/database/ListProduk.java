package com.pab.ui.database;

import java.util.ArrayList;
import java.util.List;

public class ListProduk {

    private List<ProdukPKL> produk = new ArrayList<ProdukPKL>();

    public void addProduk(ProdukPKL p) {
        produk.add(p);
    }

    public ProdukPKL getProdukAt(int idx) {
        return produk.get(idx);
    }

    public int getSize() {
        return produk.size();
    }

}

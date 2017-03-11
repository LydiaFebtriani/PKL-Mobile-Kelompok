package com.pab.ui.database;

import java.util.ArrayList;
import java.util.List;

public class ListTransaksi  {

    List<TransaksiPenjualan> transaksi = new ArrayList<TransaksiPenjualan>();

    public void addTransaksi(TransaksiPenjualan t) {
        transaksi.add(t);
    }

    public TransaksiPenjualan getTransaksiAt(int idx) {
        return transaksi.get(idx);
    }

    public int getSize() {
        return transaksi.size();
    }

}

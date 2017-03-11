package com.pab.ui.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import com.pab.ui.Produk;
import com.pab.ui.Transaksi;

import java.util.ArrayList;
import java.util.List;

public class DBManager {

    private static final String DATABASE_NAME = "mydatabase.db";
    private static final int DATABASE_VERSION = 1;

    private static Context context;
    static SQLiteDatabase db;

    static final String TABLE_PENGGUNA = "table_pengguna";
    static final String TABLE_PRODUK = "table_produk";
    static final String TABLE_TRANSAKSI = "table_transaksi";
    static final String VIEW_TRANSAKSI = "view_transaksi";

    private static final String INSERT_PENGGUNA = "INSERT INTO " + TABLE_PENGGUNA
            + " (email, nama_pengguna, alamat, no_hp, tanggal_lahir, produk_unggul)"
            + " VALUES (?, ?, ?, ?, ?, ?);";

    private static final String INSERT_PRODUK = "INSERT INTO " + TABLE_PRODUK
            + " (nama_produk, harga_pokok, harga_jual, pemilik) VALUES (?, ?, ?, ?);";

    private static final String INSERT_TRANSAKSI = "INSERT INTO " + TABLE_TRANSAKSI
            + " (tanggal_transaksi, id_produk, kuantitas, harga_total) VALUES (?, ?, ?, ?);";

    public DBManager(Context context) {
        DBManager.context = context;
        OpenHelper openHelper = new OpenHelper(DBManager.context);
        DBManager.db = openHelper.getWritableDatabase();
    }

    public long insertPengguna(String email, String nama, String alamat, String noHP, String tglLahir, String produkUnggul) {
        SQLiteStatement statement = DBManager.db.compileStatement(INSERT_PENGGUNA);
        statement.bindString(1, email);
        statement.bindString(2, nama);
        statement.bindString(3, alamat);
        statement.bindString(4, noHP);
        statement.bindString(5, tglLahir);
        statement.bindString(6, produkUnggul);
        return statement.executeInsert();
    }

    public long insertProduk(String nama, int hargaPokok, int hargaJual, String pemilik) {
        SQLiteStatement statement = DBManager.db.compileStatement(INSERT_PRODUK);
        statement.bindString(1, nama);
        statement.bindString(2, hargaPokok + "");
        statement.bindString(3, hargaJual + "");
        statement.bindString(4, pemilik);
        return statement.executeInsert();
    }

    public long insertTransaksi(String tanggal, int id, int kuantitas, int total) {
        SQLiteStatement statement = DBManager.db.compileStatement(INSERT_TRANSAKSI);
        statement.bindString(1, tanggal);
        statement.bindString(2, id + "");
        statement.bindString(3, kuantitas + "");
        statement.bindString(4, total + "");
        return statement.executeInsert();
    }

    public void deleteAll() {
        db.delete(TABLE_PENGGUNA, null, null);
        db.delete(TABLE_PRODUK, null, null);
        db.delete(TABLE_TRANSAKSI, null, null);
    }

    public List<String[]> selectAllPengguna() {
        List<String[]> list = new ArrayList<String[]>();
        Cursor cursor = db.query(TABLE_PENGGUNA, new String[] {
                "email", "nama_pengguna", "alamat", "no_hp",
                "tanggal_lahir", "produk_unggul"
        }, null, null, null, null, "name asc");

        int x = 0;
        if (cursor.moveToFirst()) {
            do {
                String[] b1 = new String[] {
                        cursor.getString(0), cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4), cursor.getString(5)
                };

                list.add(b1);

                x = x + 1;
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return list;
    }

    public String getTanggalLahir(String email) {
        String ttl = "";
        Cursor cursor = db.rawQuery("SELECT tanggal_lahir from " + TABLE_PENGGUNA +
            " WHERE email = ?;", new String[]{ email });
        if (cursor.moveToFirst()) {
            ttl = cursor.getString(0);
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return ttl;
    }

    public ListProduk getAllProduk(String email) {
        ListProduk list = new ListProduk();
        String query = "SELECT id, nama_produk, harga_pokok, harga_jual FROM " +
                TABLE_PRODUK + " WHERE pemilik = ?;";
        Cursor cursor = db.rawQuery(query, new String[] { email });

        int x = 0;
        if (cursor.moveToFirst()) {
            do {
                ProdukPKL p = new ProdukPKL(cursor.getString(0), cursor.getString(1),
                        Integer.parseInt(cursor.getString(2)),
                        Integer.parseInt(cursor.getString(3)));

                list.addProduk(p);

                x = x + 1;
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return list;
    }

    public List<String> getIdProduk(String email) {
        List<String> list = new ArrayList<String>();
        String query = "SELECT id FROM " +
                TABLE_PRODUK + " WHERE pemilik = ?;";
        Cursor cursor = db.rawQuery(query, new String[] { email });

        int x = 0;
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(0));
                x = x + 1;
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return list;
    }

    public List<String> getNamaProduk(String email) {
        List<String> list = new ArrayList<String>();
        String query = "SELECT nama_produk FROM " +
                TABLE_PRODUK + " WHERE pemilik = ?;";
        Cursor cursor = db.rawQuery(query, new String[] { email });

        int x = 0;
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(0));
                x = x + 1;
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return list;
    }

    public ListTransaksi getAllTransaksi(String username) {
        ListTransaksi list = new ListTransaksi();
        Cursor cursor = db.rawQuery("SELECT * FROM " + VIEW_TRANSAKSI
                + " WHERE pemilik = ?", new String[] { username });

        int x = 0;
        if (cursor.moveToFirst()) {
            do {
                TransaksiPenjualan t = new TransaksiPenjualan(cursor.getString(0),
                        cursor.getString(2), Integer.parseInt(cursor.getString(3)),
                        Integer.parseInt(cursor.getString(4)), Integer.parseInt(cursor.getString(5)));

                list.addTransaksi(t);

                x = x + 1;
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return list;
    }

    public ListTransaksi getAllTransaksi(String username, String date) {
        ListTransaksi list = new ListTransaksi();
        Cursor cursor = db.rawQuery("SELECT * FROM " + VIEW_TRANSAKSI
                + " WHERE pemilik = ? AND tanggal >= ? ", new String[] { username, date });

        int x = 0;
        if (cursor.moveToFirst()) {
            do {
                TransaksiPenjualan t = new TransaksiPenjualan(cursor.getString(0),
                        cursor.getString(2), Integer.parseInt(cursor.getString(3)),
                        Integer.parseInt(cursor.getString(4)), Integer.parseInt(cursor.getString(5)));

                list.addTransaksi(t);

                x = x + 1;
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return list;
    }

    public int countTotalTransaksi(String username) {
        if (getCountTransaksi(username) > 0) {
            String command = "SELECT SUM(harga_total) FROM " + VIEW_TRANSAKSI +
                    "WHERE pemilik = ?;";
            Cursor cursor = db.rawQuery(command, new String[] { username });
            if (cursor.moveToFirst()) {
                int total = Integer.parseInt(cursor.getString(0));
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
                return total;
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            return 0;
        }
        return 0;
    }

    public int countTotalTransaksi (String username, String tanggal) {
        if (getCountTransaksi (username, tanggal) > 0) {
            String command = "SELECT SUM(harga_total) FROM " + VIEW_TRANSAKSI +
                    " WHERE pemilik = ? AND tanggal >= ?";
            Cursor cursor = db.rawQuery(command, new String[] { username, tanggal });
            if (cursor.moveToFirst()) {
                int total = Integer.parseInt(cursor.getString(0));
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
                return total;
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            return 0;
        }
        return 0;
    }

    public int getCountTransaksi(String username) {
        String command = "SELECT COUNT(harga_total) FROM " + VIEW_TRANSAKSI
                + " WHERE pemilik = ?;";

        Cursor cursor = db.rawQuery(command, new String[] { username });
        if (cursor.moveToFirst()) {
            int count = Integer.parseInt(cursor.getString(0));
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            return count;
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return 0;
    }

    public int getCountTransaksi (String username, String tanggal) {
        String command = "SELECT COUNT(harga_total) FROM " + VIEW_TRANSAKSI
                + " WHERE pemilik = ? AND tanggal >= ?;";

        Cursor cursor = db.rawQuery(command, new String[] { username, tanggal });
        if (cursor.moveToFirst()) {
            int count = Integer.parseInt(cursor.getString(0));
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            return count;
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return 0;
    }

    public ProdukPKL showDetailProduk(String id) {
        ProdukPKL p = null;
        Cursor cursor = db.rawQuery("SELECT nama_produk, harga_pokok, harga_jual FROM "
        + TABLE_PRODUK + " WHERE id = ?;" , new String[] { id });
        if (cursor.moveToFirst()) {
            p = new ProdukPKL(id, cursor.getString(0), Integer.parseInt(cursor.getString(1)),
                Integer.parseInt(cursor.getString(2)));
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return p;
    }

    public ProdukPKL showDetailProdukByName(String nama) {
        ProdukPKL p = null;
        Cursor cursor = db.rawQuery("SELECT id, nama_produk, harga_pokok, harga_jual FROM "
                + TABLE_PRODUK + " WHERE nama = ?;" , new String[] { nama });
        if (cursor.moveToFirst()) {
            p = new ProdukPKL(cursor.getString(0), cursor.getString(1),
                    Integer.parseInt(cursor.getString(2)),
                    Integer.parseInt(cursor.getString(3)));
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return p;
    }

    public int updateProduk(String id, String nama, String hargaPokok, String hargaJual) {
        String update = ("UPDATE " + TABLE_PRODUK + " SET nama_produk = ?, harga_pokok = ?, harga_jual = ? WHERE id = ?;");
        SQLiteStatement statement = DBManager.db.compileStatement(update);
        statement.bindString(1, nama);
        statement.bindString(2, hargaPokok + "");
        statement.bindString(3, hargaJual + "");
        statement.bindString(4, id);
        return statement.executeUpdateDelete();
    }

    public boolean isEmailExist(String email) {
        Cursor cursor = db.rawQuery("SELECT email FROM " + TABLE_PENGGUNA +
                " WHERE email = ?;", new String[] { email });
        if (cursor.moveToFirst()) {
            return true;
        } else {
            return false;
        }
    }

    public int deleteProduk(String id) {
        String delete = ("DELETE FROM " + TABLE_PRODUK + " WHERE id = ?;");
        SQLiteStatement statement = DBManager.db.compileStatement(delete);
        statement.bindString(1, id);
        return statement.executeUpdateDelete();
    }

    private static class OpenHelper extends SQLiteOpenHelper {

        OpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_PENGGUNA
            + " (email TEXT(128) PRIMARY KEY, nama_pengguna TEXT(50), "
            + "alamat TEXT(255), no_hp TEXT(15), tanggal_lahir TEXT(8), "
            + "produk_unggul TEXT(50));");

            db.execSQL("CREATE TABLE " + TABLE_PRODUK +
            " (id INTEGER PRIMARY KEY AUTOINCREMENT, nama_produk TEXT(50),"
            + " harga_pokok INTEGER, harga_jual INTEGER, pemilik TEXT(128), "
            + "FOREIGN KEY(pemilik) REFERENCES " + TABLE_PENGGUNA + "(email));");

            db.execSQL("CREATE TABLE " + TABLE_TRANSAKSI +
            " (id INTEGER PRIMARY KEY AUTOINCREMENT, tanggal_transaksi TEXT(8), "
            + "id_produk INTEGER, kuantitas INTEGER, harga_total INTEGER,"
            + " FOREIGN KEY(id_produk) REFERENCES " + TABLE_PRODUK + "(id));");

            db.execSQL("CREATE VIEW " + VIEW_TRANSAKSI + " AS SELECT "
            + TABLE_TRANSAKSI + ".tanggal_transaksi AS tanggal, "
            + TABLE_PRODUK + ".pemilik AS pemilik, "
            + TABLE_PRODUK + ".nama_produk, " + TABLE_PRODUK + ".harga_jual, "
            + TABLE_TRANSAKSI + ".kuantitas, " + TABLE_TRANSAKSI + ".harga_total AS harga_total FROM "
            + TABLE_TRANSAKSI + " INNER JOIN " + TABLE_PRODUK + " ON ("
            + TABLE_TRANSAKSI + ".id_produk = " + TABLE_PRODUK + ".id);");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXIST " + TABLE_PENGGUNA);
            db.execSQL("DROP TABLE IF EXIST " + TABLE_PRODUK);
            db.execSQL("DROP TABLE IF EXIST " + TABLE_TRANSAKSI);
            onCreate(db);
        }

    }

}

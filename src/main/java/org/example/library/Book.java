package org.example.library;

public class Book {
    private int id;
    private String judul;
    private String penulis;
    private String tahun;
    private String kategori;
    private int stok;
    private String foto;

    // Overloaded constructors
    public Book(int id, String judul) {
        this.id = id;
        this.judul = judul;
    }

    public Book(int id, String judul, String penulis, String tahun, String kategori, int stok, String foto) {
        this.id = id;
        this.judul = judul;
        this.penulis = penulis;
        this.tahun = tahun;
        this.kategori = kategori;
        this.stok = stok;
        this.foto = foto;
    }

    // Overloaded methods
    public void setInfo(int id, String judul) {
        this.id = id;
        this.judul = judul;
    }

    public void setInfo(int id, String judul, String penulis) {
        this.id = id;
        this.judul = judul;
        this.penulis = penulis;
    }

    // Getter and setter methods for each field
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getPenulis() {
        return penulis;
    }

    public void setPenulis(String penulis) {
        this.penulis = penulis;
    }

    public String getTahun() {
        return tahun;
    }

    public void setTahun(String tahun) {
        this.tahun = tahun;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public int getStok() {
        return stok;
    }

    public void setStok(int stok) {
        this.stok = stok;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}

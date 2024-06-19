package org.example.library.models;

public class User {
    private int id;
    private String username;
    private String password;
    private String nim;
    private String role;
    private String nama;
    private String fakultas;
    private String jurusan;

    public User(int id, String username, String password, String nim, String role, String nama, String fakultas, String jurusan) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.nim = nim;
        this.role = role;
        this.nama = nama;
        this.fakultas = fakultas;
        this.jurusan = jurusan;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNim() { return nim; }
    public void setNim(String nim) { this.nim = nim; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public String getFakultas() { return fakultas; }
    public void setFakultas(String fakultas) { this.fakultas = fakultas; }

    public String getJurusan() { return jurusan; }
    public void setJurusan(String jurusan) { this.jurusan = jurusan; }
}

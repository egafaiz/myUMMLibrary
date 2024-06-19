package org.example.library.services;

import java.sql.*;

public class DatabaseService {
    private static final String URL = "jdbc:mysql://localhost:3306/library";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void initialize() {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(50), " +
                    "password VARCHAR(50), " +
                    "nim VARCHAR(15), " +
                    "role VARCHAR(20), " +
                    "nama VARCHAR(100), " +
                    "fakultas VARCHAR(100), " +
                    "jurusan VARCHAR(100)" +
                    ")";
            stmt.execute(sql);

            sql = "CREATE TABLE IF NOT EXISTS books (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "judul VARCHAR(100), " +
                    "penulis VARCHAR(100), " +
                    "kategori VARCHAR(50), " +
                    "stok INT, " +
                    "tahun_terbit INT" +
                    ")";
            stmt.execute(sql);

            sql = "CREATE TABLE IF NOT EXISTS transactions (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "user_id INT, " +
                    "book_id INT, " +
                    "borrow_date DATE, " +
                    "return_date DATE, " +
                    "status VARCHAR(20), " +
                    "fine DECIMAL(10, 2), " +
                    "FOREIGN KEY (user_id) REFERENCES users(id), " +
                    "FOREIGN KEY (book_id) REFERENCES books(id)" +
                    ")";
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

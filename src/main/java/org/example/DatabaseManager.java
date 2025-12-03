package org.example;

import java.sql.*;

public class DatabaseManager {
    private Connection connection;

    public DatabaseManager() {
        try {
            // 1. QUAN TRỌNG: Nạp Driver thủ công để tránh lỗi "No suitable driver"
            Class.forName("org.sqlite.JDBC");

            // 2. Đường dẫn file database
            String url = "jdbc:sqlite:database.db";
            connection = DriverManager.getConnection(url);
            System.out.println("Kết nối Database thành công!");

            createTable();
        } catch (ClassNotFoundException e) {
            System.err.println("Lỗi: Không tìm thấy Driver SQLite. Kiểm tra pom.xml!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Lỗi kết nối Database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ... (Giữ nguyên các hàm createTable, registerUser, loginUser bên dưới)
    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT NOT NULL UNIQUE," +
                "password TEXT NOT NULL)";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Lỗi tạo bảng: " + e.getMessage());
        }
    }

    public boolean registerUser(String username, String password) {
        String sql = "INSERT INTO users(username, password) VALUES(?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Lỗi đăng ký (Có thể trùng tên): " + e.getMessage());
            return false;
        }
    }

    public boolean loginUser(String username, String password) {
        String sql = "SELECT id FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
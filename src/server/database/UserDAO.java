package server.database;

import common.models.User;
import java.sql.*;
import java.security.MessageDigest;

public class UserDAO {

    public static boolean register(String login, String password) {
        String hashedPassword = hashMD5(password);
        String sql = "INSERT INTO users (login, password_hash) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, login);
            ps.setString(2, hashedPassword);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Ошибка регистрации: " + e.getMessage());
            return false;
        }
    }

    public static User login(String login, String password) {
        String hashedPassword = hashMD5(password);
        String sql = "SELECT id, login FROM users WHERE login = ? AND password_hash = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, login);
            ps.setString(2, hashedPassword);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getLong("id"));
                    user.setLogin(rs.getString("login"));
                    return user;
                }
            }
        } catch (SQLException e) {
            System.out.println("Ошибка авторизации: " + e.getMessage());
        }
        return null;
    }

    private static String hashMD5(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(password.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            System.out.println("Ошибка хэширования: " + e.getMessage());
            return password; 
        }
    }

    
    public static Long getUserIdByLogin(String login) {
        if (login == null) return null;

        String sql = "SELECT id FROM users WHERE login = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, login);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id");
                }
            }
        } catch (SQLException e) {
            System.out.println("Ошибка получения user id: " + e.getMessage());
        }
        return null;
    }
}
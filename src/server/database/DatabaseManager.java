package server.database;

import java.sql.*;
import java.util.Properties;

public class DatabaseManager {


    private static final String HOST     = "localhost";
    private static final String DATABASE = "laba7_db";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "56793";


    private static final String URL = "jdbc:postgresql://" + HOST + "/" + DATABASE;


    public static Connection getConnection() throws SQLException {
        Properties props = new Properties();
        props.setProperty("user", USERNAME);
        props.setProperty("password", PASSWORD);
        props.setProperty("ssl", "false");

        Connection conn = DriverManager.getConnection(URL, props);
        conn.setAutoCommit(true);
        return conn;
    }


    public static void connect() {
        try {

            try (Connection conn = getConnection()) {
                System.out.println(" Успешное подключение к PostgreSQL");
                System.out.println("   Хост: " + HOST);
                System.out.println("   База: " + DATABASE);
                System.out.println("   Пользователь: " + USERNAME);
            }
        } catch (SQLException e) {
            System.out.println(" Не удалось подключиться к " + URL);
            System.out.println("   Ошибка: " + e.getMessage());
        }
    }

    public static boolean isConnected() {
        return true;
    }
}
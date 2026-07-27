package lms.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection — Singleton Pattern for PostgreSQL
 * Keywords: Singleton, JDBC, PostgreSQL Driver
 *
 * CHANGES FROM MYSQL VERSION:
 * 1. JDBC URL: jdbc:mysql → jdbc:postgresql
 * 2. Driver class: com.mysql.cj.jdbc.Driver → org.postgresql.Driver
 * 3. Default port: 3306 → 5432
 */
public class DBConnection {

    // ── CHANGE THESE TO MATCH YOUR POSTGRESQL SETUP ──
    private static final String URL      = "jdbc:postgresql://localhost:3000/lms_db";
    private static final String USER     = "postgres";           // default PostgreSQL user
    private static final String PASSWORD = "1234"; // ← CHANGE THIS

    private static Connection connection = null;

    private DBConnection() {}

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("[DB] Connected to PostgreSQL successfully.");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("[DB ERROR] PostgreSQL JDBC Driver not found: " + e.getMessage());
            System.err.println("           → Make sure postgresql-xx.jar is in lib/ folder!");
        } catch (SQLException e) {
            System.err.println("[DB ERROR] Connection failed: " + e.getMessage());
            System.err.println("           → Check: password correct? PostgreSQL running? lms_db created?");
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] PostgreSQL connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("[DB ERROR] Error closing connection: " + e.getMessage());
        }
    }
}

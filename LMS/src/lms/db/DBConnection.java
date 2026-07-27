package lms.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection.java
 * ------------------
 * Provides a single shared Connection to the MySQL database.
 * Uses the Singleton pattern so only ONE connection is created.
 *
 * HOW TO USE:
 *   Connection conn = DBConnection.getConnection();
 */
public class DBConnection {

    // ── Change these values to match your MySQL setup ──
    private static final String URL      = "jdbc:mysql://localhost:3306/lms_db?useSSL=false&serverTimezone=UTC";
    private static final String USER     = "root";
    private static final String PASSWORD = "your_password_here";   // ← Replace with your MySQL password

    // The single shared connection instance
    private static Connection connection = null;

    /**
     * Private constructor prevents creating objects of this class.
     * This enforces the Singleton pattern.
     */
    private DBConnection() {}

    /**
     * Returns the shared Connection.
     * Creates it only once; returns the same object on subsequent calls.
     *
     * @return Connection object to lms_db
     */
    public static Connection getConnection() {
        try {
            // If no connection exists, or it got closed, create a new one
            if (connection == null || connection.isClosed()) {
                // Load the MySQL JDBC driver (required for older versions)
                Class.forName("com.mysql.cj.jdbc.Driver");

                // Establish the connection
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("[DB] Connection established successfully.");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("[DB ERROR] MySQL JDBC Driver not found. Add mysql-connector-java.jar to classpath.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("[DB ERROR] Cannot connect to database. Check URL, user, password.");
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Closes the connection. Call this when the application exits.
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

import java.sql.*;

/**
 * Database Connection Handler for LMS Backend
 * Manages connections to PostgreSQL database
 */
public class DatabaseConnection {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/lms_db";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "123";
    
    /**
     * Get a connection to the database
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC Driver not found!");
            e.printStackTrace();
        }
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
    
    /**
     * Close database connection
     */
    public static void closeConnection(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

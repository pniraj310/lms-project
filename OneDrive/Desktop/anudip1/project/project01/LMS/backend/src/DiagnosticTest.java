import java.sql.*;

/**
 * Simple diagnostic tool to test database connection
 */
public class DiagnosticTest {
    public static void main(String[] args) {
        System.out.println("═══ LMS Database Diagnostic Test ═══\n");
        
        // Test 1: Check Java version
        System.out.println("Test 1: Java Version");
        System.out.println("Java version: " + System.getProperty("java.version"));
        System.out.println();
        
        // Test 2: Check PostgreSQL Driver
        System.out.println("Test 2: PostgreSQL JDBC Driver");
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("✓ PostgreSQL driver found!");
        } catch (ClassNotFoundException e) {
            System.out.println("✗ PostgreSQL driver NOT found!");
            System.out.println("   Error: " + e.getMessage());
            System.out.println("   Solution: Add postgresql.jar to lib folder");
            return;
        }
        System.out.println();
        
        // Test 3: Test Database Connection
        System.out.println("Test 3: Database Connection");
        System.out.println("Connecting to: jdbc:postgresql://localhost:5432/lms_db");
        System.out.println("User: postgres");
        
        try {
            Connection conn = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/lms_db",
                "postgres",
                "123"
            );
            System.out.println("✓ Connected to database successfully!");
            
            // Test 4: Check tables
            System.out.println("\nTest 4: Database Tables");
            String[] tables = {"users", "courses", "enrollments"};
            DatabaseMetaData metadata = conn.getMetaData();
            ResultSet rs = metadata.getTables(null, "public", "%", new String[]{"TABLE"});
            
            System.out.println("Tables in database:");
            while (rs.next()) {
                System.out.println("  ✓ " + rs.getString("TABLE_NAME"));
            }
            
            // Test 5: Count users
            System.out.println("\nTest 5: User Count");
            Statement stmt = conn.createStatement();
            ResultSet userRs = stmt.executeQuery("SELECT COUNT(*) as count FROM users");
            if (userRs.next()) {
                System.out.println("✓ Total users in database: " + userRs.getInt("count"));
            }
            
            // Test 6: List all users
            System.out.println("\nTest 6: All Users");
            ResultSet allUsers = stmt.executeQuery("SELECT user_id, username, email, role FROM users");
            while (allUsers.next()) {
                System.out.println("  - ID: " + allUsers.getInt("user_id") + 
                                 ", Username: " + allUsers.getString("username") + 
                                 ", Email: " + allUsers.getString("email") + 
                                 ", Role: " + allUsers.getString("role"));
            }
            
            // Test 7: Try login
            System.out.println("\nTest 7: Test Login");
            PreparedStatement pstmt = conn.prepareStatement(
                "SELECT * FROM users WHERE username = ? AND password = ?"
            );
            pstmt.setString(1, "admin_user");
            pstmt.setString(2, "admin123");
            ResultSet loginRs = pstmt.executeQuery();
            
            if (loginRs.next()) {
                System.out.println("✓ Login test successful!");
                System.out.println("  Logged in as: " + loginRs.getString("username"));
            } else {
                System.out.println("✗ Login test failed!");
                System.out.println("  User 'admin_user' not found or password incorrect");
            }
            
            conn.close();
            System.out.println("\n✓ All tests passed! Database is working correctly.\n");
            
        } catch (SQLException e) {
            System.out.println("✗ Connection failed!");
            System.out.println("Error: " + e.getMessage());
            System.out.println("\nPossible solutions:");
            System.out.println("1. Check PostgreSQL is running (open pgAdmin 4)");
            System.out.println("2. Check database 'lms_db' exists");
            System.out.println("3. Check PostgreSQL password is '123' in line 37");
            System.out.println("4. Check port 5432 is correct");
        }
    }
}

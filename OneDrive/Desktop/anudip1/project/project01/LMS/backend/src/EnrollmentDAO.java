import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Enrollment management with Progress Tracking
 * Handles all database operations for enrollments (CRUD operations)
 */
public class EnrollmentDAO {
    
    /**
     * Add a new enrollment to database
     */
    public static boolean addEnrollment(Enrollment enrollment) {
        String sql = "INSERT INTO enrollments (user_id, course_id, enrollment_date, progress, status) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, enrollment.getUserId());
            pstmt.setInt(2, enrollment.getCourseId());
            pstmt.setDate(3, java.sql.Date.valueOf(enrollment.getEnrollmentDate()));
            pstmt.setFloat(4, enrollment.getProgress());
            pstmt.setString(5, enrollment.getStatus());
            
            int result = pstmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("Error adding enrollment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get enrollment by ID
     */
    public static Enrollment getEnrollmentById(int enrollmentId) {
        String sql = "SELECT * FROM enrollments WHERE enrollment_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, enrollmentId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Enrollment enrollment = new Enrollment(
                    rs.getInt("enrollment_id"),
                    rs.getInt("user_id"),
                    rs.getInt("course_id"),
                    rs.getDate("enrollment_date").toLocalDate(),
                    rs.getFloat("progress"),
                    rs.getString("status")
                );
                return enrollment;
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting enrollment: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Get all enrollments
     */
    public static List<Enrollment> getAllEnrollments() {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT * FROM enrollments";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Enrollment enrollment = new Enrollment(
                    rs.getInt("enrollment_id"),
                    rs.getInt("user_id"),
                    rs.getInt("course_id"),
                    rs.getDate("enrollment_date").toLocalDate(),
                    rs.getFloat("progress"),
                    rs.getString("status")
                );
                enrollments.add(enrollment);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting all enrollments: " + e.getMessage());
            e.printStackTrace();
        }
        return enrollments;
    }
    
    /**
     * Update enrollment (status + progress)
     */
    public static boolean updateEnrollment(Enrollment enrollment) {
        String sql = "UPDATE enrollments SET status = ?, progress = ? WHERE enrollment_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, enrollment.getStatus());
            pstmt.setFloat(2, enrollment.getProgress());
            pstmt.setInt(3, enrollment.getEnrollmentId());
            
            int result = pstmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating enrollment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update progress for a user in a course
     */
    public static boolean updateProgress(int userId, int courseId, float progressIncrement) {
        String sql = "UPDATE enrollments SET progress = LEAST(100, progress + ?) WHERE user_id = ? AND course_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setFloat(1, progressIncrement);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, courseId);
            
            int result = pstmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating progress: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Set progress directly for a user in a course
     */
    public static boolean setProgress(int userId, int courseId, float progress) {
        String sql = "UPDATE enrollments SET progress = ? WHERE user_id = ? AND course_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setFloat(1, Math.min(100, progress));
            pstmt.setInt(2, userId);
            pstmt.setInt(3, courseId);
            
            int result = pstmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("Error setting progress: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get progress for a user in a course
     */
    public static float getProgress(int userId, int courseId) {
        String sql = "SELECT progress FROM enrollments WHERE user_id = ? AND course_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, courseId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getFloat("progress");
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting progress: " + e.getMessage());
            e.printStackTrace();
        }
        return 0.0f;
    }
    
    /**
     * Delete enrollment by ID
     */
    public static boolean deleteEnrollment(int enrollmentId) {
        String sql = "DELETE FROM enrollments WHERE enrollment_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, enrollmentId);
            int result = pstmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting enrollment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get enrollments by user ID
     */
    public static List<Enrollment> getEnrollmentsByUser(int userId) {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT * FROM enrollments WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Enrollment enrollment = new Enrollment(
                    rs.getInt("enrollment_id"),
                    rs.getInt("user_id"),
                    rs.getInt("course_id"),
                    rs.getDate("enrollment_date").toLocalDate(),
                    rs.getFloat("progress"),
                    rs.getString("status")
                );
                enrollments.add(enrollment);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting enrollments by user: " + e.getMessage());
            e.printStackTrace();
        }
        return enrollments;
    }
    
    /**
     * Get enrollments by course ID
     */
    public static List<Enrollment> getEnrollmentsByCourse(int courseId) {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT * FROM enrollments WHERE course_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Enrollment enrollment = new Enrollment(
                    rs.getInt("enrollment_id"),
                    rs.getInt("user_id"),
                    rs.getInt("course_id"),
                    rs.getDate("enrollment_date").toLocalDate(),
                    rs.getFloat("progress"),
                    rs.getString("status")
                );
                enrollments.add(enrollment);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting enrollments by course: " + e.getMessage());
            e.printStackTrace();
        }
        return enrollments;
    }
}

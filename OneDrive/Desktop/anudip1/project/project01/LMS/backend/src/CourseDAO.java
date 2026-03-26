import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Course management
 * Handles all database operations for courses (CRUD operations)
 */
public class CourseDAO {
    
    /**
     * Add a new course to database
     */
    public static boolean addCourse(Course course) {
        String sql = "INSERT INTO courses (course_name, description, instructor_id, credits) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, course.getCourseName());
            pstmt.setString(2, course.getDescription());
            pstmt.setInt(3, course.getInstructorId());
            pstmt.setInt(4, course.getCredits());
            
            int result = pstmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("Error adding course: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get course by ID
     */
    public static Course getCourseById(int courseId) {
        String sql = "SELECT * FROM courses WHERE course_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Course course = new Course(
                    rs.getInt("course_id"),
                    rs.getString("course_name"),
                    rs.getString("description"),
                    rs.getInt("instructor_id"),
                    rs.getInt("credits")
                );
                return course;
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting course: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Get all courses
     */
    public static List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Course course = new Course(
                    rs.getInt("course_id"),
                    rs.getString("course_name"),
                    rs.getString("description"),
                    rs.getInt("instructor_id"),
                    rs.getInt("credits")
                );
                courses.add(course);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting all courses: " + e.getMessage());
            e.printStackTrace();
        }
        return courses;
    }
    
    /**
     * Update course information
     */
    public static boolean updateCourse(Course course) {
        String sql = "UPDATE courses SET course_name = ?, description = ?, instructor_id = ?, credits = ? WHERE course_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, course.getCourseName());
            pstmt.setString(2, course.getDescription());
            pstmt.setInt(3, course.getInstructorId());
            pstmt.setInt(4, course.getCredits());
            pstmt.setInt(5, course.getCourseId());
            
            int result = pstmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating course: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Delete course by ID
     */
    public static boolean deleteCourse(int courseId) {
        String sql = "DELETE FROM courses WHERE course_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, courseId);
            int result = pstmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting course: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get courses by instructor ID
     */
    public static List<Course> getCoursesByInstructor(int instructorId) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses WHERE instructor_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, instructorId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Course course = new Course(
                    rs.getInt("course_id"),
                    rs.getString("course_name"),
                    rs.getString("description"),
                    rs.getInt("instructor_id"),
                    rs.getInt("credits")
                );
                courses.add(course);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting courses by instructor: " + e.getMessage());
            e.printStackTrace();
        }
        return courses;
    }
}

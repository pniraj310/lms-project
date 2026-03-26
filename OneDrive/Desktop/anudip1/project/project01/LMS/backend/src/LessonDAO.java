import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Lesson management
 * Handles all database operations for lessons
 */
public class LessonDAO {
    
    /**
     * Add a new lesson to a course
     */
    public static boolean addLesson(Lesson lesson) {
        String sql = "INSERT INTO lessons (course_id, lesson_title, lesson_content, lesson_order) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, lesson.getCourseId());
            pstmt.setString(2, lesson.getLessonTitle());
            pstmt.setString(3, lesson.getLessonContent());
            pstmt.setInt(4, lesson.getLessonOrder());
            
            int result = pstmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("Error adding lesson: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get lesson by ID
     */
    public static Lesson getLessonById(int lessonId) {
        String sql = "SELECT * FROM lessons WHERE lesson_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, lessonId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Lesson(
                    rs.getInt("lesson_id"),
                    rs.getInt("course_id"),
                    rs.getString("lesson_title"),
                    rs.getString("lesson_content"),
                    rs.getInt("lesson_order")
                );
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting lesson: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Get all lessons for a course
     */
    public static List<Lesson> getLessonsByCourse(int courseId) {
        List<Lesson> lessons = new ArrayList<>();
        String sql = "SELECT * FROM lessons WHERE course_id = ? ORDER BY lesson_order";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                lessons.add(new Lesson(
                    rs.getInt("lesson_id"),
                    rs.getInt("course_id"),
                    rs.getString("lesson_title"),
                    rs.getString("lesson_content"),
                    rs.getInt("lesson_order")
                ));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting lessons: " + e.getMessage());
            e.printStackTrace();
        }
        return lessons;
    }
    
    /**
     * Get total lessons in a course
     */
    public static int getTotalLessonsInCourse(int courseId) {
        String sql = "SELECT COUNT(*) as total FROM lessons WHERE course_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total");
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting total lessons: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Update lesson
     */
    public static boolean updateLesson(Lesson lesson) {
        String sql = "UPDATE lessons SET lesson_title = ?, lesson_content = ? WHERE lesson_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, lesson.getLessonTitle());
            pstmt.setString(2, lesson.getLessonContent());
            pstmt.setInt(3, lesson.getLessonId());
            
            int result = pstmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating lesson: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Delete lesson
     */
    public static boolean deleteLesson(int lessonId) {
        String sql = "DELETE FROM lessons WHERE lesson_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, lessonId);
            int result = pstmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting lesson: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}

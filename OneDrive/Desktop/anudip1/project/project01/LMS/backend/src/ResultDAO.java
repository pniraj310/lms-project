import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Quiz Results management
 * Handles all database operations for quiz results
 */
public class ResultDAO {
    
    /**
     * Add a new quiz result
     */
    public static boolean addResult(QuizResult result) {
        String sql = "INSERT INTO results (user_id, course_id, score, total_questions, percentage) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, result.getUserId());
            pstmt.setInt(2, result.getCourseId());
            pstmt.setInt(3, result.getScore());
            pstmt.setInt(4, result.getTotalQuestions());
            pstmt.setFloat(5, result.getPercentage());
            
            int resultVal = pstmt.executeUpdate();
            return resultVal > 0;
            
        } catch (SQLException e) {
            System.err.println("Error adding result: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get quiz result by ID
     */
    public static QuizResult getResultById(int resultId) {
        String sql = "SELECT * FROM results WHERE result_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, resultId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new QuizResult(
                    rs.getInt("result_id"),
                    rs.getInt("user_id"),
                    rs.getInt("course_id"),
                    rs.getInt("score"),
                    rs.getInt("total_questions"),
                    rs.getFloat("percentage"),
                    rs.getTimestamp("attempt_date")
                );
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting result: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Get all quiz results for a user in a course
     */
    public static List<QuizResult> getResultsByUserAndCourse(int userId, int courseId) {
        List<QuizResult> results = new ArrayList<>();
        String sql = "SELECT * FROM results WHERE user_id = ? AND course_id = ? ORDER BY attempt_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, courseId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                results.add(new QuizResult(
                    rs.getInt("result_id"),
                    rs.getInt("user_id"),
                    rs.getInt("course_id"),
                    rs.getInt("score"),
                    rs.getInt("total_questions"),
                    rs.getFloat("percentage"),
                    rs.getTimestamp("attempt_date")
                ));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting results: " + e.getMessage());
            e.printStackTrace();
        }
        return results;
    }
    
    /**
     * Get latest quiz result for a user in a course
     */
    public static QuizResult getLatestResult(int userId, int courseId) {
        String sql = "SELECT * FROM results WHERE user_id = ? AND course_id = ? ORDER BY attempt_date DESC LIMIT 1";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, courseId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new QuizResult(
                    rs.getInt("result_id"),
                    rs.getInt("user_id"),
                    rs.getInt("course_id"),
                    rs.getInt("score"),
                    rs.getInt("total_questions"),
                    rs.getFloat("percentage"),
                    rs.getTimestamp("attempt_date")
                );
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting latest result: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Get average score for a user across all courses
     */
    public static float getAverageScore(int userId) {
        String sql = "SELECT AVG(percentage) as avg_score FROM results WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                float avg = rs.getFloat("avg_score");
                return avg > 0 ? avg : 0.0f;
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting average score: " + e.getMessage());
            e.printStackTrace();
        }
        return 0.0f;
    }
}

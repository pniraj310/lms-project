import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Question management
 * Handles all database operations for quiz questions
 */
public class QuestionDAO {
    
    /**
     * Add a new question to a course
     */
    public static boolean addQuestion(Question question) {
        String sql = "INSERT INTO questions (course_id, question_text, option_a, option_b, option_c, option_d, correct_option) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, question.getCourseId());
            pstmt.setString(2, question.getQuestionText());
            pstmt.setString(3, question.getOptionA());
            pstmt.setString(4, question.getOptionB());
            pstmt.setString(5, question.getOptionC());
            pstmt.setString(6, question.getOptionD());
            pstmt.setString(7, question.getCorrectOption());
            
            int result = pstmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("Error adding question: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get question by ID
     */
    public static Question getQuestionById(int questionId) {
        String sql = "SELECT * FROM questions WHERE question_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, questionId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Question(
                    rs.getInt("question_id"),
                    rs.getInt("course_id"),
                    rs.getString("question_text"),
                    rs.getString("option_a"),
                    rs.getString("option_b"),
                    rs.getString("option_c"),
                    rs.getString("option_d"),
                    rs.getString("correct_option")
                );
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting question: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Get all questions for a course
     */
    public static List<Question> getQuestionsByCourse(int courseId) {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM questions WHERE course_id = ? ORDER BY question_id";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                questions.add(new Question(
                    rs.getInt("question_id"),
                    rs.getInt("course_id"),
                    rs.getString("question_text"),
                    rs.getString("option_a"),
                    rs.getString("option_b"),
                    rs.getString("option_c"),
                    rs.getString("option_d"),
                    rs.getString("correct_option")
                ));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting questions: " + e.getMessage());
            e.printStackTrace();
        }
        return questions;
    }
    
    /**
     * Get total questions in a course
     */
    public static int getTotalQuestionsByCourse(int courseId) {
        String sql = "SELECT COUNT(*) as total FROM questions WHERE course_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total");
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting total questions: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Update question
     */
    public static boolean updateQuestion(Question question) {
        String sql = "UPDATE questions SET question_text = ?, option_a = ?, option_b = ?, option_c = ?, option_d = ?, correct_option = ? WHERE question_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, question.getQuestionText());
            pstmt.setString(2, question.getOptionA());
            pstmt.setString(3, question.getOptionB());
            pstmt.setString(4, question.getOptionC());
            pstmt.setString(5, question.getOptionD());
            pstmt.setString(6, question.getCorrectOption());
            pstmt.setInt(7, question.getQuestionId());
            
            int result = pstmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating question: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Delete question
     */
    public static boolean deleteQuestion(int questionId) {
        String sql = "DELETE FROM questions WHERE question_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, questionId);
            int result = pstmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting question: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Chatbot Q&A management
 * Handles all database operations for chatbot questions and answers
 */
public class ChatbotDAO {
    
    /**
     * Add a new chatbot Q&A
     */
    public static boolean addChatbotQA(ChatbotQA qa) {
        String sql = "INSERT INTO chatbot_qa (course_id, question, answer, keywords) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, qa.getCourseId());
            pstmt.setString(2, qa.getQuestion());
            pstmt.setString(3, qa.getAnswer());
            pstmt.setString(4, qa.getKeywords());
            
            int result = pstmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("Error adding chatbot Q&A: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get chatbot Q&A by ID
     */
    public static ChatbotQA getQAById(int qaId) {
        String sql = "SELECT * FROM chatbot_qa WHERE qa_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, qaId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new ChatbotQA(
                    rs.getInt("qa_id"),
                    rs.getInt("course_id"),
                    rs.getString("question"),
                    rs.getString("answer"),
                    rs.getString("keywords")
                );
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting Q&A: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Get all chatbot Q&A for a course
     */
    public static List<ChatbotQA> getQAByCourse(int courseId) {
        List<ChatbotQA> qaList = new ArrayList<>();
        String sql = "SELECT * FROM chatbot_qa WHERE course_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                qaList.add(new ChatbotQA(
                    rs.getInt("qa_id"),
                    rs.getInt("course_id"),
                    rs.getString("question"),
                    rs.getString("answer"),
                    rs.getString("keywords")
                ));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting Q&A by course: " + e.getMessage());
            e.printStackTrace();
        }
        return qaList;
    }
    
    /**
     * Get all chatbot Q&A (general knowledge base)
     */
    public static List<ChatbotQA> getAllQA() {
        List<ChatbotQA> qaList = new ArrayList<>();
        String sql = "SELECT * FROM chatbot_qa";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                qaList.add(new ChatbotQA(
                    rs.getInt("qa_id"),
                    rs.getInt("course_id"),
                    rs.getString("question"),
                    rs.getString("answer"),
                    rs.getString("keywords")
                ));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting all Q&A: " + e.getMessage());
            e.printStackTrace();
        }
        return qaList;
    }
    
    /**
     * Search chatbot Q&A by keyword
     * This is the core chatbot logic
     */
    public static ChatbotQA searchByKeyword(String userInput) {
        List<ChatbotQA> allQA = getAllQA();
        String input = userInput.toLowerCase().trim();
        
        // Exact match first
        for (ChatbotQA qa : allQA) {
            if (qa.getQuestion().toLowerCase().contains(input)) {
                return qa;
            }
        }
        
        // Keyword match
        for (ChatbotQA qa : allQA) {
            String keywords = qa.getKeywords().toLowerCase();
            String[] keywordArray = keywords.split(" ");
            for (String keyword : keywordArray) {
                if (input.contains(keyword.trim())) {
                    return qa;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Update chatbot Q&A
     */
    public static boolean updateChatbotQA(ChatbotQA qa) {
        String sql = "UPDATE chatbot_qa SET question = ?, answer = ?, keywords = ? WHERE qa_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, qa.getQuestion());
            pstmt.setString(2, qa.getAnswer());
            pstmt.setString(3, qa.getKeywords());
            pstmt.setInt(4, qa.getQaId());
            
            int result = pstmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating Q&A: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Delete chatbot Q&A
     */
    public static boolean deleteQA(int qaId) {
        String sql = "DELETE FROM chatbot_qa WHERE qa_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, qaId);
            int result = pstmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting Q&A: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}

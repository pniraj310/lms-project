package lms.dao;

import lms.db.DBConnection;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * ChatbotDAO — Rule-based keyword matching chatbot
 * Keywords: HashMap, Keyword Matching, Rule-based AI
 */
public class ChatbotDAO {

    // In-memory keyword → answer store
    private Map<String, String> qaMap = new HashMap<>();

    /** Loads all keyword-answer pairs from DB into HashMap */
    public void loadQA() {
        qaMap.clear();
        String sql = "SELECT keyword, answer FROM chatbot_qa";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                qaMap.put(rs.getString("keyword").toLowerCase(), rs.getString("answer"));
            }
        } catch (SQLException e) {
            System.err.println("[ChatbotDAO] loadQA error: " + e.getMessage());
        }
    }

    /**
     * Returns chatbot response for a user message.
     * Checks if message CONTAINS any keyword (case-insensitive).
     */
    public String getResponse(String message) {
        if (message == null || message.trim().isEmpty()) {
            return "Please type a message.";
        }
        String lowerMsg = message.toLowerCase().trim();
        for (Map.Entry<String, String> entry : qaMap.entrySet()) {
            if (lowerMsg.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return "Sorry, I don't understand that. Try asking about courses, quizzes, results, or progress!";
    }

    /** Adds a new keyword-answer pair to DB */
    public boolean addQA(String keyword, String answer) {
        String sql = "INSERT INTO chatbot_qa (keyword, answer) VALUES (?, ?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, keyword.toLowerCase());
            ps.setString(2, answer);
            boolean result = ps.executeUpdate() > 0;
            if (result) loadQA(); // Reload map
            return result;
        } catch (SQLException e) {
            System.err.println("[ChatbotDAO] addQA error: " + e.getMessage());
        }
        return false;
    }

    /** Returns all QA pairs as a formatted string for admin display */
    public Map<String, String> getAllQA() {
        loadQA();
        return new HashMap<>(qaMap);
    }
}

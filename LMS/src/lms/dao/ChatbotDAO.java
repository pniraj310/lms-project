package lms.dao;

import lms.db.DBConnection;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * ChatbotDAO.java
 * ----------------
 * Provides chatbot Q&A responses from the database.
 * Falls back to a hard-coded default if no match is found.
 *
 * How it works:
 *  1. Load all keyword→answer mappings from chatbot_qa table at startup.
 *  2. When user sends a message, scan for any matching keyword.
 *  3. Return matched answer, or a default "I don't know" message.
 */
public class ChatbotDAO {

    private Connection conn;
    // In-memory cache: keyword (lowercase) → answer
    private Map<String, String> qaCache = new HashMap<>();

    public ChatbotDAO() {
        this.conn = DBConnection.getConnection();
        loadQACache();    // Load all Q&A from DB once at startup
    }

    // ─────────────────────────────────────────────────────────
    // LOAD all Q&A pairs from database into memory
    // ─────────────────────────────────────────────────────────
    private void loadQACache() {
        String sql = "SELECT keyword, answer FROM chatbot_qa";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                // Store keyword in lowercase for case-insensitive matching
                qaCache.put(rs.getString("keyword").toLowerCase(), rs.getString("answer"));
            }
            System.out.println("[ChatbotDAO] Loaded " + qaCache.size() + " Q&A pairs.");
        } catch (SQLException e) {
            System.err.println("[ChatbotDAO] loadQACache error: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────
    // GET RESPONSE for a user message
    // ─────────────────────────────────────────────────────────
    /**
     * Returns a chatbot response for the given user input.
     * Checks if any known keyword appears in the message.
     *
     * @param userMessage raw text typed by the user
     * @return chatbot response string
     */
    public String getResponse(String userMessage) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return "Please type a message!";
        }

        String lower = userMessage.toLowerCase().trim();

        // Check each keyword: does the user message CONTAIN this keyword?
        for (Map.Entry<String, String> entry : qaCache.entrySet()) {
            if (lower.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        // No match found → return default response
        return "I'm not sure about that. You can ask me about: enrollment, quizzes, results, progress, or courses. Type 'help' for more options!";
    }

    // ─────────────────────────────────────────────────────────
    // ADD a new Q&A pair (Admin feature)
    // ─────────────────────────────────────────────────────────
    public boolean addQA(String keyword, String question, String answer) {
        String sql = "INSERT INTO chatbot_qa (keyword, question, answer) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, keyword.toLowerCase());
            ps.setString(2, question);
            ps.setString(3, answer);
            boolean ok = ps.executeUpdate() > 0;
            // Refresh cache
            if (ok) qaCache.put(keyword.toLowerCase(), answer);
            return ok;
        } catch (SQLException e) {
            System.err.println("[ChatbotDAO] addQA error: " + e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────
    // GET cache size (for admin display)
    // ─────────────────────────────────────────────────────────
    public int getCacheSize() { return qaCache.size(); }

    // ─────────────────────────────────────────────────────────
    // RELOAD cache from DB (call after adding new Q&A)
    // ─────────────────────────────────────────────────────────
    public void reloadCache() {
        qaCache.clear();
        loadQACache();
    }
}

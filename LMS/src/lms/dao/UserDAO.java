package lms.dao;

import lms.db.DBConnection;
import lms.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * UserDAO.java
 * -------------
 * Data Access Object for the 'users' table.
 * All DB operations for users go here.
 * Uses PreparedStatement to prevent SQL injection.
 */
public class UserDAO {

    private Connection conn;

    public UserDAO() {
        this.conn = DBConnection.getConnection();
    }

    // ─────────────────────────────────────────────────────────
    // LOGIN: find user by username and password
    // ─────────────────────────────────────────────────────────
    /**
     * Authenticates a user by username and password.
     * @return User object if found, null otherwise
     */
    public User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);    // user found → return object
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Login error: " + e.getMessage());
        }
        return null;   // user not found
    }

    // ─────────────────────────────────────────────────────────
    // REGISTER: insert a new user
    // ─────────────────────────────────────────────────────────
    /**
     * Registers a new user. Returns true if successful.
     */
    public boolean register(User user) {
        // Check if username or email already exists
        if (existsByUsername(user.getUsername())) {
            System.out.println("[UserDAO] Username already taken: " + user.getUsername());
            return false;
        }
        if (existsByEmail(user.getEmail())) {
            System.out.println("[UserDAO] Email already registered: " + user.getEmail());
            return false;
        }

        String sql = "INSERT INTO users (username, password, full_name, email, role) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getRole());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] Register error: " + e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────
    // GET ALL USERS (for Admin)
    // ─────────────────────────────────────────────────────────
    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY role, full_name";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[UserDAO] getAllUsers error: " + e.getMessage());
        }
        return list;
    }

    // ─────────────────────────────────────────────────────────
    // GET USERS BY ROLE
    // ─────────────────────────────────────────────────────────
    public List<User> getUsersByRole(String role) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = ? ORDER BY full_name";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[UserDAO] getUsersByRole error: " + e.getMessage());
        }
        return list;
    }

    // ─────────────────────────────────────────────────────────
    // DELETE USER (Admin only)
    // ─────────────────────────────────────────────────────────
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] deleteUser error: " + e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────
    // HELPER: check existence
    // ─────────────────────────────────────────────────────────
    private boolean existsByUsername(String username) {
        String sql = "SELECT id FROM users WHERE username = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            return ps.executeQuery().next();
        } catch (SQLException e) { return false; }
    }

    private boolean existsByEmail(String email) {
        String sql = "SELECT id FROM users WHERE email = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            return ps.executeQuery().next();
        } catch (SQLException e) { return false; }
    }

    // ─────────────────────────────────────────────────────────
    // HELPER: map a ResultSet row to a User object
    // ─────────────────────────────────────────────────────────
    private User mapRow(ResultSet rs) throws SQLException {
        return new User(
            rs.getInt("id"),
            rs.getString("username"),
            rs.getString("password"),
            rs.getString("full_name"),
            rs.getString("email"),
            rs.getString("role")
        );
    }
}

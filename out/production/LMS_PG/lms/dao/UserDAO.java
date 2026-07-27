package lms.dao;

import lms.db.DBConnection;
import lms.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * UserDAO — Data Access Object for User operations
 * Keywords: DAO Pattern, PreparedStatement, SQL Injection Prevention
 */
public class UserDAO {

    /**
     * Authenticates user by username and password.
     * Uses PreparedStatement to prevent SQL injection.
     */
    public User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Login error: " + e.getMessage());
        }
        return null; // null = login failed
    }

    /**
     * Registers a new user.
     * Checks for duplicate username/email before inserting.
     */
    public boolean register(User user) {
        // Check if username or email already exists
        String checkSql = "SELECT id FROM users WHERE username = ? OR email = ?";
        try (PreparedStatement checkPs = DBConnection.getConnection().prepareStatement(checkSql)) {
            checkPs.setString(1, user.getUsername());
            checkPs.setString(2, user.getEmail());
            ResultSet rs = checkPs.executeQuery();
            if (rs.next()) return false; // Duplicate found
        } catch (SQLException e) {
            System.err.println("[UserDAO] Check duplicate error: " + e.getMessage());
            return false;
        }

        // Insert new user
        String sql = "INSERT INTO users (name, username, email, password, role) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPassword());
            ps.setString(5, user.getRole());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] Register error: " + e.getMessage());
        }
        return false;
    }

    /** Returns all users — used by Admin dashboard */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY role, name";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) users.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[UserDAO] getAllUsers error: " + e.getMessage());
        }
        return users;
    }

    /** Deletes a user by ID */
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] deleteUser error: " + e.getMessage());
        }
        return false;
    }

    /** Maps a ResultSet row to a User object */
    private User mapRow(ResultSet rs) throws SQLException {
        return new User(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("username"),
            rs.getString("email"),
            rs.getString("password"),
            rs.getString("role")
        );
    }
}

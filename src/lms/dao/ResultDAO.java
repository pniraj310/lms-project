package lms.dao;

import lms.db.DBConnection;
import lms.model.Progress;
import lms.model.Result;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ResultDAO — PostgreSQL Version
 * KEY CHANGE:
 * MySQL:      INSERT ... ON DUPLICATE KEY UPDATE percentage = VALUES(percentage)
 * PostgreSQL: INSERT ... ON CONFLICT (student_id, course_id) DO UPDATE SET percentage = EXCLUDED.percentage
 */
public class ResultDAO {

    public boolean saveResult(int studentId, int quizId, int score, int total) {
        String sql = "INSERT INTO results (student_id, quiz_id, score, total) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, quizId);
            ps.setInt(3, score);
            ps.setInt(4, total);
            boolean saved = ps.executeUpdate() > 0;
            if (saved) updateProgress(studentId, quizId);
            return saved;
        } catch (SQLException e) {
            System.err.println("[ResultDAO] saveResult error: " + e.getMessage());
        }
        return false;
    }

    /**
     * PostgreSQL UPSERT syntax:
     * ON CONFLICT (column) DO UPDATE SET col = EXCLUDED.col
     *
     * MySQL equivalent:
     * ON DUPLICATE KEY UPDATE percentage = VALUES(percentage)
     */
    private void updateProgress(int studentId, int quizId) {
        // Step 1: Get course_id for this quiz
        int courseId = -1;
        String courseIdSql = "SELECT course_id FROM quizzes WHERE id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(courseIdSql)) {
            ps.setInt(1, quizId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) courseId = rs.getInt("course_id");
        } catch (SQLException e) {
            System.err.println("[ResultDAO] getCourseId error: " + e.getMessage());
            return;
        }

        // Step 2: Count total quizzes in course
        int total = 0;
        String totalSql = "SELECT COUNT(*) FROM quizzes WHERE course_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(totalSql)) {
            ps.setInt(1, courseId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) total = rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[ResultDAO] totalQuizzes error: " + e.getMessage());
            return;
        }

        // Step 3: Count distinct quizzes attempted
        int attempted = 0;
        String attemptedSql = "SELECT COUNT(DISTINCT r.quiz_id) FROM results r " +
                              "JOIN quizzes q ON r.quiz_id = q.id " +
                              "WHERE r.student_id = ? AND q.course_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(attemptedSql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) attempted = rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[ResultDAO] attemptedQuizzes error: " + e.getMessage());
            return;
        }

        double percentage = total == 0 ? 0 : ((double) attempted / total) * 100;

        // Step 4: PostgreSQL UPSERT
        // ON CONFLICT (student_id, course_id) DO UPDATE SET percentage = EXCLUDED.percentage
        String upsertSql = "INSERT INTO progress (student_id, course_id, percentage) VALUES (?, ?, ?) " +
                           "ON CONFLICT (student_id, course_id) DO UPDATE SET percentage = EXCLUDED.percentage";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(upsertSql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            ps.setDouble(3, percentage);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[ResultDAO] updateProgress UPSERT error: " + e.getMessage());
        }
    }

    public List<Result> getResultsByStudent(int studentId) {
        List<Result> list = new ArrayList<>();
        String sql = "SELECT r.*, q.title AS quiz_title, c.title AS course_title " +
                     "FROM results r JOIN quizzes q ON r.quiz_id = q.id " +
                     "JOIN courses c ON q.course_id = c.id " +
                     "WHERE r.student_id = ? ORDER BY r.attempted_at DESC";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Result(rs.getInt("id"), rs.getInt("student_id"),
                        rs.getInt("quiz_id"), rs.getString("quiz_title"),
                        rs.getString("course_title"), rs.getInt("score"), rs.getInt("total")));
            }
        } catch (SQLException e) {
            System.err.println("[ResultDAO] getResultsByStudent error: " + e.getMessage());
        }
        return list;
    }

    public List<Progress> getProgressByStudent(int studentId) {
        List<Progress> list = new ArrayList<>();
        String sql = "SELECT p.*, c.title AS course_title FROM progress p " +
                     "JOIN courses c ON p.course_id = c.id WHERE p.student_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Progress(rs.getInt("student_id"), rs.getInt("course_id"),
                        rs.getString("course_title"), rs.getDouble("percentage")));
            }
        } catch (SQLException e) {
            System.err.println("[ResultDAO] getProgressByStudent error: " + e.getMessage());
        }
        return list;
    }

    public List<Result> getResultsByTeacher(int teacherId) {
        List<Result> list = new ArrayList<>();
        String sql = "SELECT r.*, q.title AS quiz_title, c.title AS course_title " +
                     "FROM results r JOIN quizzes q ON r.quiz_id = q.id " +
                     "JOIN courses c ON q.course_id = c.id " +
                     "WHERE c.teacher_id = ? ORDER BY r.attempted_at DESC";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Result(rs.getInt("id"), rs.getInt("student_id"),
                        rs.getInt("quiz_id"), rs.getString("quiz_title"),
                        rs.getString("course_title"), rs.getInt("score"), rs.getInt("total")));
            }
        } catch (SQLException e) {
            System.err.println("[ResultDAO] getResultsByTeacher error: " + e.getMessage());
        }
        return list;
    }
}

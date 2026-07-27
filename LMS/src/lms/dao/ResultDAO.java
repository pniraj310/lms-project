package lms.dao;

import lms.db.DBConnection;
import lms.model.Progress;
import lms.model.Result;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ResultDAO.java
 * Handles quiz result storage, retrieval, and progress tracking updates.
 */
public class ResultDAO {

    private Connection conn;

    public ResultDAO() {
        this.conn = DBConnection.getConnection();
    }

    // Save quiz result and trigger progress update
    public boolean saveResult(int studentId, int quizId, int score, int total) {
        String sql = "INSERT INTO results (student_id, quiz_id, score, total) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, quizId);
            ps.setInt(3, score);
            ps.setInt(4, total);
            boolean saved = ps.executeUpdate() > 0;
            if (saved) updateProgress(studentId, quizId);
            return saved;
        } catch (SQLException e) {
            System.err.println("[ResultDAO] saveResult error: " + e.getMessage());
            return false;
        }
    }

    // Get all results for a student
    public List<Result> getResultsByStudent(int studentId) {
        List<Result> list = new ArrayList<>();
        String sql = "SELECT r.*, q.title AS quiz_title "
                   + "FROM results r JOIN quizzes q ON r.quiz_id = q.id "
                   + "WHERE r.student_id = ? ORDER BY r.attempted_at DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapResultRow(rs));
        } catch (SQLException e) {
            System.err.println("[ResultDAO] getResultsByStudent error: " + e.getMessage());
        }
        return list;
    }

    // Get all results for quizzes belonging to a teacher's courses
    public List<Result> getResultsForTeacher(int teacherId) {
        List<Result> list = new ArrayList<>();
        String sql = "SELECT r.*, q.title AS quiz_title "
                   + "FROM results r "
                   + "JOIN quizzes q ON r.quiz_id = q.id "
                   + "JOIN courses c ON q.course_id = c.id "
                   + "WHERE c.teacher_id = ? ORDER BY r.attempted_at DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapResultRow(rs));
        } catch (SQLException e) {
            System.err.println("[ResultDAO] getResultsForTeacher error: " + e.getMessage());
        }
        return list;
    }

    // Check if student already attempted a quiz
    public boolean hasAttempted(int studentId, int quizId) {
        String sql = "SELECT id FROM results WHERE student_id = ? AND quiz_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, quizId);
            return ps.executeQuery().next();
        } catch (SQLException e) { return false; }
    }

    // Update progress: (attempted quizzes / total quizzes in course) * 100
    private void updateProgress(int studentId, int quizId) {
        String getCourse = "SELECT course_id FROM quizzes WHERE id = ?";
        try (PreparedStatement ps1 = conn.prepareStatement(getCourse)) {
            ps1.setInt(1, quizId);
            ResultSet rs1 = ps1.executeQuery();
            if (!rs1.next()) return;
            int courseId = rs1.getInt("course_id");

            String countTotal = "SELECT COUNT(*) FROM quizzes WHERE course_id = ?";
            PreparedStatement ps2 = conn.prepareStatement(countTotal);
            ps2.setInt(1, courseId);
            ResultSet rs2 = ps2.executeQuery();
            int totalQuizzes = rs2.next() ? rs2.getInt(1) : 1;

            String countDone = "SELECT COUNT(DISTINCT r.quiz_id) FROM results r "
                             + "JOIN quizzes q ON r.quiz_id = q.id "
                             + "WHERE r.student_id = ? AND q.course_id = ?";
            PreparedStatement ps3 = conn.prepareStatement(countDone);
            ps3.setInt(1, studentId);
            ps3.setInt(2, courseId);
            ResultSet rs3 = ps3.executeQuery();
            int doneQuizzes = rs3.next() ? rs3.getInt(1) : 0;

            double percent = (totalQuizzes == 0) ? 0 : (doneQuizzes * 100.0 / totalQuizzes);

            String upsert = "INSERT INTO progress (student_id, course_id, completion_percent) "
                          + "VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE completion_percent = ?";
            PreparedStatement ps4 = conn.prepareStatement(upsert);
            ps4.setInt(1, studentId);
            ps4.setInt(2, courseId);
            ps4.setDouble(3, percent);
            ps4.setDouble(4, percent);
            ps4.executeUpdate();

        } catch (SQLException e) {
            System.err.println("[ResultDAO] updateProgress error: " + e.getMessage());
        }
    }

    // Get progress for all enrolled courses of a student
    public List<Progress> getProgressByStudent(int studentId) {
        List<Progress> list = new ArrayList<>();
        String sql = "SELECT p.*, c.title AS course_title "
                   + "FROM progress p JOIN courses c ON p.course_id = c.id "
                   + "WHERE p.student_id = ? ORDER BY c.title";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Progress(
                    rs.getInt("id"), rs.getInt("student_id"), rs.getInt("course_id"),
                    rs.getDouble("completion_percent"), rs.getString("course_title"),
                    rs.getString("last_updated")
                ));
            }
        } catch (SQLException e) {
            System.err.println("[ResultDAO] getProgressByStudent error: " + e.getMessage());
        }
        return list;
    }

    private Result mapResultRow(ResultSet rs) throws SQLException {
        return new Result(
            rs.getInt("id"), rs.getInt("student_id"), rs.getInt("quiz_id"),
            rs.getInt("score"), rs.getInt("total"),
            rs.getString("quiz_title"), rs.getString("attempted_at")
        );
    }
}

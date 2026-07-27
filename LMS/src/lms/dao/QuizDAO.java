package lms.dao;

import lms.db.DBConnection;
import lms.model.Question;
import lms.model.Quiz;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * QuizDAO.java
 * -------------
 * Manages all database operations for quizzes and questions.
 */
public class QuizDAO {

    private Connection conn;

    public QuizDAO() {
        this.conn = DBConnection.getConnection();
    }

    // ─────────────────────────────────────────────────────────
    // ADD QUIZ
    // ─────────────────────────────────────────────────────────
    public boolean addQuiz(Quiz quiz) {
        String sql = "INSERT INTO quizzes (course_id, title) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quiz.getCourseId());
            ps.setString(2, quiz.getTitle());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[QuizDAO] addQuiz error: " + e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────
    // DELETE QUIZ
    // ─────────────────────────────────────────────────────────
    public boolean deleteQuiz(int quizId) {
        String sql = "DELETE FROM quizzes WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quizId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[QuizDAO] deleteQuiz error: " + e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────
    // GET QUIZZES BY COURSE
    // ─────────────────────────────────────────────────────────
    public List<Quiz> getQuizzesByCourse(int courseId) {
        List<Quiz> list = new ArrayList<>();
        String sql = "SELECT q.*, c.title AS course_title "
                   + "FROM quizzes q JOIN courses c ON q.course_id = c.id "
                   + "WHERE q.course_id = ? ORDER BY q.title";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapQuizRow(rs));
        } catch (SQLException e) {
            System.err.println("[QuizDAO] getQuizzesByCourse error: " + e.getMessage());
        }
        return list;
    }

    // ─────────────────────────────────────────────────────────
    // GET ALL QUIZZES for enrolled courses of a student
    // ─────────────────────────────────────────────────────────
    public List<Quiz> getQuizzesForStudent(int studentId) {
        List<Quiz> list = new ArrayList<>();
        String sql = "SELECT q.*, c.title AS course_title "
                   + "FROM quizzes q "
                   + "JOIN courses c ON q.course_id = c.id "
                   + "JOIN enrollments e ON e.course_id = c.id "
                   + "WHERE e.student_id = ? ORDER BY c.title, q.title";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapQuizRow(rs));
        } catch (SQLException e) {
            System.err.println("[QuizDAO] getQuizzesForStudent error: " + e.getMessage());
        }
        return list;
    }

    // ─────────────────────────────────────────────────────────
    // GET QUIZZES BY TEACHER (for teacher's courses)
    // ─────────────────────────────────────────────────────────
    public List<Quiz> getQuizzesByTeacher(int teacherId) {
        List<Quiz> list = new ArrayList<>();
        String sql = "SELECT q.*, c.title AS course_title "
                   + "FROM quizzes q JOIN courses c ON q.course_id = c.id "
                   + "WHERE c.teacher_id = ? ORDER BY c.title, q.title";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapQuizRow(rs));
        } catch (SQLException e) {
            System.err.println("[QuizDAO] getQuizzesByTeacher error: " + e.getMessage());
        }
        return list;
    }

    // ─────────────────────────────────────────────────────────
    // ADD QUESTION
    // ─────────────────────────────────────────────────────────
    public boolean addQuestion(Question q) {
        String sql = "INSERT INTO questions (quiz_id, question_text, option_a, option_b, option_c, option_d, correct_option) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, q.getQuizId());
            ps.setString(2, q.getQuestionText());
            ps.setString(3, q.getOptionA());
            ps.setString(4, q.getOptionB());
            ps.setString(5, q.getOptionC());
            ps.setString(6, q.getOptionD());
            ps.setString(7, String.valueOf(q.getCorrectOption()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[QuizDAO] addQuestion error: " + e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────
    // GET QUESTIONS FOR A QUIZ
    // ─────────────────────────────────────────────────────────
    public List<Question> getQuestionsByQuiz(int quizId) {
        List<Question> list = new ArrayList<>();
        String sql = "SELECT * FROM questions WHERE quiz_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quizId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapQuestionRow(rs));
        } catch (SQLException e) {
            System.err.println("[QuizDAO] getQuestionsByQuiz error: " + e.getMessage());
        }
        return list;
    }

    // ─────────────────────────────────────────────────────────
    // COUNT QUESTIONS IN A QUIZ
    // ─────────────────────────────────────────────────────────
    public int countQuestions(int quizId) {
        String sql = "SELECT COUNT(*) FROM questions WHERE quiz_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quizId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[QuizDAO] countQuestions error: " + e.getMessage());
        }
        return 0;
    }

    // ─────────────────────────────────────────────────────────
    // COUNT TOTAL QUIZZES IN A COURSE
    // ─────────────────────────────────────────────────────────
    public int countQuizzesInCourse(int courseId) {
        String sql = "SELECT COUNT(*) FROM quizzes WHERE course_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[QuizDAO] countQuizzesInCourse error: " + e.getMessage());
        }
        return 0;
    }

    // ─────────────────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────────────────
    private Quiz mapQuizRow(ResultSet rs) throws SQLException {
        return new Quiz(
            rs.getInt("id"),
            rs.getInt("course_id"),
            rs.getString("title"),
            rs.getString("course_title")
        );
    }

    private Question mapQuestionRow(ResultSet rs) throws SQLException {
        return new Question(
            rs.getInt("id"),
            rs.getInt("quiz_id"),
            rs.getString("question_text"),
            rs.getString("option_a"),
            rs.getString("option_b"),
            rs.getString("option_c"),
            rs.getString("option_d"),
            rs.getString("correct_option").charAt(0)
        );
    }
}

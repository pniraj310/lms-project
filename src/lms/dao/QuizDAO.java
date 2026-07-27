package lms.dao;

import lms.db.DBConnection;
import lms.model.Question;
import lms.model.Quiz;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * QuizDAO — Handles quiz and question operations
 * Keywords: DAO Pattern, PreparedStatement, JOIN
 */
public class QuizDAO {

    /** Returns all quizzes for a course */
    public List<Quiz> getQuizzesByCourse(int courseId) {
        List<Quiz> list = new ArrayList<>();
        String sql = "SELECT q.*, c.title AS course_title FROM quizzes q " +
                     "JOIN courses c ON q.course_id = c.id WHERE q.course_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapQuizRow(rs));
        } catch (SQLException e) {
            System.err.println("[QuizDAO] getQuizzesByCourse error: " + e.getMessage());
        }
        return list;
    }

    /** Returns all quizzes for enrolled courses of a student */
    public List<Quiz> getQuizzesForStudent(int studentId) {
        List<Quiz> list = new ArrayList<>();
        String sql = "SELECT q.*, c.title AS course_title FROM quizzes q " +
                     "JOIN courses c ON q.course_id = c.id " +
                     "JOIN enrollments e ON c.id = e.course_id " +
                     "WHERE e.student_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapQuizRow(rs));
        } catch (SQLException e) {
            System.err.println("[QuizDAO] getQuizzesForStudent error: " + e.getMessage());
        }
        return list;
    }

    /** Returns all quizzes by teacher */
    public List<Quiz> getQuizzesByTeacher(int teacherId) {
        List<Quiz> list = new ArrayList<>();
        String sql = "SELECT q.*, c.title AS course_title FROM quizzes q " +
                     "JOIN courses c ON q.course_id = c.id WHERE c.teacher_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapQuizRow(rs));
        } catch (SQLException e) {
            System.err.println("[QuizDAO] getQuizzesByTeacher error: " + e.getMessage());
        }
        return list;
    }

    /** Adds a quiz */
    public boolean addQuiz(Quiz quiz) {
        String sql = "INSERT INTO quizzes (title, course_id) VALUES (?, ?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, quiz.getTitle());
            ps.setInt(2, quiz.getCourseId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[QuizDAO] addQuiz error: " + e.getMessage());
        }
        return false;
    }

    /** Returns all questions for a quiz */
    public List<Question> getQuestionsByQuiz(int quizId) {
        List<Question> list = new ArrayList<>();
        String sql = "SELECT * FROM questions WHERE quiz_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, quizId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapQuestionRow(rs));
        } catch (SQLException e) {
            System.err.println("[QuizDAO] getQuestionsByQuiz error: " + e.getMessage());
        }
        return list;
    }

    /** Adds a question to a quiz */
    public boolean addQuestion(Question q) {
        String sql = "INSERT INTO questions (quiz_id, question_text, option_a, option_b, option_c, option_d, correct_option) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
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
        }
        return false;
    }

    /** Returns all quizzes for a specific course (for count purposes) */
    public int getTotalQuizzesInCourse(int courseId) {
        String sql = "SELECT COUNT(*) FROM quizzes WHERE course_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[QuizDAO] getTotalQuizzesInCourse error: " + e.getMessage());
        }
        return 0;
    }

    /** Gets course_id for a quiz */
    public int getCourseIdByQuiz(int quizId) {
        String sql = "SELECT course_id FROM quizzes WHERE id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, quizId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("course_id");
        } catch (SQLException e) {
            System.err.println("[QuizDAO] getCourseIdByQuiz error: " + e.getMessage());
        }
        return -1;
    }

    private Quiz mapQuizRow(ResultSet rs) throws SQLException {
        return new Quiz(rs.getInt("id"), rs.getString("title"),
                        rs.getInt("course_id"), rs.getString("course_title"));
    }

    private Question mapQuestionRow(ResultSet rs) throws SQLException {
        return new Question(
            rs.getInt("id"), rs.getInt("quiz_id"),
            rs.getString("question_text"),
            rs.getString("option_a"), rs.getString("option_b"),
            rs.getString("option_c"), rs.getString("option_d"),
            rs.getString("correct_option").charAt(0)
        );
    }
}

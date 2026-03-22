package lms.dao;

import lms.db.DBConnection;
import lms.model.Course;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CourseDAO.java
 * ---------------
 * Handles all database operations for courses and enrollments.
 */
public class CourseDAO {

    private Connection conn;

    public CourseDAO() {
        this.conn = DBConnection.getConnection();
    }

    // ─────────────────────────────────────────────────────────
    // ADD COURSE (Teacher)
    // ─────────────────────────────────────────────────────────
    public boolean addCourse(Course course) {
        String sql = "INSERT INTO courses (title, description, teacher_id) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, course.getTitle());
            ps.setString(2, course.getDescription());
            ps.setInt(3, course.getTeacherId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CourseDAO] addCourse error: " + e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────
    // DELETE COURSE (Teacher/Admin)
    // ─────────────────────────────────────────────────────────
    public boolean deleteCourse(int courseId) {
        String sql = "DELETE FROM courses WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CourseDAO] deleteCourse error: " + e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────
    // GET ALL COURSES (Student view - all available)
    // ─────────────────────────────────────────────────────────
    public List<Course> getAllCourses() {
        List<Course> list = new ArrayList<>();
        // JOIN to get teacher name
        String sql = "SELECT c.*, u.full_name AS teacher_name "
                   + "FROM courses c JOIN users u ON c.teacher_id = u.id "
                   + "ORDER BY c.title";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[CourseDAO] getAllCourses error: " + e.getMessage());
        }
        return list;
    }

    // ─────────────────────────────────────────────────────────
    // GET COURSES BY TEACHER
    // ─────────────────────────────────────────────────────────
    public List<Course> getCoursesByTeacher(int teacherId) {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT c.*, u.full_name AS teacher_name "
                   + "FROM courses c JOIN users u ON c.teacher_id = u.id "
                   + "WHERE c.teacher_id = ? ORDER BY c.title";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[CourseDAO] getCoursesByTeacher error: " + e.getMessage());
        }
        return list;
    }

    // ─────────────────────────────────────────────────────────
    // GET ENROLLED COURSES for a student
    // ─────────────────────────────────────────────────────────
    public List<Course> getEnrolledCourses(int studentId) {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT c.*, u.full_name AS teacher_name "
                   + "FROM courses c "
                   + "JOIN enrollments e ON c.id = e.course_id "
                   + "JOIN users u ON c.teacher_id = u.id "
                   + "WHERE e.student_id = ? ORDER BY c.title";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[CourseDAO] getEnrolledCourses error: " + e.getMessage());
        }
        return list;
    }

    // ─────────────────────────────────────────────────────────
    // ENROLL STUDENT in a course
    // ─────────────────────────────────────────────────────────
    public boolean enrollStudent(int studentId, int courseId) {
        // Prevent duplicate enrollment
        if (isEnrolled(studentId, courseId)) {
            System.out.println("[CourseDAO] Student already enrolled.");
            return false;
        }
        // Enroll
        String enroll = "INSERT INTO enrollments (student_id, course_id) VALUES (?, ?)";
        // Also create a progress entry at 0%
        String progress = "INSERT INTO progress (student_id, course_id, completion_percent) VALUES (?, ?, 0.00)";
        try (PreparedStatement ps1 = conn.prepareStatement(enroll);
             PreparedStatement ps2 = conn.prepareStatement(progress)) {

            ps1.setInt(1, studentId);
            ps1.setInt(2, courseId);
            ps1.executeUpdate();

            ps2.setInt(1, studentId);
            ps2.setInt(2, courseId);
            ps2.executeUpdate();

            return true;
        } catch (SQLException e) {
            System.err.println("[CourseDAO] enrollStudent error: " + e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────
    // CHECK ENROLLMENT
    // ─────────────────────────────────────────────────────────
    public boolean isEnrolled(int studentId, int courseId) {
        String sql = "SELECT id FROM enrollments WHERE student_id = ? AND course_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            return ps.executeQuery().next();
        } catch (SQLException e) { return false; }
    }

    // ─────────────────────────────────────────────────────────
    // GET TOTAL ENROLLED STUDENTS for a course
    // ─────────────────────────────────────────────────────────
    public int getEnrolledCount(int courseId) {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE course_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[CourseDAO] getEnrolledCount error: " + e.getMessage());
        }
        return 0;
    }

    // ─────────────────────────────────────────────────────────
    // HELPER: map ResultSet row → Course
    // ─────────────────────────────────────────────────────────
    private Course mapRow(ResultSet rs) throws SQLException {
        return new Course(
            rs.getInt("id"),
            rs.getString("title"),
            rs.getString("description"),
            rs.getInt("teacher_id"),
            rs.getString("teacher_name")
        );
    }
}

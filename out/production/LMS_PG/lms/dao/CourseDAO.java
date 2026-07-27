package lms.dao;

import lms.db.DBConnection;
import lms.model.Course;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CourseDAO — PostgreSQL Version
 * KEY CHANGE: INSERT IGNORE → INSERT ... ON CONFLICT DO NOTHING
 */
public class CourseDAO {

    public List<Course> getAllCourses() {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT c.*, u.name AS teacher_name FROM courses c " +
                     "JOIN users u ON c.teacher_id = u.id ORDER BY c.title";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[CourseDAO] getAllCourses error: " + e.getMessage());
        }
        return list;
    }

    public List<Course> getCoursesByTeacher(int teacherId) {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT c.*, u.name AS teacher_name FROM courses c " +
                     "JOIN users u ON c.teacher_id = u.id WHERE c.teacher_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[CourseDAO] getCoursesByTeacher error: " + e.getMessage());
        }
        return list;
    }

    public List<Course> getEnrolledCourses(int studentId) {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT c.*, u.name AS teacher_name FROM courses c " +
                     "JOIN users u ON c.teacher_id = u.id " +
                     "JOIN enrollments e ON c.id = e.course_id " +
                     "WHERE e.student_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[CourseDAO] getEnrolledCourses error: " + e.getMessage());
        }
        return list;
    }

    public boolean addCourse(Course course) {
        String sql = "INSERT INTO courses (title, description, teacher_id) VALUES (?, ?, ?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, course.getTitle());
            ps.setString(2, course.getDescription());
            ps.setInt(3, course.getTeacherId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CourseDAO] addCourse error: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteCourse(int courseId) {
        String sql = "DELETE FROM courses WHERE id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, courseId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CourseDAO] deleteCourse error: " + e.getMessage());
        }
        return false;
    }

    /**
     * PostgreSQL version:
     * MySQL:      INSERT IGNORE INTO enrollments ...
     * PostgreSQL: INSERT INTO enrollments ... ON CONFLICT DO NOTHING
     */
    public boolean enrollStudent(int studentId, int courseId) {
        String sql = "INSERT INTO enrollments (student_id, course_id) VALUES (?, ?) " +
                     "ON CONFLICT DO NOTHING";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            ps.executeUpdate();

            // Create progress entry
            String progressSql = "INSERT INTO progress (student_id, course_id, percentage) " +
                                 "VALUES (?, ?, 0) ON CONFLICT DO NOTHING";
            try (PreparedStatement pp = DBConnection.getConnection().prepareStatement(progressSql)) {
                pp.setInt(1, studentId);
                pp.setInt(2, courseId);
                pp.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            System.err.println("[CourseDAO] enrollStudent error: " + e.getMessage());
        }
        return false;
    }

    public boolean isEnrolled(int studentId, int courseId) {
        String sql = "SELECT id FROM enrollments WHERE student_id = ? AND course_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("[CourseDAO] isEnrolled error: " + e.getMessage());
        }
        return false;
    }

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

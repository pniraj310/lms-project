import java.time.LocalDate;

/**
 * Enrollment Service for LMS
 * Handles course enrollment and enrollment-related operations
 */
public class EnrollmentService {
    
    /**
     * View all available courses
     */
    public static void viewAllCourses() {
        java.util.List<Course> courses = CourseDAO.getAllCourses();
        
        if (courses.isEmpty()) {
            System.out.println("❌ No courses available yet!");
            return;
        }
        
        System.out.println("\n📚 AVAILABLE COURSES:");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        for (Course course : courses) {
            System.out.println("ID: " + course.getCourseId());
            System.out.println("Name: " + course.getCourseName());
            System.out.println("Description: " + course.getDescription());
            System.out.println("Credits: " + course.getCredits());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        }
    }
    
    /**
     * Enroll a user in a course
     * Progress starts at 0%
     */
    public static boolean enrollInCourse(int userId, int courseId) {
        // Check if user already enrolled
        java.util.List<Enrollment> enrollments = EnrollmentDAO.getEnrollmentsByUser(userId);
        
        for (Enrollment e : enrollments) {
            if (e.getCourseId() == courseId) {
                System.out.println("❌ You are already enrolled in this course!");
                return false;
            }
        }
        
        // Check if course exists
        Course course = CourseDAO.getCourseById(courseId);
        if (course == null) {
            System.out.println("❌ Course not found!");
            return false;
        }
        
        // Create enrollment with 0% progress
        Enrollment enrollment = new Enrollment(userId, courseId, LocalDate.now(), 0.0f, "Active");
        
        if (EnrollmentDAO.addEnrollment(enrollment)) {
            System.out.println("✅ Successfully enrolled in: " + course.getCourseName());
            System.out.println("   Progress: 0%");
            return true;
        } else {
            System.out.println("❌ Enrollment failed!");
            return false;
        }
    }
    
    /**
     * View user's enrolled courses
     */
    public static void viewMyEnrolledCourses(int userId) {
        java.util.List<Enrollment> enrollments = EnrollmentDAO.getEnrollmentsByUser(userId);
        
        if (enrollments.isEmpty()) {
            System.out.println("❌ You are not enrolled in any course yet!");
            return;
        }
        
        System.out.println("\n📚 YOUR ENROLLED COURSES:");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        for (Enrollment enrollment : enrollments) {
            Course course = CourseDAO.getCourseById(enrollment.getCourseId());
            if (course != null) {
                System.out.println("Course: " + course.getCourseName());
                System.out.println("Progress: " + enrollment.getProgress() + "%");
                System.out.println("Status: " + enrollment.getStatus());
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            }
        }
    }
    
    /**
     * Get progress of a user in a course
     */
    public static float getCourseProgress(int userId, int courseId) {
        return EnrollmentDAO.getProgress(userId, courseId);
    }
    
    /**
     * Drop a course
     */
    public static boolean dropCourse(int userId, int courseId) {
        java.util.List<Enrollment> enrollments = EnrollmentDAO.getEnrollmentsByUser(userId);
        
        for (Enrollment enrollment : enrollments) {
            if (enrollment.getCourseId() == courseId) {
                enrollment.setStatus("Dropped");
                if (EnrollmentDAO.updateEnrollment(enrollment)) {
                    System.out.println("✅ Course dropped successfully!");
                    return true;
                }
            }
        }
        
        System.out.println("❌ You are not enrolled in this course!");
        return false;
    }
}

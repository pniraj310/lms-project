/**
 * Dashboard Service for LMS
 * Displays user dashboard with all relevant information
 */
public class DashboardService {
    
    /**
     * Display student dashboard
     */
    public static void displayStudentDashboard(User user) {
        System.out.println("\n════════════════════════════════════════════════════════");
        System.out.println("         🎓 STUDENT DASHBOARD");
        System.out.println("════════════════════════════════════════════════════════");
        System.out.println("Welcome, " + user.getUsername() + "!");
        System.out.println("User ID: " + user.getUserId());
        System.out.println("Email: " + user.getEmail());
        System.out.println("════════════════════════════════════════════════════════\n");
        
        // Get enrolled courses
        java.util.List<Enrollment> enrollments = EnrollmentDAO.getEnrollmentsByUser(user.getUserId());
        
        System.out.println("📊 YOUR COURSE PROGRESS:");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        if (enrollments.isEmpty()) {
            System.out.println("You are not enrolled in any course yet.\n");
        } else {
            for (Enrollment enrollment : enrollments) {
                Course course = CourseDAO.getCourseById(enrollment.getCourseId());
                if (course != null) {
                    System.out.println("📚 " + course.getCourseName());
                    System.out.println("   Progress: " + String.format("%.1f", enrollment.getProgress()) + "%");
                    
                    // Show progress bar
                    System.out.print("   [");
                    int filled = (int) (enrollment.getProgress() / 5);
                    for (int i = 0; i < 20; i++) {
                        if (i < filled) System.out.print("█");
                        else System.out.print("░");
                    }
                    System.out.println("]");
                    
                    System.out.println("   Status: " + enrollment.getStatus());
                    System.out.println();
                }
            }
        }
        
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        // Average score across all courses
        float avgScore = ResultDAO.getAverageScore(user.getUserId());
        System.out.println("\n📈 OVERALL STATISTICS:");
        System.out.println("   Average Quiz Score: " + String.format("%.2f", avgScore) + "%");
    }
    
    /**
     * Display admin dashboard
     */
    public static void displayAdminDashboard(User user) {
        System.out.println("\n════════════════════════════════════════════════════════");
        System.out.println("         👨‍💼 ADMIN DASHBOARD");
        System.out.println("════════════════════════════════════════════════════════");
        System.out.println("Welcome, " + user.getUsername() + "!");
        System.out.println("════════════════════════════════════════════════════════\n");
        
        // Total users
        java.util.List<User> allUsers = UserDAO.getAllUsers();
        System.out.println("👥 SYSTEM STATISTICS:");
        System.out.println("   Total Users: " + allUsers.size());
        
        // Total courses
        java.util.List<Course> allCourses = CourseDAO.getAllCourses();
        System.out.println("   Total Courses: " + allCourses.size());
        
        // Total enrollments
        java.util.List<Enrollment> allEnrollments = EnrollmentDAO.getAllEnrollments();
        System.out.println("   Total Enrollments: " + allEnrollments.size());
        
        System.out.println("\n📚 COURSES:");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        if (allCourses.isEmpty()) {
            System.out.println("No courses available.");
        } else {
            for (Course course : allCourses) {
                System.out.println("ID: " + course.getCourseId());
                System.out.println("Name: " + course.getCourseName());
                System.out.println("Description: " + course.getDescription());
                System.out.println("Credits: " + course.getCredits());
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            }
        }
    }
    
    /**
     * Display instructor dashboard
     */
    public static void displayInstructorDashboard(User user) {
        System.out.println("\n════════════════════════════════════════════════════════");
        System.out.println("         👨‍🏫 INSTRUCTOR DASHBOARD");
        System.out.println("════════════════════════════════════════════════════════");
        System.out.println("Welcome, " + user.getUsername() + "!");
        System.out.println("User ID: " + user.getUserId());
        System.out.println("════════════════════════════════════════════════════════\n");
        
        // Get instructor's courses
        java.util.List<Course> courses = CourseDAO.getAllCourses();
        System.out.println("📚 YOUR COURSES:");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        boolean hasCourses = false;
        for (Course course : courses) {
            if (course.getInstructorId() == user.getUserId()) {
                hasCourses = true;
                System.out.println("ID: " + course.getCourseId());
                System.out.println("Name: " + course.getCourseName());
                
                // Get enrollments for this course
                java.util.List<Enrollment> enrollments = EnrollmentDAO.getEnrollmentsByCourse(course.getCourseId());
                System.out.println("Enrolled Students: " + enrollments.size());
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            }
        }
        
        if (!hasCourses) {
            System.out.println("You are not teaching any course yet.");
        }
    }
}

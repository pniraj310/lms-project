import java.time.LocalDate;
import java.util.List;

/**
 * Test class to demonstrate database operations
 * Shows how to use DAO classes to connect and work with SQL
 */
public class TestDatabase {
    
    public static void main(String[] args) {
        System.out.println("=== Testing LMS Database Connection ===\n");
        
        // Test 1: Test Database Connection
        testConnection();
        
        // Test 2: Get all users
        testGetAllUsers();
        
        // Test 3: Get all courses
        testGetAllCourses();
        
        // Test 4: Add a new user
        testAddUser();
        
        // Test 5: Get enrollments by user
        testGetEnrollmentsByUser();
    }
    
    /**
     * Test database connection
     */
    public static void testConnection() {
        System.out.println("Test 1: Testing Database Connection");
        try {
            DatabaseConnection.getConnection();
            System.out.println("✓ Connected to database successfully!\n");
        } catch (Exception e) {
            System.out.println("✗ Connection failed: " + e.getMessage() + "\n");
        }
    }
    
    /**
     * Test getting all users from database
     */
    public static void testGetAllUsers() {
        System.out.println("Test 2: Getting All Users");
        List<User> users = UserDAO.getAllUsers();
        
        if (users.isEmpty()) {
            System.out.println("No users found\n");
        } else {
            System.out.println("Found " + users.size() + " users:");
            for (User user : users) {
                System.out.println("  - " + user.getUsername() + " (" + user.getRole() + ")");
            }
            System.out.println();
        }
    }
    
    /**
     * Test getting all courses from database
     */
    public static void testGetAllCourses() {
        System.out.println("Test 3: Getting All Courses");
        List<Course> courses = CourseDAO.getAllCourses();
        
        if (courses.isEmpty()) {
            System.out.println("No courses found\n");
        } else {
            System.out.println("Found " + courses.size() + " courses:");
            for (Course course : courses) {
                System.out.println("  - " + course.getCourseName() + " (" + course.getCredits() + " credits)");
            }
            System.out.println();
        }
    }
    
    /**
     * Test adding a new user
     */
    public static void testAddUser() {
        System.out.println("Test 4: Adding a New User");
        User newUser = new User("student_charlie", "charlie@lms.com", "charlie123", "Student");
        
        boolean result = UserDAO.addUser(newUser);
        if (result) {
            System.out.println("✓ User added successfully!\n");
        } else {
            System.out.println("✗ Failed to add user (user might already exist)\n");
        }
    }
    
    /**
     * Test getting enrollments by user
     */
    public static void testGetEnrollmentsByUser() {
        System.out.println("Test 5: Getting Enrollments for User ID 3");
        List<Enrollment> enrollments = EnrollmentDAO.getEnrollmentsByUser(3);
        
        if (enrollments.isEmpty()) {
            System.out.println("No enrollments found\n");
        } else {
            System.out.println("Found " + enrollments.size() + " enrollments:");
            for (Enrollment enrollment : enrollments) {
                System.out.println("  - Course ID: " + enrollment.getCourseId() + ", Status: " + enrollment.getStatus());
            }
            System.out.println();
        }
    }
}

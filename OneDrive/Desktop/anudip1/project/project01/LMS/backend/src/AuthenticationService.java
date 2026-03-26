/**
 * Authentication Service for LMS
 * Handles user registration and login logic
 */
public class AuthenticationService {
    
    /**
     * Register a new user
     * @param username Username for registration
     * @param email Email for registration
     * @param password Password (consider hashing in production)
     * @param role Role of user (Student, Instructor, Admin)
     * @return true if registration successful
     */
    public static boolean register(String username, String email, String password, String role) {
        // Validation
        if (username == null || username.trim().isEmpty()) {
            System.out.println("❌ Username cannot be empty!");
            return false;
        }
        
        if (email == null || !email.contains("@")) {
            System.out.println("❌ Invalid email address!");
            return false;
        }
        
        if (password == null || password.length() < 6) {
            System.out.println("❌ Password must be at least 6 characters!");
            return false;
        }
        
        // Create user and save to database
        User newUser = new User(username, email, password, role);
        
        if (UserDAO.addUser(newUser)) {
            System.out.println("✅ User registered successfully!");
            System.out.println("   Username: " + username);
            System.out.println("   Email: " + email);
            System.out.println("   Role: " + role);
            return true;
        } else {
            System.out.println("❌ Registration failed! Email might already exist.");
            return false;
        }
    }
    
    /**
     * Login a user
     * @param email User email
     * @param password User password
     * @return User object if login successful, null otherwise
     */
    public static User login(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            System.out.println("❌ Email cannot be empty!");
            return null;
        }
        
        if (password == null || password.trim().isEmpty()) {
            System.out.println("❌ Password cannot be empty!");
            return null;
        }
        
        // Query database for user
        java.util.List<User> allUsers = UserDAO.getAllUsers();
        
        for (User user : allUsers) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                System.out.println("✅ Login successful!");
                System.out.println("   Welcome, " + user.getUsername() + "!");
                System.out.println("   Role: " + user.getRole());
                return user;
            }
        }
        
        System.out.println("❌ Login failed! Invalid email or password.");
        return null;
    }
    
    /**
     * Change user password
     */
    public static boolean changePassword(User user, String oldPassword, String newPassword) {
        if (!user.getPassword().equals(oldPassword)) {
            System.out.println("❌ Old password is incorrect!");
            return false;
        }
        
        if (newPassword == null || newPassword.length() < 6) {
            System.out.println("❌ New password must be at least 6 characters!");
            return false;
        }
        
        user.setPassword(newPassword);
        if (UserDAO.updateUser(user)) {
            System.out.println("✅ Password changed successfully!");
            return true;
        }
        
        return false;
    }
}

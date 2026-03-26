import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Main GUI Frame for LMS Frontend
 * Connected to PostgreSQL database using DAO classes
 * Provides user login and management interface
 */
public class LMSFrame extends JFrame {
    private JPanel mainPanel;
    private JTabbedPane tabbedPane;
    private JTable usersTable, coursesTable, enrollmentsTable;
    private DefaultTableModel usersModel, coursesModel, enrollmentsModel;
    private JLabel statusLabel;
    private User currentUser;

    public LMSFrame() {
        setTitle("Learning Management System (LMS) - Connected to Database");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create main panel with BorderLayout
        mainPanel = new JPanel(new BorderLayout());
        
        // Add status bar at the bottom
        statusLabel = new JLabel("Welcome to LMS - Not Logged In");
        statusLabel.setBackground(Color.LIGHT_GRAY);
        statusLabel.setOpaque(true);
        mainPanel.add(statusLabel, BorderLayout.SOUTH);
        
        // Create tabbed pane for different modules
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Login", createLoginPanel());
        tabbedPane.addTab("Users", createUsersPanel());
        tabbedPane.addTab("Courses", createCoursesPanel());
        tabbedPane.addTab("Enrollment", createEnrollmentPanel());
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel);
        setVisible(true);
    }

    /**
     * Login Panel - User authentication and registration with database
     */
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Title
        JLabel titleLabel = new JLabel("Welcome to LMS - Login or Register");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);
        
        // Separator
        JSeparator separator = new JSeparator();
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        panel.add(separator, gbc);
        
        // Username
        gbc.gridwidth = 1;
        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("Username:"), gbc);
        
        JTextField usernameField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);
        
        // Password
        gbc.gridy = 3;
        gbc.gridx = 0;
        panel.add(new JLabel("Password:"), gbc);
        
        JPasswordField passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);
        
        // Login Button
        JButton loginBtn = new JButton("Login");
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(loginBtn, gbc);
        
        // Login Button Action
        loginBtn.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter username and password");
                return;
            }
            
            currentUser = UserDAO.authenticateUser(username, password);
            if (currentUser != null) {
                statusLabel.setText("Logged in as: " + currentUser.getUsername() + " (" + currentUser.getRole() + ")");
                JOptionPane.showMessageDialog(this, "✓ Login Successful!");
                usernameField.setText("");
                passwordField.setText("");
                refreshAllTables();
            } else {
                JOptionPane.showMessageDialog(this, "✗ Invalid username or password\n\nNot registered? Click 'Register New User' below");
            }
        });
        
        // Register Button
        JButton registerBtn = new JButton("Register New User");
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        registerBtn.setBackground(new Color(34, 139, 34));
        registerBtn.setForeground(Color.WHITE);
        panel.add(registerBtn, gbc);
        
        // Register Button Action
        registerBtn.addActionListener(e -> showRegistrationDialog());
        
        // Add some vertical space
        gbc.gridy = 6;
        panel.add(Box.createVerticalStrut(20), gbc);
        
        // Info text
        JLabel infoLabel = new JLabel("<html><b>New User?</b> Click 'Register New User' to create an account<br>" +
                                       "<b>Already registered?</b> Enter your credentials and click 'Login'</html>");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        panel.add(infoLabel, gbc);
        
        return panel;
    }
    
    /**
     * Show registration dialog
     */
    private void showRegistrationDialog() {
        JTextField usernameField = new JTextField(15);
        JTextField emailField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);
        JPasswordField confirmPasswordField = new JPasswordField(15);
        JComboBox<String> roleCombo = new JComboBox<>(new String[] {"Student", "Instructor", "Admin"});
        roleCombo.setSelectedItem("Student"); // Default role
        
        Object[] message = {
            "═══ Create New Account ═══\n",
            "Username:", usernameField,
            "Email:", emailField,
            "Password:", passwordField,
            "Confirm Password:", confirmPasswordField,
            "Role:", roleCombo,
            "\nNote: Admin approval may be required"
        };
        
        int option = JOptionPane.showConfirmDialog(this, message, "Register New User", 
                                                   JOptionPane.OK_CANCEL_OPTION, 
                                                   JOptionPane.PLAIN_MESSAGE);
        
        if (option == JOptionPane.OK_OPTION) {
            // Validate input
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            String role = (String) roleCombo.getSelectedItem();
            
            // Validation checks
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "✗ All fields are required!");
                return;
            }
            
            if (username.length() < 3) {
                JOptionPane.showMessageDialog(this, "✗ Username must be at least 3 characters!");
                return;
            }
            
            if (password.length() < 4) {
                JOptionPane.showMessageDialog(this, "✗ Password must be at least 4 characters!");
                return;
            }
            
            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "✗ Passwords do not match!");
                return;
            }
            
            if (!email.contains("@")) {
                JOptionPane.showMessageDialog(this, "✗ Please enter a valid email!");
                return;
            }
            
            // Try to register user
            User newUser = new User(username, email, password, role);
            
            if (UserDAO.addUser(newUser)) {
                JOptionPane.showMessageDialog(this, 
                    "✓ Registration Successful!\n\n" +
                    "Username: " + username + "\n" +
                    "Role: " + role + "\n\n" +
                    "You can now login with your credentials.", 
                    "Registration Complete", 
                    JOptionPane.INFORMATION_MESSAGE);
                refreshUsersTable();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "✗ Registration Failed!\n\n" +
                    "Username or email already exists.\n" +
                    "Please try a different username or email.", 
                    "Registration Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Users Panel - Manage users with database integration
     */
    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Top panel with buttons
        JPanel topPanel = new JPanel();
        JButton refreshBtn = new JButton("Refresh");
        JButton addBtn = new JButton("Add User");
        JButton updateBtn = new JButton("Update User");
        JButton deleteBtn = new JButton("Delete User");
        
        topPanel.add(refreshBtn);
        topPanel.add(addBtn);
        topPanel.add(updateBtn);
        topPanel.add(deleteBtn);
        
        panel.add(topPanel, BorderLayout.NORTH);
        
        // Table
        usersModel = new DefaultTableModel(new String[] {"ID", "Username", "Email", "Role"}, 0);
        usersTable = new JTable(usersModel);
        panel.add(new JScrollPane(usersTable), BorderLayout.CENTER);
        
        // Refresh Button
        refreshBtn.addActionListener(e -> refreshUsersTable());
        
        // Add User Button
        addBtn.addActionListener(e -> showAddUserDialog());
        
        // Delete User Button
        deleteBtn.addActionListener(e -> deleteSelectedUser());
        
        // Load data on first view
        refreshUsersTable();
        
        return panel;
    }

    /**
     * Courses Panel - Manage courses with database integration
     */
    private JPanel createCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Top panel with buttons
        JPanel topPanel = new JPanel();
        JButton refreshBtn = new JButton("Refresh");
        JButton addBtn = new JButton("Add Course");
        JButton deleteBtn = new JButton("Delete Course");
        
        topPanel.add(refreshBtn);
        topPanel.add(addBtn);
        topPanel.add(deleteBtn);
        
        panel.add(topPanel, BorderLayout.NORTH);
        
        // Table
        coursesModel = new DefaultTableModel(new String[] {"ID", "Course Name", "Instructor ID", "Credits"}, 0);
        coursesTable = new JTable(coursesModel);
        panel.add(new JScrollPane(coursesTable), BorderLayout.CENTER);
        
        // Refresh Button
        refreshBtn.addActionListener(e -> refreshCoursesTable());
        
        // Add Course Button
        addBtn.addActionListener(e -> showAddCourseDialog());
        
        // Delete Course Button
        deleteBtn.addActionListener(e -> deleteSelectedCourse());
        
        // Load data
        refreshCoursesTable();
        
        return panel;
    }

    /**
     * Enrollment Panel - Manage enrollments with database integration
     */
    private JPanel createEnrollmentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Top panel with buttons
        JPanel topPanel = new JPanel();
        JButton refreshBtn = new JButton("Refresh");
        JButton addBtn = new JButton("Enroll Student");
        JButton deleteBtn = new JButton("Delete Enrollment");
        
        topPanel.add(refreshBtn);
        topPanel.add(addBtn);
        topPanel.add(deleteBtn);
        
        panel.add(topPanel, BorderLayout.NORTH);
        
        // Table
        enrollmentsModel = new DefaultTableModel(new String[] {"ID", "User ID", "Course ID", "Date", "Status"}, 0);
        enrollmentsTable = new JTable(enrollmentsModel);
        panel.add(new JScrollPane(enrollmentsTable), BorderLayout.CENTER);
        
        // Refresh Button
        refreshBtn.addActionListener(e -> refreshEnrollmentsTable());
        
        // Add Enrollment Button
        addBtn.addActionListener(e -> showAddEnrollmentDialog());
        
        // Delete Enrollment Button
        deleteBtn.addActionListener(e -> deleteSelectedEnrollment());
        
        // Load data
        refreshEnrollmentsTable();
        
        return panel;
    }

    /**
     * Refresh Users Table from Database
     */
    private void refreshUsersTable() {
        usersModel.setRowCount(0);
        List<User> users = UserDAO.getAllUsers();
        
        for (User user : users) {
            usersModel.addRow(new Object[] {
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
            });
        }
    }

    /**
     * Refresh Courses Table from Database
     */
    private void refreshCoursesTable() {
        coursesModel.setRowCount(0);
        List<Course> courses = CourseDAO.getAllCourses();
        
        for (Course course : courses) {
            coursesModel.addRow(new Object[] {
                course.getCourseId(),
                course.getCourseName(),
                course.getInstructorId(),
                course.getCredits()
            });
        }
    }

    /**
     * Refresh Enrollments Table from Database
     */
    private void refreshEnrollmentsTable() {
        enrollmentsModel.setRowCount(0);
        List<Enrollment> enrollments = EnrollmentDAO.getAllEnrollments();
        
        for (Enrollment enrollment : enrollments) {
            enrollmentsModel.addRow(new Object[] {
                enrollment.getEnrollmentId(),
                enrollment.getUserId(),
                enrollment.getCourseId(),
                enrollment.getEnrollmentDate(),
                enrollment.getStatus()
            });
        }
    }

    /**
     * Show dialog to add a new user
     */
    private void showAddUserDialog() {
        JTextField usernameField = new JTextField(15);
        JTextField emailField = new JTextField(15);
        JTextField passwordField = new JTextField(15);
        JComboBox<String> roleCombo = new JComboBox<>(new String[] {"Student", "Instructor", "Admin"});
        
        Object[] message = {
            "Username:", usernameField,
            "Email:", emailField,
            "Password:", passwordField,
            "Role:", roleCombo
        };
        
        int option = JOptionPane.showConfirmDialog(this, message, "Add New User", JOptionPane.OK_CANCEL_OPTION);
        
        if (option == JOptionPane.OK_OPTION) {
            User newUser = new User(usernameField.getText(), emailField.getText(), 
                                    passwordField.getText(), (String) roleCombo.getSelectedItem());
            
            if (UserDAO.addUser(newUser)) {
                JOptionPane.showMessageDialog(this, "User added successfully!");
                refreshUsersTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add user (username might already exist)");
            }
        }
    }

    /**
     * Show dialog to add a new course
     */
    private void showAddCourseDialog() {
        JTextField courseNameField = new JTextField(15);
        JTextField descriptionField = new JTextField(15);
        JTextField instructorIdField = new JTextField(15);
        JTextField creditsField = new JTextField("3", 15);
        
        Object[] message = {
            "Course Name:", courseNameField,
            "Description:", descriptionField,
            "Instructor ID:", instructorIdField,
            "Credits:", creditsField
        };
        
        int option = JOptionPane.showConfirmDialog(this, message, "Add New Course", JOptionPane.OK_CANCEL_OPTION);
        
        if (option == JOptionPane.OK_OPTION) {
            try {
                Course newCourse = new Course(courseNameField.getText(), descriptionField.getText(),
                                             Integer.parseInt(instructorIdField.getText()),
                                             Integer.parseInt(creditsField.getText()));
                
                if (CourseDAO.addCourse(newCourse)) {
                    JOptionPane.showMessageDialog(this, "Course added successfully!");
                    refreshCoursesTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add course");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers for ID and Credits");
            }
        }
    }

    /**
     * Show dialog to add enrollment
     */
    private void showAddEnrollmentDialog() {
        JTextField userIdField = new JTextField(15);
        JTextField courseIdField = new JTextField(15);
        JComboBox<String> statusCombo = new JComboBox<>(new String[] {"Active", "Completed", "Dropped"});
        
        Object[] message = {
            "User ID:", userIdField,
            "Course ID:", courseIdField,
            "Status:", statusCombo
        };
        
        int option = JOptionPane.showConfirmDialog(this, message, "Add Enrollment", JOptionPane.OK_CANCEL_OPTION);
        
        if (option == JOptionPane.OK_OPTION) {
            try {
                Enrollment newEnrollment = new Enrollment(Integer.parseInt(userIdField.getText()),
                                                         Integer.parseInt(courseIdField.getText()),
                                                         LocalDate.now(),
                                                         (String) statusCombo.getSelectedItem());
                
                if (EnrollmentDAO.addEnrollment(newEnrollment)) {
                    JOptionPane.showMessageDialog(this, "Enrollment added successfully!");
                    refreshEnrollmentsTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add enrollment");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter valid user and course IDs");
            }
        }
    }

    /**
     * Delete selected user
     */
    private void deleteSelectedUser() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete");
            return;
        }
        
        int userId = (int) usersModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure?");
        
        if (confirm == JOptionPane.OK_OPTION) {
            if (UserDAO.deleteUser(userId)) {
                JOptionPane.showMessageDialog(this, "User deleted successfully!");
                refreshUsersTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete user");
            }
        }
    }

    /**
     * Delete selected course
     */
    private void deleteSelectedCourse() {
        int selectedRow = coursesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a course to delete");
            return;
        }
        
        int courseId = (int) coursesModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure?");
        
        if (confirm == JOptionPane.OK_OPTION) {
            if (CourseDAO.deleteCourse(courseId)) {
                JOptionPane.showMessageDialog(this, "Course deleted successfully!");
                refreshCoursesTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete course");
            }
        }
    }

    /**
     * Delete selected enrollment
     */
    private void deleteSelectedEnrollment() {
        int selectedRow = enrollmentsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an enrollment to delete");
            return;
        }
        
        int enrollmentId = (int) enrollmentsModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure?");
        
        if (confirm == JOptionPane.OK_OPTION) {
            if (EnrollmentDAO.deleteEnrollment(enrollmentId)) {
                JOptionPane.showMessageDialog(this, "Enrollment deleted successfully!");
                refreshEnrollmentsTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete enrollment");
            }
        }
    }

    /**
     * Refresh all tables
     */
    private void refreshAllTables() {
        refreshUsersTable();
        refreshCoursesTable();
        refreshEnrollmentsTable();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LMSFrame());
    }
}

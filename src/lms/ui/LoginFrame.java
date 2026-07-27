package lms.ui;

import lms.dao.UserDAO;
import lms.model.User;
import lms.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * LoginFrame — Entry point UI
 * Keywords: JTabbedPane, Event Handling, Role-based Routing
 */
public class LoginFrame extends JFrame {

    private UserDAO userDAO = new UserDAO();

    // Login fields
    private JTextField     loginUsernameField;
    private JPasswordField loginPasswordField;

    // Register fields
    private JTextField     regNameField;
    private JTextField     regUsernameField;
    private JTextField     regEmailField;
    private JPasswordField regPasswordField;
    private JComboBox<String> regRoleBox;

    public LoginFrame() {
        UIHelper.setLookAndFeel();
        setTitle("LMS — Learning Management System");
        setSize(450, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIHelper.LIGHT_BG);

        // Header
        JPanel header = UIHelper.headerPanel("📚 LMS — SochTech");
        JLabel subtitle = new JLabel("Learning Management System", SwingConstants.CENTER);
        subtitle.setFont(UIHelper.SMALL_FONT);
        subtitle.setForeground(new Color(200, 220, 240));
        header.add(subtitle, BorderLayout.SOUTH);
        root.add(header, BorderLayout.NORTH);

        // Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UIHelper.BODY_FONT);
        tabs.addTab("Login",    buildLoginPanel());
        tabs.addTab("Register", buildRegisterPanel());
        tabs.setBorder(new EmptyBorder(10, 10, 10, 10));
        root.add(tabs, BorderLayout.CENTER);

        // Footer
        JLabel footer = new JLabel("© 2024 SochTech | Niraj Patel", SwingConstants.CENTER);
        footer.setFont(UIHelper.SMALL_FONT);
        footer.setForeground(Color.GRAY);
        footer.setBorder(new EmptyBorder(5, 0, 8, 0));
        root.add(footer, BorderLayout.SOUTH);

        add(root);
    }

    // ── LOGIN PANEL ──
    private JPanel buildLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIHelper.WHITE);
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        loginUsernameField = UIHelper.styledField(20);
        loginPasswordField = UIHelper.styledPasswordField(20);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel heading = UIHelper.heading("Welcome Back 👋");
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(heading, gbc);

        gbc.gridy = 1; gbc.gridwidth = 1;
        panel.add(UIHelper.label("Username:"), gbc);
        gbc.gridx = 1;
        panel.add(loginUsernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(UIHelper.label("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(loginPasswordField, gbc);

        JButton loginBtn = UIHelper.primaryButton("Login");
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 5, 5);
        panel.add(loginBtn, gbc);

        // Demo hint
        JLabel hint = new JLabel("<html><center><font color='gray' size='2'>" +
                "Demo: admin/admin123 | teacher1/teach123 | student1/stud123</font></center></html>",
                SwingConstants.CENTER);
        gbc.gridy = 4;
        panel.add(hint, gbc);

        loginBtn.addActionListener(e -> handleLogin());
        loginPasswordField.addActionListener(e -> handleLogin()); // Enter key

        return panel;
    }

    // ── REGISTER PANEL ──
    private JPanel buildRegisterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIHelper.WHITE);
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 5, 6, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        regNameField     = UIHelper.styledField(20);
        regUsernameField = UIHelper.styledField(20);
        regEmailField    = UIHelper.styledField(20);
        regPasswordField = UIHelper.styledPasswordField(20);
        regRoleBox       = new JComboBox<>(new String[]{"student", "teacher"});
        regRoleBox.setFont(UIHelper.BODY_FONT);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel heading = UIHelper.heading("Create Account ✍️");
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(heading, gbc);

        Object[][] fields = {
            {"Full Name:", regNameField},
            {"Username:", regUsernameField},
            {"Email:", regEmailField},
            {"Password:", regPasswordField},
            {"Role:", regRoleBox}
        };

        for (int i = 0; i < fields.length; i++) {
            gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = i + 1;
            panel.add(UIHelper.label((String) fields[i][0]), gbc);
            gbc.gridx = 1;
            panel.add((Component) fields[i][1], gbc);
        }

        JButton regBtn = UIHelper.successButton("Register");
        gbc.gridx = 0; gbc.gridy = fields.length + 1;
        gbc.gridwidth = 2; gbc.insets = new Insets(15, 5, 5, 5);
        panel.add(regBtn, gbc);

        regBtn.addActionListener(e -> handleRegister());
        return panel;
    }

    // ── HANDLERS ──
    private void handleLogin() {
        String username = loginUsernameField.getText().trim();
        String password = new String(loginPasswordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            UIHelper.showError(this, "Please enter username and password.");
            return;
        }

        User user = userDAO.login(username, password);
        if (user == null) {
            UIHelper.showError(this, "Invalid username or password!");
            loginPasswordField.setText("");
            return;
        }

        // Role-based routing — Polymorphism via different dashboard classes
        dispose();
        switch (user.getRole()) {
            case "admin":   new AdminDashboard(user).setVisible(true);   break;
            case "teacher": new TeacherDashboard(user).setVisible(true); break;
            default:        new StudentDashboard(user).setVisible(true); break;
        }
    }

    private void handleRegister() {
        String name     = regNameField.getText().trim();
        String username = regUsernameField.getText().trim();
        String email    = regEmailField.getText().trim();
        String password = new String(regPasswordField.getPassword()).trim();
        String role     = (String) regRoleBox.getSelectedItem();

        if (name.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            UIHelper.showError(this, "All fields are required.");
            return;
        }
        if (password.length() < 5) {
            UIHelper.showError(this, "Password must be at least 5 characters.");
            return;
        }

        User newUser = new User(0, name, username, email, password, role);
        if (userDAO.register(newUser)) {
            UIHelper.showInfo(this, "Registration successful! You can now login.");
            regNameField.setText(""); regUsernameField.setText("");
            regEmailField.setText(""); regPasswordField.setText("");
        } else {
            UIHelper.showError(this, "Username or email already exists!");
        }
    }
}

package lms.ui;

import lms.dao.UserDAO;
import lms.model.User;
import lms.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * LoginFrame.java
 * ----------------
 * The first screen the user sees.
 * Handles Login and Registration via a tabbed pane.
 *
 * On successful login it opens:
 *  → StudentDashboard  (role = student)
 *  → TeacherDashboard  (role = teacher)
 *  → AdminDashboard    (role = admin)
 */
public class LoginFrame extends JFrame {

    // ── DAO ──────────────────────────────────────────────────
    private UserDAO userDAO = new UserDAO();

    // ── Login panel fields ───────────────────────────────────
    private JTextField     loginUsernameField;
    private JPasswordField loginPasswordField;

    // ── Register panel fields ────────────────────────────────
    private JTextField     regUsernameField;
    private JPasswordField regPasswordField;
    private JPasswordField regConfirmField;
    private JTextField     regFullNameField;
    private JTextField     regEmailField;
    private JComboBox<String> regRoleCombo;

    // ─────────────────────────────────────────────────────────
    public LoginFrame() {
        UIHelper.setLookAndFeel();
        initUI();
        setTitle("LMS — Learning Management System");
        setSize(480, 540);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        UIHelper.centreWindow(this);
        setVisible(true);
    }

    // ─────────────────────────────────────────────────────────
    private void initUI() {
        // Outer wrapper
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(UIHelper.PRIMARY);
        wrapper.setBorder(new EmptyBorder(0, 0, 0, 0));

        // ── Header banner ──
        JPanel banner = new JPanel(new GridLayout(2, 1));
        banner.setBackground(UIHelper.PRIMARY);
        banner.setBorder(new EmptyBorder(24, 20, 16, 20));

        JLabel titleLbl = new JLabel("📚  Learning Management System", JLabel.CENTER);
        titleLbl.setFont(UIHelper.FONT_TITLE);
        titleLbl.setForeground(Color.WHITE);

        JLabel subLbl = new JLabel("Quiz · Courses · Progress · Chatbot", JLabel.CENTER);
        subLbl.setFont(UIHelper.FONT_SMALL);
        subLbl.setForeground(new Color(200, 230, 255));

        banner.add(titleLbl);
        banner.add(subLbl);

        // ── Tabbed pane ──
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UIHelper.FONT_BUTTON);
        tabs.addTab("  Login  ",   buildLoginPanel());
        tabs.addTab("  Register  ", buildRegisterPanel());

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(UIHelper.WHITE);
        center.add(tabs, BorderLayout.CENTER);

        wrapper.add(banner, BorderLayout.NORTH);
        wrapper.add(center, BorderLayout.CENTER);
        setContentPane(wrapper);
    }

    // ─────────────────────────────────────────────────────────
    // LOGIN PANEL
    // ─────────────────────────────────────────────────────────
    private JPanel buildLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIHelper.WHITE);
        panel.setBorder(new EmptyBorder(30, 40, 30, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.fill   = GridBagConstraints.HORIZONTAL;
        gbc.gridx  = 0; gbc.weightx = 1;

        // Fields
        loginUsernameField = UIHelper.textField(20);
        loginPasswordField = UIHelper.passwordField(20);

        gbc.gridy = 0; panel.add(UIHelper.normalLabel("Username:"), gbc);
        gbc.gridy = 1; panel.add(loginUsernameField, gbc);
        gbc.gridy = 2; panel.add(UIHelper.normalLabel("Password:"), gbc);
        gbc.gridy = 3; panel.add(loginPasswordField, gbc);

        // Login button
        JButton loginBtn = UIHelper.primaryButton("🔐  Login");
        loginBtn.setPreferredSize(new Dimension(200, 40));
        loginBtn.addActionListener(this::doLogin);

        gbc.gridy = 4;
        gbc.insets = new Insets(20, 0, 0, 0);
        panel.add(loginBtn, gbc);

        // Allow Enter key to trigger login
        loginPasswordField.addActionListener(this::doLogin);

        return panel;
    }

    // ─────────────────────────────────────────────────────────
    // REGISTER PANEL
    // ─────────────────────────────────────────────────────────
    private JPanel buildRegisterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIHelper.WHITE);
        panel.setBorder(new EmptyBorder(20, 40, 20, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.fill   = GridBagConstraints.HORIZONTAL;
        gbc.gridx  = 0; gbc.weightx = 1;

        regFullNameField  = UIHelper.textField(20);
        regUsernameField  = UIHelper.textField(20);
        regEmailField     = UIHelper.textField(20);
        regPasswordField  = UIHelper.passwordField(20);
        regConfirmField   = UIHelper.passwordField(20);
        regRoleCombo      = new JComboBox<>(new String[]{"student", "teacher"});
        regRoleCombo.setFont(UIHelper.FONT_NORMAL);

        String[][] rows = {
            {"Full Name:", null},  {"Username:", null},
            {"Email:",     null},  {"Password:", null},
            {"Confirm Password:", null}, {"Register As:", null}
        };
        JComponent[] fields = {regFullNameField, regUsernameField,
                                regEmailField,    regPasswordField,
                                regConfirmField,  regRoleCombo};

        for (int i = 0; i < fields.length; i++) {
            gbc.gridy = i * 2;     panel.add(UIHelper.normalLabel(rows[i][0]), gbc);
            gbc.gridy = i * 2 + 1; panel.add(fields[i], gbc);
        }

        JButton regBtn = UIHelper.successButton("✅  Create Account");
        regBtn.setPreferredSize(new Dimension(220, 40));
        regBtn.addActionListener(this::doRegister);

        gbc.gridy = 12;
        gbc.insets = new Insets(15, 0, 0, 0);
        panel.add(regBtn, gbc);

        return panel;
    }

    // ─────────────────────────────────────────────────────────
    // LOGIN ACTION
    // ─────────────────────────────────────────────────────────
    private void doLogin(ActionEvent e) {
        String username = loginUsernameField.getText().trim();
        String password = new String(loginPasswordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            UIHelper.showError(this, "Please enter both username and password.");
            return;
        }

        User user = userDAO.login(username, password);

        if (user == null) {
            UIHelper.showError(this, "Invalid username or password. Please try again.");
            loginPasswordField.setText("");
            return;
        }

        // ── Route to the correct dashboard ──
        dispose();   // close login window
        switch (user.getRole()) {
            case "student": new StudentDashboard(user); break;
            case "teacher": new TeacherDashboard(user); break;
            case "admin":   new AdminDashboard(user);   break;
            default:
                UIHelper.showError(null, "Unknown role: " + user.getRole());
        }
    }

    // ─────────────────────────────────────────────────────────
    // REGISTER ACTION
    // ─────────────────────────────────────────────────────────
    private void doRegister(ActionEvent e) {
        String fullName  = regFullNameField.getText().trim();
        String username  = regUsernameField.getText().trim();
        String email     = regEmailField.getText().trim();
        String password  = new String(regPasswordField.getPassword()).trim();
        String confirm   = new String(regConfirmField.getPassword()).trim();
        String role      = (String) regRoleCombo.getSelectedItem();

        // ── Validation ──
        if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            UIHelper.showError(this, "All fields are required!");
            return;
        }
        if (!password.equals(confirm)) {
            UIHelper.showError(this, "Passwords do not match!");
            return;
        }
        if (password.length() < 4) {
            UIHelper.showError(this, "Password must be at least 4 characters.");
            return;
        }
        if (!email.contains("@")) {
            UIHelper.showError(this, "Please enter a valid email address.");
            return;
        }

        User newUser = new User(username, password, fullName, email, role);
        boolean success = userDAO.register(newUser);

        if (success) {
            UIHelper.showSuccess(this, "Account created! You can now log in as " + username);
            clearRegisterFields();
        } else {
            UIHelper.showError(this, "Registration failed. Username or email may already be in use.");
        }
    }

    private void clearRegisterFields() {
        regFullNameField.setText("");
        regUsernameField.setText("");
        regEmailField.setText("");
        regPasswordField.setText("");
        regConfirmField.setText("");
    }
}

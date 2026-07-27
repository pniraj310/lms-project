package lms.ui;

import lms.dao.*;
import lms.model.*;
import lms.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * AdminDashboard — Full control panel for admin
 * Keywords: CRUD, User Management, System Statistics
 */
public class AdminDashboard extends JFrame {

    private User        admin;
    private UserDAO     userDAO     = new UserDAO();
    private CourseDAO   courseDAO   = new CourseDAO();
    private ChatbotDAO  chatbotDAO  = new ChatbotDAO();

    public AdminDashboard(User admin) {
        this.admin = admin;
        setTitle("LMS — Admin Panel");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        JPanel header = UIHelper.headerPanel("🛡️ Admin Dashboard — " + admin.getName());
        JButton logoutBtn = UIHelper.dangerButton("Logout");
        logoutBtn.addActionListener(e -> { dispose(); new LoginFrame().setVisible(true); });
        header.add(logoutBtn, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UIHelper.BODY_FONT);
        tabs.addTab("👥 Users",      buildUsersTab());
        tabs.addTab("📚 Courses",    buildCoursesTab());
        tabs.addTab("🤖 Chatbot",    buildChatbotTab());
        tabs.addTab("📊 Statistics", buildStatsTab());
        root.add(tabs, BorderLayout.CENTER);
        add(root);
    }

    // ── USERS TAB ──
    private JPanel buildUsersTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        List<User> users = userDAO.getAllUsers();
        String[] cols = {"ID", "Name", "Username", "Email", "Role"};
        Object[][] data = new Object[users.size()][5];
        for (int i = 0; i < users.size(); i++) {
            User u = users.get(i);
            data[i][0] = u.getId(); data[i][1] = u.getName();
            data[i][2] = u.getUsername(); data[i][3] = u.getEmail();
            data[i][4] = u.getRole();
        }

        JTable table = UIHelper.styledTable(cols, data);

        JButton deleteBtn = UIHelper.dangerButton("Delete Selected User");
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { UIHelper.showError(panel, "Select a user to delete."); return; }
            if (users.get(row).getRole().equals("admin")) {
                UIHelper.showError(panel, "Cannot delete admin user!"); return;
            }
            if (UIHelper.confirm(panel, "Delete user: " + users.get(row).getName() + "?")) {
                userDAO.deleteUser(users.get(row).getId());
                UIHelper.showInfo(panel, "User deleted. Restart to refresh.");
            }
        });

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.add(deleteBtn);

        panel.add(UIHelper.heading("All Users (" + users.size() + ")"), BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    // ── COURSES TAB ──
    private JPanel buildCoursesTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        List<Course> courses = courseDAO.getAllCourses();
        String[] cols = {"ID", "Title", "Teacher", "Description"};
        Object[][] data = new Object[courses.size()][4];
        for (int i = 0; i < courses.size(); i++) {
            Course c = courses.get(i);
            data[i][0] = c.getId(); data[i][1] = c.getTitle();
            data[i][2] = c.getTeacherName(); data[i][3] = c.getDescription();
        }

        JTable table = UIHelper.styledTable(cols, data);

        JButton deleteBtn = UIHelper.dangerButton("Delete Selected Course");
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { UIHelper.showError(panel, "Select a course to delete."); return; }
            if (UIHelper.confirm(panel, "Delete course: " + courses.get(row).getTitle() + "?")) {
                courseDAO.deleteCourse(courses.get(row).getId());
                UIHelper.showInfo(panel, "Course deleted. Restart to refresh.");
            }
        });

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.add(deleteBtn);

        panel.add(UIHelper.heading("All Courses (" + courses.size() + ")"), BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    // ── CHATBOT TAB ──
    private JPanel buildChatbotTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        Map<String, String> qa = chatbotDAO.getAllQA();
        String[] cols = {"Keyword", "Answer"};
        Object[][] data = new Object[qa.size()][2];
        int i = 0;
        for (Map.Entry<String, String> entry : qa.entrySet()) {
            data[i][0] = entry.getKey();
            data[i][1] = entry.getValue();
            i++;
        }
        JTable table = UIHelper.styledTable(cols, data);

        // Add new QA pair
        JTextField keyField = UIHelper.styledField(15);
        JTextField ansField = UIHelper.styledField(30);
        JButton addBtn = UIHelper.successButton("Add Q&A");

        addBtn.addActionListener(e -> {
            String key = keyField.getText().trim();
            String ans = ansField.getText().trim();
            if (key.isEmpty() || ans.isEmpty()) {
                UIHelper.showError(panel, "Keyword and answer both required."); return;
            }
            if (chatbotDAO.addQA(key, ans)) {
                UIHelper.showInfo(panel, "Q&A pair added successfully!");
                keyField.setText(""); ansField.setText("");
            } else {
                UIHelper.showError(panel, "Failed to add Q&A pair.");
            }
        });

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        form.add(UIHelper.label("Keyword:")); form.add(keyField);
        form.add(UIHelper.label("Answer:"));  form.add(ansField);
        form.add(addBtn);

        panel.add(UIHelper.heading("Chatbot Q&A Management"), BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(form, BorderLayout.SOUTH);
        return panel;
    }

    // ── STATISTICS TAB ──
    private JPanel buildStatsTab() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 20, 20));
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        panel.setBackground(UIHelper.LIGHT_BG);

        int totalUsers   = userDAO.getAllUsers().size();
        int totalCourses = courseDAO.getAllCourses().size();

        panel.add(statCard("👥 Total Users",   String.valueOf(totalUsers),   UIHelper.PRIMARY));
        panel.add(statCard("📚 Total Courses", String.valueOf(totalCourses), UIHelper.SUCCESS));
        panel.add(statCard("🛡️ System",       "Online ✅",                  UIHelper.DARK));
        panel.add(statCard("🤖 Chatbot QA",   chatbotDAO.getAllQA().size() + " pairs", UIHelper.WARNING));

        return panel;
    }

    private JPanel statCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLbl = new JLabel(title, SwingConstants.CENTER);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLbl.setForeground(Color.WHITE);

        JLabel valueLbl = new JLabel(value, SwingConstants.CENTER);
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLbl.setForeground(Color.WHITE);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valueLbl, BorderLayout.CENTER);
        return card;
    }
}

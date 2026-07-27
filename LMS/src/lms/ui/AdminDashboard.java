package lms.ui;

import lms.dao.ChatbotDAO;
import lms.dao.CourseDAO;
import lms.dao.UserDAO;
import lms.model.Course;
import lms.model.User;
import lms.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * AdminDashboard.java
 * --------------------
 * Admin panel with full control over users, courses, and chatbot.
 * Tabs: All Users | All Courses | Chatbot Manager | Stats
 */
public class AdminDashboard extends JFrame {

    private User       admin;
    private UserDAO    userDAO   = new UserDAO();
    private CourseDAO  courseDAO = new CourseDAO();
    private ChatbotDAO chatbot   = new ChatbotDAO();

    private List<User>   allUsers;
    private List<Course> allCourses;

    // ─────────────────────────────────────────────────────────
    public AdminDashboard(User admin) {
        this.admin = admin;
        initUI();
        setTitle("LMS — Admin Dashboard");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        UIHelper.centreWindow(this);
        setVisible(true);
    }

    // ─────────────────────────────────────────────────────────
    private void initUI() {
        setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIHelper.DANGER);
        header.setBorder(new EmptyBorder(10, 20, 10, 20));
        JLabel title = new JLabel("🔧  " + admin.getFullName() + "  |  Admin Dashboard");
        title.setFont(UIHelper.FONT_HEADER);
        title.setForeground(Color.WHITE);
        JButton logout = UIHelper.warningButton("Logout");
        logout.addActionListener(e -> { dispose(); new LoginFrame(); });
        header.add(title,  BorderLayout.WEST);
        header.add(logout, BorderLayout.EAST);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UIHelper.FONT_BUTTON);
        tabs.addTab("👥 All Users",       buildUsersPanel());
        tabs.addTab("📚 All Courses",      buildCoursesPanel());
        tabs.addTab("🤖 Chatbot Manager", buildChatbotPanel());
        tabs.addTab("📊 System Stats",    buildStatsPanel());

        add(header, BorderLayout.NORTH);
        add(tabs,   BorderLayout.CENTER);
    }

    // ─────────────────────────────────────────────────────────
    // TAB 1 — ALL USERS (view + delete)
    // ─────────────────────────────────────────────────────────
    private JPanel buildUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(UIHelper.WHITE);

        String[] cols = {"#", "Full Name", "Username", "Email", "Role", "ID"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        UIHelper.styleTable(table);
        table.getColumnModel().getColumn(5).setMinWidth(0);
        table.getColumnModel().getColumn(5).setMaxWidth(0); // hide ID column

        allUsers = userDAO.getAllUsers();
        for (int i = 0; i < allUsers.size(); i++) {
            User u = allUsers.get(i);
            model.addRow(new Object[]{i + 1, u.getFullName(), u.getUsername(), u.getEmail(), u.getRole(), u.getId()});
        }

        // Filter by role
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(UIHelper.WHITE);
        String[] roles = {"All", "student", "teacher", "admin"};
        JComboBox<String> roleFilter = new JComboBox<>(roles);
        roleFilter.setFont(UIHelper.FONT_NORMAL);
        roleFilter.addActionListener(e -> {
            String sel = (String) roleFilter.getSelectedItem();
            model.setRowCount(0);
            int idx = 1;
            for (User u : allUsers) {
                if ("All".equals(sel) || u.getRole().equals(sel)) {
                    model.addRow(new Object[]{idx++, u.getFullName(), u.getUsername(),
                                              u.getEmail(), u.getRole(), u.getId()});
                }
            }
        });
        filterPanel.add(UIHelper.normalLabel("Filter by role:"));
        filterPanel.add(roleFilter);

        JButton deleteBtn = UIHelper.dangerButton("🗑  Delete Selected User");
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { UIHelper.showWarning(panel, "Select a user."); return; }
            String name = (String) model.getValueAt(row, 1);
            int    uid  = (int)    model.getValueAt(row, 5);
            if (uid == admin.getId()) { UIHelper.showError(panel, "Cannot delete yourself!"); return; }
            if (!UIHelper.confirm(panel, "Delete user: " + name + "?")) return;
            if (userDAO.deleteUser(uid)) {
                model.removeRow(row);
                allUsers.removeIf(u -> u.getId() == uid);
                UIHelper.showSuccess(panel, "User deleted.");
            }
        });

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(UIHelper.WHITE);
        top.add(UIHelper.headerLabel("All Users  (" + allUsers.size() + ")"), BorderLayout.WEST);
        top.add(filterPanel, BorderLayout.EAST);

        panel.add(top,   BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(deleteBtn, BorderLayout.SOUTH);
        return panel;
    }

    // ─────────────────────────────────────────────────────────
    // TAB 2 — ALL COURSES
    // ─────────────────────────────────────────────────────────
    private JPanel buildCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(UIHelper.WHITE);

        String[] cols = {"#", "Course Title", "Teacher", "Students Enrolled"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        UIHelper.styleTable(table);

        allCourses = courseDAO.getAllCourses();
        for (int i = 0; i < allCourses.size(); i++) {
            Course c = allCourses.get(i);
            model.addRow(new Object[]{
                i + 1, c.getTitle(), c.getTeacherName(),
                courseDAO.getEnrolledCount(c.getId())
            });
        }

        JButton deleteBtn = UIHelper.dangerButton("🗑  Delete Selected Course");
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { UIHelper.showWarning(panel, "Select a course."); return; }
            if (!UIHelper.confirm(panel, "Delete course: " + allCourses.get(row).getTitle() + "?")) return;
            if (courseDAO.deleteCourse(allCourses.get(row).getId())) {
                model.removeRow(row);
                allCourses.remove(row);
                UIHelper.showSuccess(panel, "Course deleted.");
            }
        });

        panel.add(UIHelper.headerLabel("All Courses  (" + allCourses.size() + ")"), BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(deleteBtn, BorderLayout.SOUTH);
        return panel;
    }

    // ─────────────────────────────────────────────────────────
    // TAB 3 — CHATBOT MANAGER (Add new Q&A)
    // ─────────────────────────────────────────────────────────
    private JPanel buildChatbotPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIHelper.WHITE);
        panel.setBorder(new EmptyBorder(30, 60, 30, 60));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.fill   = GridBagConstraints.HORIZONTAL;
        gbc.gridx  = 0; gbc.weightx = 1;

        JTextField keywordField  = UIHelper.textField(30);
        JTextField questionField = UIHelper.textField(30);
        JTextArea  answerArea    = UIHelper.textArea(4, 30);
        JScrollPane answerScroll = new JScrollPane(answerArea);
        answerScroll.setPreferredSize(new Dimension(400, 90));

        gbc.gridy = 0; panel.add(UIHelper.headerLabel("Add Chatbot Q&A Pair"), gbc);
        gbc.gridy = 1; panel.add(UIHelper.normalLabel("Trigger Keyword (e.g. 'enroll', 'grade'):"), gbc);
        gbc.gridy = 2; panel.add(keywordField, gbc);
        gbc.gridy = 3; panel.add(UIHelper.normalLabel("Sample Question:"), gbc);
        gbc.gridy = 4; panel.add(questionField, gbc);
        gbc.gridy = 5; panel.add(UIHelper.normalLabel("Bot Answer:"), gbc);
        gbc.gridy = 6; panel.add(answerScroll, gbc);

        JLabel cacheInfo = UIHelper.normalLabel("Current Q&A pairs in DB: " + chatbot.getCacheSize());
        cacheInfo.setForeground(UIHelper.PRIMARY);
        gbc.gridy = 7; panel.add(cacheInfo, gbc);

        JButton addBtn = UIHelper.successButton("➕  Add Q&A Pair");
        addBtn.addActionListener(e -> {
            String kw = keywordField.getText().trim();
            String q  = questionField.getText().trim();
            String a  = answerArea.getText().trim();
            if (kw.isEmpty() || q.isEmpty() || a.isEmpty()) {
                UIHelper.showError(panel, "All fields are required!");
                return;
            }
            if (chatbot.addQA(kw, q, a)) {
                UIHelper.showSuccess(panel, "Q&A pair added! Keyword: '" + kw + "'");
                keywordField.setText(""); questionField.setText(""); answerArea.setText("");
                cacheInfo.setText("Current Q&A pairs in DB: " + chatbot.getCacheSize());
            } else {
                UIHelper.showError(panel, "Failed to add Q&A pair.");
            }
        });

        gbc.gridy = 8; gbc.insets = new Insets(20, 0, 0, 0);
        panel.add(addBtn, gbc);
        return panel;
    }

    // ─────────────────────────────────────────────────────────
    // TAB 4 — SYSTEM STATS
    // ─────────────────────────────────────────────────────────
    private JPanel buildStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 20, 20));
        panel.setBorder(new EmptyBorder(30, 40, 30, 40));
        panel.setBackground(UIHelper.WHITE);

        allUsers   = userDAO.getAllUsers();
        allCourses = courseDAO.getAllCourses();

        long studentCount = allUsers.stream().filter(User::isStudent).count();
        long teacherCount = allUsers.stream().filter(User::isTeacher).count();

        panel.add(statCard("👥 Total Users",    String.valueOf(allUsers.size()),  UIHelper.PRIMARY));
        panel.add(statCard("🎓 Students",        String.valueOf(studentCount),    UIHelper.SUCCESS));
        panel.add(statCard("📖 Teachers",        String.valueOf(teacherCount),    UIHelper.WARNING));
        panel.add(statCard("📚 Total Courses",   String.valueOf(allCourses.size()), new Color(155, 89, 182)));
        panel.add(statCard("🤖 Chatbot Q&A",    String.valueOf(chatbot.getCacheSize()), new Color(22, 160, 133)));
        panel.add(statCard("🔧 System",         "Operational", UIHelper.SUCCESS));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(UIHelper.WHITE);
        wrapper.setBorder(new EmptyBorder(10, 10, 10, 10));
        wrapper.add(UIHelper.headerLabel("System Statistics"), BorderLayout.NORTH);
        wrapper.add(panel, BorderLayout.CENTER);
        return wrapper;
    }

    /** Creates a coloured stat card */
    private JPanel statCard(String label, String value, Color color) {
        JPanel card = new JPanel(new GridLayout(2, 1));
        card.setBackground(color);
        card.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel valueLbl = new JLabel(value, JLabel.CENTER);
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLbl.setForeground(Color.WHITE);

        JLabel nameLbl = new JLabel(label, JLabel.CENTER);
        nameLbl.setFont(UIHelper.FONT_BUTTON);
        nameLbl.setForeground(new Color(220, 240, 255));

        card.add(valueLbl);
        card.add(nameLbl);
        return card;
    }
}

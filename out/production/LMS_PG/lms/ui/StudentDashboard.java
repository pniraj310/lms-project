package lms.ui;

import lms.dao.*;
import lms.model.*;
import lms.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * StudentDashboard — Main screen for students
 * Keywords: JTabbedPane, Role-based UI, Event-driven Programming
 */
public class StudentDashboard extends JFrame {

    private User         student;
    private CourseDAO    courseDAO    = new CourseDAO();
    private QuizDAO      quizDAO      = new QuizDAO();
    private ResultDAO    resultDAO    = new ResultDAO();
    private ChatbotDAO   chatbotDAO   = new ChatbotDAO();

    private JTextArea chatOutput;
    private JTextField chatInput;

    public StudentDashboard(User student) {
        this.student = student;
        chatbotDAO.loadQA();

        setTitle("LMS — Student: " + student.getName());
        setSize(900, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());

        // Header
        JPanel header = UIHelper.headerPanel("📚 Student Dashboard — " + student.getName());
        JButton logoutBtn = UIHelper.dangerButton("Logout");
        logoutBtn.addActionListener(e -> logout());
        header.add(logoutBtn, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        // Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UIHelper.BODY_FONT);
        tabs.addTab("📖 My Courses",   buildMyCoursesTab());
        tabs.addTab("🌐 All Courses",  buildAllCoursesTab());
        tabs.addTab("📝 Quizzes",      buildQuizzesTab());
        tabs.addTab("📊 Results",      buildResultsTab());
        tabs.addTab("📈 Progress",     buildProgressTab());
        tabs.addTab("🤖 Chatbot",      buildChatbotTab());
        root.add(tabs, BorderLayout.CENTER);

        add(root);
    }

    // ── MY COURSES TAB ──
    private JPanel buildMyCoursesTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        List<Course> courses = courseDAO.getEnrolledCourses(student.getId());
        String[] cols = {"#", "Course Title", "Teacher"};
        Object[][] data = new Object[courses.size()][3];
        for (int i = 0; i < courses.size(); i++) {
            data[i][0] = i + 1;
            data[i][1] = courses.get(i).getTitle();
            data[i][2] = courses.get(i).getTeacherName();
        }

        JTable table = UIHelper.styledTable(cols, data);
        panel.add(UIHelper.heading("My Enrolled Courses (" + courses.size() + ")"), BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    // ── ALL COURSES TAB ──
    private JPanel buildAllCoursesTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        List<Course> courses = courseDAO.getAllCourses();
        String[] cols = {"#", "Course Title", "Teacher", "Status"};
        Object[][] data = new Object[courses.size()][4];
        for (int i = 0; i < courses.size(); i++) {
            Course c = courses.get(i);
            data[i][0] = i + 1;
            data[i][1] = c.getTitle();
            data[i][2] = c.getTeacherName();
            data[i][3] = courseDAO.isEnrolled(student.getId(), c.getId()) ? "✅ Enrolled" : "Not Enrolled";
        }

        JTable table = UIHelper.styledTable(cols, data);
        JScrollPane scroll = new JScrollPane(table);

        JButton enrollBtn = UIHelper.successButton("Enroll in Selected");
        enrollBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { UIHelper.showError(panel, "Select a course first."); return; }
            Course selected = courses.get(row);
            if (courseDAO.isEnrolled(student.getId(), selected.getId())) {
                UIHelper.showInfo(panel, "Already enrolled in: " + selected.getTitle());
            } else {
                courseDAO.enrollStudent(student.getId(), selected.getId());
                UIHelper.showInfo(panel, "Enrolled in: " + selected.getTitle() + "!");
                data[row][3] = "✅ Enrolled";
                table.repaint();
            }
        });

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.add(enrollBtn);

        panel.add(UIHelper.heading("All Available Courses"), BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    // ── QUIZZES TAB ──
    private JPanel buildQuizzesTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        List<Quiz> quizzes = quizDAO.getQuizzesForStudent(student.getId());
        String[] cols = {"#", "Quiz Title", "Course"};
        Object[][] data = new Object[quizzes.size()][3];
        for (int i = 0; i < quizzes.size(); i++) {
            data[i][0] = i + 1;
            data[i][1] = quizzes.get(i).getTitle();
            data[i][2] = quizzes.get(i).getCourseTitle();
        }

        JTable table = UIHelper.styledTable(cols, data);
        JScrollPane scroll = new JScrollPane(table);

        JButton attemptBtn = UIHelper.primaryButton("Attempt Selected Quiz");
        attemptBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { UIHelper.showError(panel, "Select a quiz to attempt."); return; }
            Quiz selected = quizzes.get(row);
            new QuizAttemptDialog(this, student, selected, quizDAO, resultDAO).setVisible(true);
        });

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.add(attemptBtn);

        panel.add(UIHelper.heading("Available Quizzes"), BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    // ── RESULTS TAB ──
    private JPanel buildResultsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        List<Result> results = resultDAO.getResultsByStudent(student.getId());
        String[] cols = {"#", "Quiz", "Course", "Score", "Total", "Percentage"};
        Object[][] data = new Object[results.size()][6];
        for (int i = 0; i < results.size(); i++) {
            Result r = results.get(i);
            data[i][0] = i + 1;
            data[i][1] = r.getQuizTitle();
            data[i][2] = r.getCourseTitle();
            data[i][3] = r.getScore();
            data[i][4] = r.getTotal();
            data[i][5] = String.format("%.1f%%", r.getPercentage());
        }

        JTable table = UIHelper.styledTable(cols, data);
        panel.add(UIHelper.heading("My Quiz Results"), BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    // ── PROGRESS TAB ──
    private JPanel buildProgressTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        List<Progress> progressList = resultDAO.getProgressByStudent(student.getId());

        JPanel progressPanel = new JPanel();
        progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.Y_AXIS));
        progressPanel.setBackground(UIHelper.WHITE);

        for (Progress p : progressList) {
            JPanel row = new JPanel(new BorderLayout(10, 5));
            row.setBorder(new EmptyBorder(8, 10, 8, 10));
            row.setBackground(UIHelper.WHITE);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

            JLabel courseLabel = UIHelper.label(p.getCourseTitle());
            JProgressBar bar = new JProgressBar(0, 100);
            bar.setValue((int) p.getPercentage());
            bar.setStringPainted(true);
            bar.setString(String.format("%.1f%%", p.getPercentage()));
            bar.setForeground(UIHelper.SUCCESS);
            bar.setPreferredSize(new Dimension(400, 20));

            row.add(courseLabel, BorderLayout.WEST);
            row.add(bar, BorderLayout.CENTER);
            progressPanel.add(row);
            progressPanel.add(new JSeparator());
        }

        if (progressList.isEmpty()) {
            progressPanel.add(UIHelper.label("No progress yet. Enroll in courses and attempt quizzes!"));
        }

        panel.add(UIHelper.heading("Course Progress"), BorderLayout.NORTH);
        panel.add(new JScrollPane(progressPanel), BorderLayout.CENTER);
        return panel;
    }

    // ── CHATBOT TAB ──
    private JPanel buildChatbotTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        chatOutput = new JTextArea();
        chatOutput.setEditable(false);
        chatOutput.setFont(UIHelper.BODY_FONT);
        chatOutput.setLineWrap(true);
        chatOutput.setWrapStyleWord(true);
        chatOutput.setText("🤖 LMS Bot: Hello " + student.getName() + "! How can I help you?\n\n");
        JScrollPane scroll = new JScrollPane(chatOutput);

        chatInput = UIHelper.styledField(40);
        chatInput.setToolTipText("Type your question here...");
        JButton sendBtn = UIHelper.primaryButton("Send");

        sendBtn.addActionListener(e -> sendMessage());
        chatInput.addActionListener(e -> sendMessage());

        JPanel inputPanel = new JPanel(new BorderLayout(8, 0));
        inputPanel.add(chatInput, BorderLayout.CENTER);
        inputPanel.add(sendBtn, BorderLayout.EAST);

        panel.add(UIHelper.heading("🤖 LMS Assistant Chatbot"), BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(inputPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void sendMessage() {
        String msg = chatInput.getText().trim();
        if (msg.isEmpty()) return;
        chatOutput.append("👤 You: " + msg + "\n");
        chatOutput.append("🤖 Bot: " + chatbotDAO.getResponse(msg) + "\n\n");
        chatInput.setText("");
        chatOutput.setCaretPosition(chatOutput.getDocument().getLength());
    }

    private void logout() {
        dispose();
        new LoginFrame().setVisible(true);
    }
}

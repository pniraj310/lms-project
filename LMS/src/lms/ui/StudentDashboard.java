package lms.ui;

import lms.dao.*;
import lms.model.*;
import lms.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * StudentDashboard.java
 * ----------------------
 * Main screen for students after login.
 * Tabs: My Courses | All Courses | My Quizzes | My Results | Progress | Chatbot
 */
public class StudentDashboard extends JFrame {

    private User         student;
    private CourseDAO    courseDAO  = new CourseDAO();
    private QuizDAO      quizDAO    = new QuizDAO();
    private ResultDAO    resultDAO  = new ResultDAO();
    private ChatbotDAO   chatbot    = new ChatbotDAO();

    // ─────────────────────────────────────────────────────────
    public StudentDashboard(User student) {
        this.student = student;
        initUI();
        setTitle("LMS — Student: " + student.getFullName());
        setSize(900, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        UIHelper.centreWindow(this);
        setVisible(true);
    }

    // ─────────────────────────────────────────────────────────
    private void initUI() {
        setLayout(new BorderLayout());

        // ── Top header bar ──
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIHelper.PRIMARY);
        header.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel titleLbl = new JLabel("👤  " + student.getFullName() + "  |  Student Dashboard");
        titleLbl.setFont(UIHelper.FONT_HEADER);
        titleLbl.setForeground(Color.WHITE);

        JButton logoutBtn = UIHelper.dangerButton("Logout");
        logoutBtn.addActionListener(e -> { dispose(); new LoginFrame(); });

        header.add(titleLbl,  BorderLayout.WEST);
        header.add(logoutBtn, BorderLayout.EAST);

        // ── Tabs ──
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UIHelper.FONT_BUTTON);
        tabs.addTab("📖 My Courses",   buildMyCoursesPanel());
        tabs.addTab("🌐 All Courses",  buildAllCoursesPanel());
        tabs.addTab("📝 My Quizzes",   buildQuizPanel());
        tabs.addTab("🏆 My Results",   buildResultsPanel());
        tabs.addTab("📊 Progress",      buildProgressPanel());
        tabs.addTab("🤖 Chatbot",       buildChatbotPanel());

        add(header, BorderLayout.NORTH);
        add(tabs,   BorderLayout.CENTER);
    }

    // ─────────────────────────────────────────────────────────
    // TAB 1 — MY ENROLLED COURSES
    // ─────────────────────────────────────────────────────────
    private JPanel buildMyCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(UIHelper.WHITE);

        String[] cols = {"#", "Course Title", "Description", "Teacher"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        UIHelper.styleTable(table);

        List<Course> courses = courseDAO.getEnrolledCourses(student.getId());
        int i = 1;
        for (Course c : courses) {
            model.addRow(new Object[]{i++, c.getTitle(), c.getDescription(), c.getTeacherName()});
        }

        JLabel lbl = UIHelper.headerLabel("My Enrolled Courses  (" + courses.size() + ")");
        panel.add(lbl, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    // ─────────────────────────────────────────────────────────
    // TAB 2 — ALL AVAILABLE COURSES (with Enroll button)
    // ─────────────────────────────────────────────────────────
    private JPanel buildAllCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(UIHelper.WHITE);

        String[] cols = {"#", "Course Title", "Teacher", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        UIHelper.styleTable(table);

        List<Course> all = courseDAO.getAllCourses();
        for (int i = 0; i < all.size(); i++) {
            Course c  = all.get(i);
            String st = courseDAO.isEnrolled(student.getId(), c.getId()) ? "✅ Enrolled" : "Not Enrolled";
            model.addRow(new Object[]{i + 1, c.getTitle(), c.getTeacherName(), st});
        }

        JButton enrollBtn = UIHelper.successButton("➕  Enroll in Selected Course");
        enrollBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { UIHelper.showWarning(panel, "Please select a course."); return; }
            Course selected = all.get(row);
            if (courseDAO.isEnrolled(student.getId(), selected.getId())) {
                UIHelper.showWarning(panel, "You are already enrolled in: " + selected.getTitle());
                return;
            }
            if (courseDAO.enrollStudent(student.getId(), selected.getId())) {
                UIHelper.showSuccess(panel, "Enrolled in: " + selected.getTitle());
                model.setValueAt("✅ Enrolled", row, 3);
            } else {
                UIHelper.showError(panel, "Enrollment failed. Please try again.");
            }
        });

        JLabel lbl = UIHelper.headerLabel("All Available Courses");
        panel.add(lbl,       BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(enrollBtn, BorderLayout.SOUTH);
        return panel;
    }

    // ─────────────────────────────────────────────────────────
    // TAB 3 — MY QUIZZES (Attempt Quiz)
    // ─────────────────────────────────────────────────────────
    private JPanel buildQuizPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(UIHelper.WHITE);

        String[] cols = {"#", "Quiz Title", "Course", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        UIHelper.styleTable(table);

        List<Quiz> quizzes = quizDAO.getQuizzesForStudent(student.getId());
        for (int i = 0; i < quizzes.size(); i++) {
            Quiz q   = quizzes.get(i);
            String st = resultDAO.hasAttempted(student.getId(), q.getId()) ? "✅ Attempted" : "⬜ Not Attempted";
            model.addRow(new Object[]{i + 1, q.getTitle(), q.getCourseTitle(), st});
        }

        JButton attemptBtn = UIHelper.primaryButton("▶  Attempt Selected Quiz");
        attemptBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { UIHelper.showWarning(panel, "Please select a quiz."); return; }
            Quiz selected = quizzes.get(row);
            if (resultDAO.hasAttempted(student.getId(), selected.getId())) {
                UIHelper.showWarning(panel, "You have already attempted this quiz.");
                return;
            }
            List<Question> questions = quizDAO.getQuestionsByQuiz(selected.getId());
            if (questions.isEmpty()) {
                UIHelper.showWarning(panel, "No questions added to this quiz yet.");
                return;
            }
            new QuizAttemptDialog(this, student, selected, questions, resultDAO);
            // Refresh status after attempt
            boolean done = resultDAO.hasAttempted(student.getId(), selected.getId());
            model.setValueAt(done ? "✅ Attempted" : "⬜ Not Attempted", row, 3);
        });

        panel.add(UIHelper.headerLabel("Available Quizzes"), BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(attemptBtn, BorderLayout.SOUTH);
        return panel;
    }

    // ─────────────────────────────────────────────────────────
    // TAB 4 — MY RESULTS
    // ─────────────────────────────────────────────────────────
    private JPanel buildResultsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(UIHelper.WHITE);

        String[] cols = {"#", "Quiz", "Score", "Total", "Percentage", "Status", "Date"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        UIHelper.styleTable(table);

        List<Result> results = resultDAO.getResultsByStudent(student.getId());
        int i = 1;
        for (Result r : results) {
            model.addRow(new Object[]{
                i++, r.getQuizTitle(), r.getScore(), r.getTotal(),
                r.getPercentage(), r.getStatus(), r.getAttemptedAt()
            });
        }

        panel.add(UIHelper.headerLabel("My Quiz Results  (" + results.size() + " attempts)"), BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    // ─────────────────────────────────────────────────────────
    // TAB 5 — PROGRESS TRACKING
    // ─────────────────────────────────────────────────────────
    private JPanel buildProgressPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(UIHelper.WHITE);

        JPanel cardsPanel = new JPanel();
        cardsPanel.setLayout(new BoxLayout(cardsPanel, BoxLayout.Y_AXIS));
        cardsPanel.setBackground(UIHelper.WHITE);

        List<Progress> progList = resultDAO.getProgressByStudent(student.getId());

        if (progList.isEmpty()) {
            cardsPanel.add(UIHelper.normalLabel("No progress yet. Enroll in courses and attempt quizzes!"));
        } else {
            for (Progress p : progList) {
                cardsPanel.add(buildProgressCard(p));
                cardsPanel.add(Box.createVerticalStrut(12));
            }
        }

        panel.add(UIHelper.headerLabel("Course Progress"), BorderLayout.NORTH);
        panel.add(new JScrollPane(cardsPanel), BorderLayout.CENTER);
        return panel;
    }

    /** Builds a single progress card for one course */
    private JPanel buildProgressCard(Progress p) {
        JPanel card = UIHelper.titledPanel(p.getCourseTitle());
        card.setLayout(new BorderLayout(10, 5));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIHelper.PRIMARY, 1),
            new EmptyBorder(10, 15, 10, 15)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue((int) p.getCompletionPercent());
        bar.setStringPainted(true);
        bar.setString(String.format("%.1f%%", p.getCompletionPercent()));
        bar.setForeground(p.getCompletionPercent() >= 100 ? UIHelper.SUCCESS : UIHelper.PRIMARY);
        bar.setPreferredSize(new Dimension(600, 25));

        JLabel nameLbl = UIHelper.normalLabel(p.getCourseTitle());
        JLabel pctLbl  = UIHelper.normalLabel(p.getProgressBar());
        pctLbl.setFont(new Font("Monospaced", Font.PLAIN, 13));

        JPanel left = new JPanel(new GridLayout(2, 1));
        left.setBackground(UIHelper.WHITE);
        left.add(nameLbl);
        left.add(pctLbl);

        card.add(left, BorderLayout.WEST);
        card.add(bar,  BorderLayout.CENTER);
        return card;
    }

    // ─────────────────────────────────────────────────────────
    // TAB 6 — CHATBOT
    // ─────────────────────────────────────────────────────────
    private JPanel buildChatbotPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(UIHelper.WHITE);

        // Chat display area
        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(UIHelper.FONT_NORMAL);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setBackground(new Color(245, 248, 250));
        chatArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        chatArea.setText("🤖 LMS Bot: Hello " + student.getFullName() + "! How can I help you today?\n"
                       + "   (Try: 'enroll', 'quiz', 'result', 'progress', 'help', 'bye')\n\n");

        JScrollPane scroll = new JScrollPane(chatArea);

        // Input area
        JTextField inputField = UIHelper.textField(40);
        JButton sendBtn = UIHelper.primaryButton("Send ➤");

        Runnable sendMessage = () -> {
            String msg = inputField.getText().trim();
            if (msg.isEmpty()) return;
            chatArea.append("👤 You: " + msg + "\n");
            String reply = chatbot.getResponse(msg);
            chatArea.append("🤖 Bot: " + reply + "\n\n");
            inputField.setText("");
            // Auto-scroll to bottom
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        };

        sendBtn.addActionListener(e -> sendMessage.run());
        inputField.addActionListener(e -> sendMessage.run());

        JPanel inputPanel = new JPanel(new BorderLayout(8, 0));
        inputPanel.setBackground(UIHelper.WHITE);
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendBtn,    BorderLayout.EAST);

        panel.add(UIHelper.headerLabel("🤖 LMS Chatbot Assistant"), BorderLayout.NORTH);
        panel.add(scroll,       BorderLayout.CENTER);
        panel.add(inputPanel,   BorderLayout.SOUTH);
        return panel;
    }
}

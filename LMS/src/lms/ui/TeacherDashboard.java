package lms.ui;

import lms.dao.*;
import lms.model.*;
import lms.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * TeacherDashboard.java
 * ----------------------
 * Main screen for teachers after login.
 * Tabs: My Courses | Manage Quizzes | Add Questions | View Results
 */
public class TeacherDashboard extends JFrame {

    private User      teacher;
    private CourseDAO courseDAO = new CourseDAO();
    private QuizDAO   quizDAO   = new QuizDAO();
    private ResultDAO resultDAO = new ResultDAO();

    // Shared state — selected course / quiz
    private List<Course> myCourses;
    private List<Quiz>   myQuizzes;

    // ─────────────────────────────────────────────────────────
    public TeacherDashboard(User teacher) {
        this.teacher = teacher;
        initUI();
        setTitle("LMS — Teacher: " + teacher.getFullName());
        setSize(950, 680);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        UIHelper.centreWindow(this);
        setVisible(true);
    }

    // ─────────────────────────────────────────────────────────
    private void initUI() {
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(39, 174, 96));
        header.setBorder(new EmptyBorder(10, 20, 10, 20));
        JLabel title = new JLabel("🎓  " + teacher.getFullName() + "  |  Teacher Dashboard");
        title.setFont(UIHelper.FONT_HEADER);
        title.setForeground(Color.WHITE);
        JButton logout = UIHelper.dangerButton("Logout");
        logout.addActionListener(e -> { dispose(); new LoginFrame(); });
        header.add(title,  BorderLayout.WEST);
        header.add(logout, BorderLayout.EAST);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UIHelper.FONT_BUTTON);
        tabs.addTab("📚 My Courses",     buildCoursesPanel());
        tabs.addTab("➕ Add Course",      buildAddCoursePanel());
        tabs.addTab("📝 Manage Quizzes", buildQuizzesPanel());
        tabs.addTab("❓ Add Questions",   buildAddQuestionPanel());
        tabs.addTab("🏆 Student Results", buildResultsPanel());

        add(header, BorderLayout.NORTH);
        add(tabs,   BorderLayout.CENTER);
    }

    // ─────────────────────────────────────────────────────────
    // TAB 1 — MY COURSES (view + delete)
    // ─────────────────────────────────────────────────────────
    private JPanel buildCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(UIHelper.WHITE);

        String[] cols = {"#", "Title", "Description", "Students Enrolled"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        UIHelper.styleTable(table);

        myCourses = courseDAO.getCoursesByTeacher(teacher.getId());
        refreshCoursesTable(model);

        JButton deleteBtn = UIHelper.dangerButton("🗑  Delete Selected Course");
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { UIHelper.showWarning(panel, "Select a course first."); return; }
            if (!UIHelper.confirm(panel, "Delete course \"" + myCourses.get(row).getTitle() + "\"?\nAll quizzes and results will also be deleted!")) return;
            if (courseDAO.deleteCourse(myCourses.get(row).getId())) {
                myCourses.remove(row);
                model.removeRow(row);
                UIHelper.showSuccess(panel, "Course deleted.");
            }
        });

        panel.add(UIHelper.headerLabel("My Courses  (" + myCourses.size() + ")"), BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(deleteBtn, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshCoursesTable(DefaultTableModel model) {
        model.setRowCount(0);
        for (int i = 0; i < myCourses.size(); i++) {
            Course c = myCourses.get(i);
            model.addRow(new Object[]{
                i + 1, c.getTitle(), c.getDescription(),
                courseDAO.getEnrolledCount(c.getId())
            });
        }
    }

    // ─────────────────────────────────────────────────────────
    // TAB 2 — ADD COURSE FORM
    // ─────────────────────────────────────────────────────────
    private JPanel buildAddCoursePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIHelper.WHITE);
        panel.setBorder(new EmptyBorder(30, 60, 30, 60));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.fill   = GridBagConstraints.HORIZONTAL;
        gbc.gridx  = 0; gbc.weightx = 1;

        JTextField  titleField = UIHelper.textField(30);
        JTextArea   descArea   = UIHelper.textArea(4, 30);
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setPreferredSize(new Dimension(400, 90));

        gbc.gridy = 0; panel.add(UIHelper.headerLabel("Add New Course"), gbc);
        gbc.gridy = 1; panel.add(UIHelper.normalLabel("Course Title:"), gbc);
        gbc.gridy = 2; panel.add(titleField, gbc);
        gbc.gridy = 3; panel.add(UIHelper.normalLabel("Description:"), gbc);
        gbc.gridy = 4; panel.add(descScroll, gbc);

        JButton addBtn = UIHelper.successButton("➕  Add Course");
        addBtn.setPreferredSize(new Dimension(180, 40));
        addBtn.addActionListener(e -> {
            String t = titleField.getText().trim();
            String d = descArea.getText().trim();
            if (t.isEmpty()) { UIHelper.showError(panel, "Course title is required!"); return; }
            Course c = new Course(t, d, teacher.getId());
            if (courseDAO.addCourse(c)) {
                UIHelper.showSuccess(panel, "Course \"" + t + "\" added successfully!");
                titleField.setText(""); descArea.setText("");
                myCourses = courseDAO.getCoursesByTeacher(teacher.getId());
            } else {
                UIHelper.showError(panel, "Failed to add course.");
            }
        });

        gbc.gridy = 5; gbc.insets = new Insets(20, 0, 0, 0);
        panel.add(addBtn, gbc);
        return panel;
    }

    // ─────────────────────────────────────────────────────────
    // TAB 3 — MANAGE QUIZZES (Add + Delete)
    // ─────────────────────────────────────────────────────────
    private JPanel buildQuizzesPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(UIHelper.WHITE);

        // Quiz table
        String[] cols = {"#", "Quiz Title", "Course", "Questions"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        UIHelper.styleTable(table);

        myQuizzes = quizDAO.getQuizzesByTeacher(teacher.getId());
        for (int i = 0; i < myQuizzes.size(); i++) {
            Quiz q = myQuizzes.get(i);
            model.addRow(new Object[]{i + 1, q.getTitle(), q.getCourseTitle(),
                                      quizDAO.countQuestions(q.getId())});
        }

        // ── Add quiz form (inline) ──
        JPanel addForm = UIHelper.titledPanel("Add New Quiz");
        addForm.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 8));

        JTextField quizTitleField = UIHelper.textField(20);
        quizTitleField.setPreferredSize(new Dimension(200, 32));

        // Dropdown for teacher's courses
        JComboBox<String> courseCombo = new JComboBox<>();
        myCourses = courseDAO.getCoursesByTeacher(teacher.getId());
        for (Course c : myCourses) courseCombo.addItem(c.getTitle());
        courseCombo.setFont(UIHelper.FONT_NORMAL);
        courseCombo.setPreferredSize(new Dimension(200, 32));

        JButton addQuizBtn = UIHelper.successButton("Add Quiz");
        addQuizBtn.addActionListener(e -> {
            String qt = quizTitleField.getText().trim();
            int ci    = courseCombo.getSelectedIndex();
            if (qt.isEmpty() || ci < 0) { UIHelper.showError(panel, "Enter quiz title and select course."); return; }
            Quiz newQuiz = new Quiz(myCourses.get(ci).getId(), qt);
            if (quizDAO.addQuiz(newQuiz)) {
                UIHelper.showSuccess(panel, "Quiz added: " + qt);
                quizTitleField.setText("");
                myQuizzes = quizDAO.getQuizzesByTeacher(teacher.getId());
                model.setRowCount(0);
                for (int i = 0; i < myQuizzes.size(); i++) {
                    Quiz q = myQuizzes.get(i);
                    model.addRow(new Object[]{i + 1, q.getTitle(), q.getCourseTitle(),
                                              quizDAO.countQuestions(q.getId())});
                }
            }
        });

        JButton delQuizBtn = UIHelper.dangerButton("Delete Selected");
        delQuizBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { UIHelper.showWarning(panel, "Select a quiz."); return; }
            if (!UIHelper.confirm(panel, "Delete quiz \"" + myQuizzes.get(row).getTitle() + "\"?")) return;
            if (quizDAO.deleteQuiz(myQuizzes.get(row).getId())) {
                myQuizzes.remove(row); model.removeRow(row);
                UIHelper.showSuccess(panel, "Quiz deleted.");
            }
        });

        addForm.add(UIHelper.normalLabel("Title:")); addForm.add(quizTitleField);
        addForm.add(UIHelper.normalLabel("Course:")); addForm.add(courseCombo);
        addForm.add(addQuizBtn); addForm.add(delQuizBtn);

        panel.add(UIHelper.headerLabel("Manage Quizzes"), BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(addForm, BorderLayout.SOUTH);
        return panel;
    }

    // ─────────────────────────────────────────────────────────
    // TAB 4 — ADD QUESTIONS TO QUIZ
    // ─────────────────────────────────────────────────────────
    private JPanel buildAddQuestionPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBorder(new EmptyBorder(15, 30, 15, 30));
        panel.setBackground(UIHelper.WHITE);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UIHelper.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.fill   = GridBagConstraints.HORIZONTAL;
        gbc.gridx  = 0; gbc.weightx = 1;

        // Quiz selector
        JComboBox<String> quizCombo = new JComboBox<>();
        myQuizzes = quizDAO.getQuizzesByTeacher(teacher.getId());
        for (Quiz q : myQuizzes) quizCombo.addItem(q.getTitle() + " [" + q.getCourseTitle() + "]");
        quizCombo.setFont(UIHelper.FONT_NORMAL);

        JTextArea qText = UIHelper.textArea(2, 40);
        JTextField optAf = UIHelper.textField(30), optBf = UIHelper.textField(30);
        JTextField optCf = UIHelper.textField(30), optDf = UIHelper.textField(30);
        JComboBox<String> correctCombo = new JComboBox<>(new String[]{"A", "B", "C", "D"});
        correctCombo.setFont(UIHelper.FONT_NORMAL);

        String[][] rows = {
            {"Select Quiz:", null}, {"Question Text:", null},
            {"Option A:", null},    {"Option B:", null},
            {"Option C:", null},    {"Option D:", null},
            {"Correct Answer:", null}
        };
        JComponent[] fields = {quizCombo, new JScrollPane(qText), optAf, optBf, optCf, optDf, correctCombo};

        for (int i = 0; i < rows.length; i++) {
            gbc.gridy = i * 2;     form.add(UIHelper.normalLabel(rows[i][0]), gbc);
            gbc.gridy = i * 2 + 1; form.add(fields[i], gbc);
        }

        JButton addBtn = UIHelper.successButton("➕  Add Question");
        addBtn.addActionListener(e -> {
            int qi = quizCombo.getSelectedIndex();
            String qt = qText.getText().trim();
            String a = optAf.getText().trim(), b = optBf.getText().trim();
            String c = optCf.getText().trim(), d = optDf.getText().trim();
            char   correct = ((String) correctCombo.getSelectedItem()).charAt(0);

            if (qi < 0 || qt.isEmpty() || a.isEmpty() || b.isEmpty() || c.isEmpty() || d.isEmpty()) {
                UIHelper.showError(panel, "All fields are required!");
                return;
            }
            Question q = new Question(myQuizzes.get(qi).getId(), qt, a, b, c, d, correct);
            if (quizDAO.addQuestion(q)) {
                UIHelper.showSuccess(panel, "Question added successfully!");
                qText.setText(""); optAf.setText(""); optBf.setText(""); optCf.setText(""); optDf.setText("");
            } else {
                UIHelper.showError(panel, "Failed to add question.");
            }
        });

        gbc.gridy = 14; gbc.insets = new Insets(15, 0, 0, 0);
        form.add(addBtn, gbc);

        panel.add(UIHelper.headerLabel("Add Questions to Quiz"), BorderLayout.NORTH);
        panel.add(new JScrollPane(form), BorderLayout.CENTER);
        return panel;
    }

    // ─────────────────────────────────────────────────────────
    // TAB 5 — STUDENT RESULTS
    // ─────────────────────────────────────────────────────────
    private JPanel buildResultsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(UIHelper.WHITE);

        String[] cols = {"#", "Student ID", "Quiz", "Score", "Total", "%", "Status", "Date"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        UIHelper.styleTable(table);

        List<Result> results = resultDAO.getResultsForTeacher(teacher.getId());
        for (int i = 0; i < results.size(); i++) {
            Result r = results.get(i);
            model.addRow(new Object[]{
                i + 1, "Student #" + r.getStudentId(),
                r.getQuizTitle(), r.getScore(), r.getTotal(),
                r.getPercentage(), r.getStatus(), r.getAttemptedAt()
            });
        }

        panel.add(UIHelper.headerLabel("Student Results  (" + results.size() + " records)"), BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }
}

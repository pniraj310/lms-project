package lms.ui;

import lms.dao.*;
import lms.model.*;
import lms.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * TeacherDashboard — Main screen for teachers
 * Keywords: JTabbedPane, CRUD Operations, Event Handling
 */
public class TeacherDashboard extends JFrame {

    private User      teacher;
    private CourseDAO courseDAO = new CourseDAO();
    private QuizDAO   quizDAO   = new QuizDAO();
    private ResultDAO resultDAO = new ResultDAO();

    public TeacherDashboard(User teacher) {
        this.teacher = teacher;
        setTitle("LMS — Teacher: " + teacher.getName());
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        JPanel header = UIHelper.headerPanel("🎓 Teacher Dashboard — " + teacher.getName());
        JButton logoutBtn = UIHelper.dangerButton("Logout");
        logoutBtn.addActionListener(e -> { dispose(); new LoginFrame().setVisible(true); });
        header.add(logoutBtn, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UIHelper.BODY_FONT);
        tabs.addTab("📚 My Courses",    buildMyCoursesTab());
        tabs.addTab("➕ Add Course",    buildAddCourseTab());
        tabs.addTab("📝 Quizzes",       buildQuizzesTab());
        tabs.addTab("❓ Add Questions", buildAddQuestionsTab());
        tabs.addTab("📊 Student Results", buildResultsTab());
        root.add(tabs, BorderLayout.CENTER);
        add(root);
    }

    // ── MY COURSES ──
    private JPanel buildMyCoursesTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        List<Course> courses = courseDAO.getCoursesByTeacher(teacher.getId());
        String[] cols = {"ID", "Course Title", "Description"};
        Object[][] data = new Object[courses.size()][3];
        for (int i = 0; i < courses.size(); i++) {
            data[i][0] = courses.get(i).getId();
            data[i][1] = courses.get(i).getTitle();
            data[i][2] = courses.get(i).getDescription();
        }
        JTable table = UIHelper.styledTable(cols, data);

        JButton deleteBtn = UIHelper.dangerButton("Delete Selected Course");
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { UIHelper.showError(panel, "Select a course to delete."); return; }
            if (UIHelper.confirm(panel, "Delete course: " + courses.get(row).getTitle() + "?")) {
                courseDAO.deleteCourse(courses.get(row).getId());
                UIHelper.showInfo(panel, "Course deleted. Please refresh.");
            }
        });

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.add(deleteBtn);

        panel.add(UIHelper.heading("My Courses (" + courses.size() + ")"), BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    // ── ADD COURSE ──
    private JPanel buildAddCourseTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(30, 40, 30, 40));
        panel.setBackground(UIHelper.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField titleField = UIHelper.styledField(30);
        JTextArea  descArea   = new JTextArea(4, 30);
        descArea.setFont(UIHelper.BODY_FONT);
        descArea.setLineWrap(true);
        descArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(UIHelper.heading("Add New Course"), gbc);

        gbc.gridwidth = 1; gbc.gridy = 1; gbc.gridx = 0;
        panel.add(UIHelper.label("Course Title:"), gbc);
        gbc.gridx = 1; panel.add(titleField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(UIHelper.label("Description:"), gbc);
        gbc.gridx = 1; panel.add(new JScrollPane(descArea), gbc);

        JButton addBtn = UIHelper.successButton("Add Course");
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(addBtn, gbc);

        addBtn.addActionListener(e -> {
            String title = titleField.getText().trim();
            String desc  = descArea.getText().trim();
            if (title.isEmpty()) { UIHelper.showError(panel, "Course title is required."); return; }
            Course c = new Course(0, title, desc, teacher.getId(), teacher.getName());
            if (courseDAO.addCourse(c)) {
                UIHelper.showInfo(panel, "Course added successfully!");
                titleField.setText(""); descArea.setText("");
            } else {
                UIHelper.showError(panel, "Failed to add course.");
            }
        });
        return panel;
    }

    // ── QUIZZES ──
    private JPanel buildQuizzesTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        List<Course> courses = courseDAO.getCoursesByTeacher(teacher.getId());
        JComboBox<Course> courseBox = new JComboBox<>(courses.toArray(new Course[0]));
        courseBox.setFont(UIHelper.BODY_FONT);

        JTextField quizTitleField = UIHelper.styledField(25);
        JButton addQuizBtn = UIHelper.successButton("Add Quiz");

        addQuizBtn.addActionListener(e -> {
            String title = quizTitleField.getText().trim();
            Course selected = (Course) courseBox.getSelectedItem();
            if (title.isEmpty() || selected == null) {
                UIHelper.showError(panel, "Quiz title and course are required."); return;
            }
            Quiz quiz = new Quiz(0, title, selected.getId(), selected.getTitle());
            if (quizDAO.addQuiz(quiz)) {
                UIHelper.showInfo(panel, "Quiz '" + title + "' added to " + selected.getTitle());
                quizTitleField.setText("");
            } else {
                UIHelper.showError(panel, "Failed to add quiz.");
            }
        });

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        form.add(UIHelper.label("Course:")); form.add(courseBox);
        form.add(UIHelper.label("Quiz Title:")); form.add(quizTitleField);
        form.add(addQuizBtn);

        // List existing quizzes
        List<Quiz> quizzes = quizDAO.getQuizzesByTeacher(teacher.getId());
        String[] cols = {"#", "Quiz Title", "Course"};
        Object[][] data = new Object[quizzes.size()][3];
        for (int i = 0; i < quizzes.size(); i++) {
            data[i][0] = i + 1;
            data[i][1] = quizzes.get(i).getTitle();
            data[i][2] = quizzes.get(i).getCourseTitle();
        }
        JTable table = UIHelper.styledTable(cols, data);

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    // ── ADD QUESTIONS ──
    private JPanel buildAddQuestionsTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));
        panel.setBackground(UIHelper.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 5, 7, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        List<Quiz> quizzes = quizDAO.getQuizzesByTeacher(teacher.getId());
        JComboBox<Quiz> quizBox = new JComboBox<>(quizzes.toArray(new Quiz[0]));
        quizBox.setFont(UIHelper.BODY_FONT);

        JTextField qText = UIHelper.styledField(30);
        JTextField optAF = UIHelper.styledField(25);
        JTextField optBF = UIHelper.styledField(25);
        JTextField optCF = UIHelper.styledField(25);
        JTextField optDF = UIHelper.styledField(25);
        JComboBox<String> correctBox = new JComboBox<>(new String[]{"A", "B", "C", "D"});
        correctBox.setFont(UIHelper.BODY_FONT);

        Object[][] rows = {
            {"Select Quiz:", quizBox}, {"Question:", qText},
            {"Option A:", optAF}, {"Option B:", optBF},
            {"Option C:", optCF}, {"Option D:", optDF},
            {"Correct Option:", correctBox}
        };

        gbc.gridwidth = 2; gbc.gridx = 0; gbc.gridy = 0;
        panel.add(UIHelper.heading("Add Question to Quiz"), gbc);

        for (int i = 0; i < rows.length; i++) {
            gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = i + 1;
            panel.add(UIHelper.label((String) rows[i][0]), gbc);
            gbc.gridx = 1;
            panel.add((Component) rows[i][1], gbc);
        }

        JButton addBtn = UIHelper.successButton("Add Question");
        gbc.gridx = 0; gbc.gridy = rows.length + 1; gbc.gridwidth = 2;
        panel.add(addBtn, gbc);

        addBtn.addActionListener(e -> {
            Quiz selectedQuiz = (Quiz) quizBox.getSelectedItem();
            if (selectedQuiz == null || qText.getText().trim().isEmpty()) {
                UIHelper.showError(panel, "Please fill all fields."); return;
            }
            Question q = new Question(0, selectedQuiz.getId(),
                qText.getText().trim(), optAF.getText().trim(),
                optBF.getText().trim(), optCF.getText().trim(), optDF.getText().trim(),
                ((String) correctBox.getSelectedItem()).charAt(0));
            if (quizDAO.addQuestion(q)) {
                UIHelper.showInfo(panel, "Question added successfully!");
                qText.setText(""); optAF.setText(""); optBF.setText("");
                optCF.setText(""); optDF.setText("");
            } else {
                UIHelper.showError(panel, "Failed to add question.");
            }
        });
        return panel;
    }

    // ── STUDENT RESULTS ──
    private JPanel buildResultsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        List<Result> results = resultDAO.getResultsByTeacher(teacher.getId());
        String[] cols = {"#", "Student ID", "Quiz", "Course", "Score", "Total", "%"};
        Object[][] data = new Object[results.size()][7];
        for (int i = 0; i < results.size(); i++) {
            Result r = results.get(i);
            data[i][0] = i + 1;
            data[i][1] = r.getStudentId();
            data[i][2] = r.getQuizTitle();
            data[i][3] = r.getCourseTitle();
            data[i][4] = r.getScore();
            data[i][5] = r.getTotal();
            data[i][6] = String.format("%.1f%%", r.getPercentage());
        }

        JTable table = UIHelper.styledTable(cols, data);
        panel.add(UIHelper.heading("Student Results"), BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }
}

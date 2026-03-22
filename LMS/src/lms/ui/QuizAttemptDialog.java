package lms.ui;

import lms.dao.ResultDAO;
import lms.model.Question;
import lms.model.Quiz;
import lms.model.User;
import lms.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * QuizAttemptDialog.java
 * -----------------------
 * Modal dialog that walks the student through each question one by one.
 *
 * Flow:
 *  1. Show question text + 4 radio buttons (A/B/C/D)
 *  2. Student selects answer → clicks "Next"
 *  3. After last question → evaluate → save result → show score
 */
public class QuizAttemptDialog extends JDialog {

    private User           student;
    private Quiz           quiz;
    private List<Question> questions;
    private ResultDAO      resultDAO;

    // State
    private int     currentIndex = 0;   // which question we are on
    private int     score        = 0;   // correct answers count
    private char[]  answers;            // student's answers (indexed by question)

    // UI components
    private JLabel        questionNumLbl;
    private JLabel        questionTextLbl;
    private ButtonGroup   optionGroup;
    private JRadioButton  optA, optB, optC, optD;
    private JButton       nextBtn;
    private JProgressBar  progressBar;

    // ─────────────────────────────────────────────────────────
    public QuizAttemptDialog(Frame parent, User student, Quiz quiz,
                             List<Question> questions, ResultDAO resultDAO) {
        super(parent, "Quiz: " + quiz.getTitle(), true);   // modal
        this.student   = student;
        this.quiz      = quiz;
        this.questions = questions;
        this.resultDAO = resultDAO;
        this.answers   = new char[questions.size()];

        initUI();
        loadQuestion(0);
        setSize(600, 420);
        setResizable(false);
        UIHelper.centreWindow(this);
        setVisible(true);
    }

    // ─────────────────────────────────────────────────────────
    private void initUI() {
        setLayout(new BorderLayout(0, 0));

        // ── Top: quiz title + progress ──
        JPanel topPanel = new JPanel(new GridLayout(3, 1));
        topPanel.setBackground(UIHelper.PRIMARY);
        topPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel quizTitle = new JLabel("📝  " + quiz.getTitle() + "  [" + quiz.getCourseTitle() + "]");
        quizTitle.setFont(UIHelper.FONT_HEADER);
        quizTitle.setForeground(Color.WHITE);

        questionNumLbl = new JLabel("Question 1 of " + questions.size());
        questionNumLbl.setFont(UIHelper.FONT_SMALL);
        questionNumLbl.setForeground(new Color(200, 230, 255));

        progressBar = new JProgressBar(0, questions.size());
        progressBar.setValue(0);
        progressBar.setForeground(UIHelper.SUCCESS);
        progressBar.setBackground(new Color(50, 100, 150));

        topPanel.add(quizTitle);
        topPanel.add(questionNumLbl);
        topPanel.add(progressBar);

        // ── Centre: question + options ──
        JPanel centrePanel = new JPanel();
        centrePanel.setLayout(new BoxLayout(centrePanel, BoxLayout.Y_AXIS));
        centrePanel.setBackground(UIHelper.WHITE);
        centrePanel.setBorder(new EmptyBorder(20, 30, 10, 30));

        questionTextLbl = new JLabel();
        questionTextLbl.setFont(UIHelper.FONT_HEADER);
        questionTextLbl.setForeground(UIHelper.DARK_TEXT);

        // Wrap long question text
        JPanel questionPanel = new JPanel(new BorderLayout());
        questionPanel.setBackground(UIHelper.WHITE);
        questionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        questionPanel.add(questionTextLbl, BorderLayout.CENTER);

        // Radio buttons for 4 options
        optA = new JRadioButton(); optA.setFont(UIHelper.FONT_NORMAL); optA.setBackground(UIHelper.WHITE);
        optB = new JRadioButton(); optB.setFont(UIHelper.FONT_NORMAL); optB.setBackground(UIHelper.WHITE);
        optC = new JRadioButton(); optC.setFont(UIHelper.FONT_NORMAL); optC.setBackground(UIHelper.WHITE);
        optD = new JRadioButton(); optD.setFont(UIHelper.FONT_NORMAL); optD.setBackground(UIHelper.WHITE);

        optionGroup = new ButtonGroup();
        optionGroup.add(optA);
        optionGroup.add(optB);
        optionGroup.add(optC);
        optionGroup.add(optD);

        centrePanel.add(questionPanel);
        centrePanel.add(Box.createVerticalStrut(15));
        centrePanel.add(optA);
        centrePanel.add(Box.createVerticalStrut(8));
        centrePanel.add(optB);
        centrePanel.add(Box.createVerticalStrut(8));
        centrePanel.add(optC);
        centrePanel.add(Box.createVerticalStrut(8));
        centrePanel.add(optD);

        // ── Bottom: Next / Submit button ──
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(UIHelper.LIGHT_BG);
        bottomPanel.setBorder(new EmptyBorder(8, 20, 8, 20));

        nextBtn = UIHelper.primaryButton("Next ▶");
        nextBtn.setPreferredSize(new Dimension(140, 38));
        nextBtn.addActionListener(e -> handleNext());

        bottomPanel.add(nextBtn);

        add(topPanel,    BorderLayout.NORTH);
        add(centrePanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // ─────────────────────────────────────────────────────────
    // Load question at given index into the UI
    // ─────────────────────────────────────────────────────────
    private void loadQuestion(int index) {
        Question q = questions.get(index);
        questionNumLbl.setText("Question " + (index + 1) + " of " + questions.size());
        progressBar.setValue(index);

        // Use HTML for wrapping long text
        questionTextLbl.setText("<html><b>Q" + (index + 1) + ".</b> " + q.getQuestionText() + "</html>");

        optA.setText("A)  " + q.getOptionA());
        optB.setText("B)  " + q.getOptionB());
        optC.setText("C)  " + q.getOptionC());
        optD.setText("D)  " + q.getOptionD());

        // Clear previous selection
        optionGroup.clearSelection();

        // Change button label on last question
        nextBtn.setText(index == questions.size() - 1 ? "Submit ✅" : "Next ▶");
    }

    // ─────────────────────────────────────────────────────────
    // Handle "Next" / "Submit" click
    // ─────────────────────────────────────────────────────────
    private void handleNext() {
        // Determine which option student selected
        char selected = getSelectedAnswer();
        if (selected == 0) {
            UIHelper.showWarning(this, "Please select an answer before proceeding.");
            return;
        }

        // Store student's answer
        answers[currentIndex] = selected;

        // Check if correct
        Question q = questions.get(currentIndex);
        if (q.isCorrect(selected)) score++;

        currentIndex++;

        if (currentIndex < questions.size()) {
            // Load next question
            loadQuestion(currentIndex);
        } else {
            // All questions answered → save result and show score
            finishQuiz();
        }
    }

    // ─────────────────────────────────────────────────────────
    // Get which radio button is selected (returns A/B/C/D or 0)
    // ─────────────────────────────────────────────────────────
    private char getSelectedAnswer() {
        if (optA.isSelected()) return 'A';
        if (optB.isSelected()) return 'B';
        if (optC.isSelected()) return 'C';
        if (optD.isSelected()) return 'D';
        return 0;
    }

    // ─────────────────────────────────────────────────────────
    // Save result to DB and show final score dialog
    // ─────────────────────────────────────────────────────────
    private void finishQuiz() {
        int total = questions.size();
        resultDAO.saveResult(student.getId(), quiz.getId(), score, total);

        // Calculate percentage
        double pct    = (score * 100.0 / total);
        String status = (pct >= 50) ? "🎉 PASS" : "😞 FAIL";
        String colour = (pct >= 50) ? "#27ae60" : "#c0392b";

        String message = String.format(
            "<html><center>"
            + "<h2>Quiz Completed!</h2>"
            + "<h3 style='color:%s;'>%s</h3>"
            + "<p>Score: <b>%d / %d</b></p>"
            + "<p>Percentage: <b>%.1f%%</b></p>"
            + "<p>Progress updated automatically.</p>"
            + "</center></html>",
            colour, status, score, total, pct
        );

        JOptionPane.showMessageDialog(this, new JLabel(message),
                                      "Quiz Result", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
}

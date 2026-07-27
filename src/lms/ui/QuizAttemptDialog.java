package lms.ui;

import lms.dao.QuizDAO;
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
 * QuizAttemptDialog — Modal dialog for quiz attempts
 * Keywords: JDialog, Modal, Auto-evaluation, JProgressBar
 */
public class QuizAttemptDialog extends JDialog {

    private User       student;
    private Quiz       quiz;
    private QuizDAO    quizDAO;
    private ResultDAO  resultDAO;

    private List<Question>    questions;
    private int               currentIndex = 0;
    private int               score        = 0;
    private char              selectedAnswer = ' ';

    // UI Components
    private JLabel       questionLabel;
    private JLabel       questionCounter;
    private JProgressBar progressBar;
    private JRadioButton optA, optB, optC, optD;
    private ButtonGroup  btnGroup;
    private JButton      nextBtn;

    public QuizAttemptDialog(JFrame parent, User student, Quiz quiz, QuizDAO quizDAO, ResultDAO resultDAO) {
        super(parent, "Quiz: " + quiz.getTitle(), true); // modal = true
        this.student   = student;
        this.quiz      = quiz;
        this.quizDAO   = quizDAO;
        this.resultDAO = resultDAO;
        this.questions = quizDAO.getQuestionsByQuiz(quiz.getId());

        if (questions.isEmpty()) {
            UIHelper.showError(parent, "No questions found for this quiz.");
            dispose();
            return;
        }

        setSize(600, 420);
        setLocationRelativeTo(parent);
        setResizable(false);
        buildUI();
        loadQuestion(0);
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        questionCounter = UIHelper.label("Question 1 of " + questions.size());
        progressBar = new JProgressBar(0, questions.size());
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setForeground(UIHelper.PRIMARY);
        header.add(questionCounter, BorderLayout.WEST);
        header.add(progressBar, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        // Question
        questionLabel = new JLabel("<html><body style='width:500px'></body></html>");
        questionLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        questionLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
        root.add(questionLabel, BorderLayout.CENTER);

        // Options
        optA = new JRadioButton(); optA.setFont(UIHelper.BODY_FONT);
        optB = new JRadioButton(); optB.setFont(UIHelper.BODY_FONT);
        optC = new JRadioButton(); optC.setFont(UIHelper.BODY_FONT);
        optD = new JRadioButton(); optD.setFont(UIHelper.BODY_FONT);

        btnGroup = new ButtonGroup();
        btnGroup.add(optA); btnGroup.add(optB);
        btnGroup.add(optC); btnGroup.add(optD);

        JPanel optionsPanel = new JPanel(new GridLayout(4, 1, 5, 8));
        optionsPanel.add(optA); optionsPanel.add(optB);
        optionsPanel.add(optC); optionsPanel.add(optD);
        root.add(optionsPanel, BorderLayout.SOUTH);

        // Next/Submit button
        nextBtn = UIHelper.primaryButton("Next →");
        nextBtn.addActionListener(e -> handleNext());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(nextBtn);
        root.add(btnPanel, BorderLayout.EAST);

        // Wrap in layout
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(UIHelper.headerPanel("📝 " + quiz.getTitle()), BorderLayout.NORTH);
        wrapper.add(root, BorderLayout.CENTER);
        wrapper.add(btnPanel, BorderLayout.SOUTH);
        add(wrapper);
    }

    private void loadQuestion(int index) {
        if (index >= questions.size()) { submitQuiz(); return; }

        Question q = questions.get(index);
        questionCounter.setText("Question " + (index + 1) + " of " + questions.size());
        progressBar.setValue(index);
        questionLabel.setText("<html><body style='width:500px'><b>Q" + (index + 1) + ". " +
                q.getQuestionText() + "</b></body></html>");

        optA.setText("A)  " + q.getOptionA());
        optB.setText("B)  " + q.getOptionB());
        optC.setText("C)  " + q.getOptionC());
        optD.setText("D)  " + q.getOptionD());
        btnGroup.clearSelection();

        nextBtn.setText(index == questions.size() - 1 ? "Submit ✓" : "Next →");
    }

    private void handleNext() {
        // Get selected answer
        if      (optA.isSelected()) selectedAnswer = 'A';
        else if (optB.isSelected()) selectedAnswer = 'B';
        else if (optC.isSelected()) selectedAnswer = 'C';
        else if (optD.isSelected()) selectedAnswer = 'D';
        else {
            UIHelper.showError(this, "Please select an answer before proceeding.");
            return;
        }

        // Auto-evaluate using Question.isCorrect()
        if (questions.get(currentIndex).isCorrect(selectedAnswer)) {
            score++;
        }

        currentIndex++;
        loadQuestion(currentIndex);
    }

    private void submitQuiz() {
        progressBar.setValue(questions.size());
        resultDAO.saveResult(student.getId(), quiz.getId(), score, questions.size());

        double percentage = ((double) score / questions.size()) * 100;
        String grade = percentage >= 80 ? "Excellent! 🏆" :
                       percentage >= 60 ? "Good! 👍" :
                       percentage >= 40 ? "Average 📖" : "Needs Improvement 💪";

        UIHelper.showInfo(this,
            "Quiz Completed!\n\n" +
            "Quiz: " + quiz.getTitle() + "\n" +
            "Score: " + score + " / " + questions.size() + "\n" +
            "Percentage: " + String.format("%.1f", percentage) + "%\n" +
            "Grade: " + grade
        );
        dispose();
    }
}

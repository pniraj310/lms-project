/**
 * Question Model for LMS
 * Represents a quiz question in a course
 */
public class Question {
    private int questionId;
    private int courseId;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctOption; // A, B, C, or D

    public Question(int questionId, int courseId, String questionText, 
                   String optionA, String optionB, String optionC, String optionD, String correctOption) {
        this.questionId = questionId;
        this.courseId = courseId;
        this.questionText = questionText;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctOption = correctOption;
    }

    public Question(int courseId, String questionText, 
                   String optionA, String optionB, String optionC, String optionD, String correctOption) {
        this.courseId = courseId;
        this.questionText = questionText;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctOption = correctOption;
    }

    // Getters and Setters
    public int getQuestionId() { return questionId; }
    public void setQuestionId(int questionId) { this.questionId = questionId; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public String getOptionA() { return optionA; }
    public void setOptionA(String optionA) { this.optionA = optionA; }

    public String getOptionB() { return optionB; }
    public void setOptionB(String optionB) { this.optionB = optionB; }

    public String getOptionC() { return optionC; }
    public void setOptionC(String optionC) { this.optionC = optionC; }

    public String getOptionD() { return optionD; }
    public void setOptionD(String optionD) { this.optionD = optionD; }

    public String getCorrectOption() { return correctOption; }
    public void setCorrectOption(String correctOption) { this.correctOption = correctOption; }

    @Override
    public String toString() {
        return "Question{" +
                "questionId=" + questionId +
                ", courseId=" + courseId +
                ", questionText='" + questionText + '\'' +
                '}';
    }
}

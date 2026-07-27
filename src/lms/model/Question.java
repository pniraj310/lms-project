package lms.model;

/**
 * Question Model — MCQ
 * Keywords: Encapsulation, POJO
 */
public class Question {
    private int    id;
    private int    quizId;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private char   correctOption;

    public Question() {}

    public Question(int id, int quizId, String questionText,
                    String optA, String optB, String optC, String optD, char correct) {
        this.id            = id;
        this.quizId        = quizId;
        this.questionText  = questionText;
        this.optionA       = optA;
        this.optionB       = optB;
        this.optionC       = optC;
        this.optionD       = optD;
        this.correctOption = correct;
    }

    public int    getId()            { return id; }
    public int    getQuizId()        { return quizId; }
    public String getQuestionText()  { return questionText; }
    public String getOptionA()       { return optionA; }
    public String getOptionB()       { return optionB; }
    public String getOptionC()       { return optionC; }
    public String getOptionD()       { return optionD; }
    public char   getCorrectOption() { return correctOption; }

    public void setId(int id)                     { this.id = id; }
    public void setQuizId(int q)                  { this.quizId = q; }
    public void setQuestionText(String t)         { this.questionText = t; }
    public void setOptionA(String a)              { this.optionA = a; }
    public void setOptionB(String b)              { this.optionB = b; }
    public void setOptionC(String c)              { this.optionC = c; }
    public void setOptionD(String d)              { this.optionD = d; }
    public void setCorrectOption(char c)          { this.correctOption = c; }

    /**
     * Checks if student's answer is correct.
     * Used in QuizAttemptDialog for auto-evaluation.
     */
    public boolean isCorrect(char studentAnswer) {
        return Character.toUpperCase(studentAnswer) == Character.toUpperCase(correctOption);
    }
}

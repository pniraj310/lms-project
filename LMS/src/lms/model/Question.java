package lms.model;

/**
 * Question.java
 * --------------
 * Represents a multiple-choice question in a quiz.
 */
public class Question {

    private int    id;
    private int    quizId;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private char   correctOption;   // 'A', 'B', 'C', or 'D'

    public Question() {}

    public Question(int id, int quizId, String questionText,
                    String a, String b, String c, String d, char correct) {
        this.id            = id;
        this.quizId        = quizId;
        this.questionText  = questionText;
        this.optionA       = a;
        this.optionB       = b;
        this.optionC       = c;
        this.optionD       = d;
        this.correctOption = correct;
    }

    public Question(int quizId, String questionText,
                    String a, String b, String c, String d, char correct) {
        this.quizId        = quizId;
        this.questionText  = questionText;
        this.optionA       = a;
        this.optionB       = b;
        this.optionC       = c;
        this.optionD       = d;
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

    public void setId(int id)                        { this.id            = id; }
    public void setQuizId(int qid)                   { this.quizId        = qid; }
    public void setQuestionText(String qt)           { this.questionText  = qt; }
    public void setOptionA(String a)                 { this.optionA       = a; }
    public void setOptionB(String b)                 { this.optionB       = b; }
    public void setOptionC(String c)                 { this.optionC       = c; }
    public void setOptionD(String d)                 { this.optionD       = d; }
    public void setCorrectOption(char co)            { this.correctOption = co; }

    /**
     * Checks if the given answer is correct (case-insensitive).
     * @param answer single character 'A', 'B', 'C', or 'D'
     * @return true if correct
     */
    public boolean isCorrect(char answer) {
        return Character.toUpperCase(answer) == Character.toUpperCase(correctOption);
    }

    @Override
    public String toString() { return questionText; }
}

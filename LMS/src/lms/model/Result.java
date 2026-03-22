package lms.model;

/**
 * Result.java
 * ------------
 * Represents a quiz attempt result for a student.
 */
public class Result {

    private int    id;
    private int    studentId;
    private int    quizId;
    private int    score;
    private int    total;
    private String quizTitle;       // for display
    private String attemptedAt;     // timestamp as string

    public Result() {}

    public Result(int id, int studentId, int quizId, int score, int total,
                  String quizTitle, String attemptedAt) {
        this.id          = id;
        this.studentId   = studentId;
        this.quizId      = quizId;
        this.score       = score;
        this.total       = total;
        this.quizTitle   = quizTitle;
        this.attemptedAt = attemptedAt;
    }

    public int    getId()          { return id; }
    public int    getStudentId()   { return studentId; }
    public int    getQuizId()      { return quizId; }
    public int    getScore()       { return score; }
    public int    getTotal()       { return total; }
    public String getQuizTitle()   { return quizTitle; }
    public String getAttemptedAt() { return attemptedAt; }

    public void setId(int id)                    { this.id          = id; }
    public void setStudentId(int sid)            { this.studentId   = sid; }
    public void setQuizId(int qid)               { this.quizId      = qid; }
    public void setScore(int score)              { this.score       = score; }
    public void setTotal(int total)              { this.total       = total; }
    public void setQuizTitle(String qt)          { this.quizTitle   = qt; }
    public void setAttemptedAt(String at)        { this.attemptedAt = at; }

    /** Returns score as a percentage string */
    public String getPercentage() {
        if (total == 0) return "0%";
        return String.format("%.1f%%", (score * 100.0 / total));
    }

    /** Returns pass/fail based on 50% threshold */
    public String getStatus() {
        if (total == 0) return "N/A";
        return (score * 100.0 / total >= 50) ? "PASS" : "FAIL";
    }
}

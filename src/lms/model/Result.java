package lms.model;

/** Result Model — stores quiz attempt scores */
public class Result {
    private int    id;
    private int    studentId;
    private int    quizId;
    private String quizTitle;
    private String courseTitle;
    private int    score;
    private int    total;

    public Result() {}

    public Result(int id, int studentId, int quizId, String quizTitle, String courseTitle, int score, int total) {
        this.id          = id;
        this.studentId   = studentId;
        this.quizId      = quizId;
        this.quizTitle   = quizTitle;
        this.courseTitle = courseTitle;
        this.score       = score;
        this.total       = total;
    }

    public int    getId()          { return id; }
    public int    getStudentId()   { return studentId; }
    public int    getQuizId()      { return quizId; }
    public String getQuizTitle()   { return quizTitle; }
    public String getCourseTitle() { return courseTitle; }
    public int    getScore()       { return score; }
    public int    getTotal()       { return total; }

    public void setId(int id)               { this.id = id; }
    public void setStudentId(int s)         { this.studentId = s; }
    public void setQuizId(int q)            { this.quizId = q; }
    public void setQuizTitle(String t)      { this.quizTitle = t; }
    public void setCourseTitle(String ct)   { this.courseTitle = ct; }
    public void setScore(int s)             { this.score = s; }
    public void setTotal(int t)             { this.total = t; }

    public double getPercentage() {
        return total == 0 ? 0 : ((double) score / total) * 100;
    }
}

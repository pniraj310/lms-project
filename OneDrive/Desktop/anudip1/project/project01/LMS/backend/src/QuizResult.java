import java.sql.Timestamp;

/**
 * QuizResult Model for LMS
 * Represents quiz attempt results
 */
public class QuizResult {
    private int resultId;
    private int userId;
    private int courseId;
    private int score;
    private int totalQuestions;
    private float percentage;
    private Timestamp attemptDate;

    public QuizResult(int resultId, int userId, int courseId, int score, int totalQuestions, float percentage, Timestamp attemptDate) {
        this.resultId = resultId;
        this.userId = userId;
        this.courseId = courseId;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.percentage = percentage;
        this.attemptDate = attemptDate;
    }

    public QuizResult(int userId, int courseId, int score, int totalQuestions, float percentage) {
        this.userId = userId;
        this.courseId = courseId;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.percentage = percentage;
    }

    // Getters and Setters
    public int getResultId() { return resultId; }
    public void setResultId(int resultId) { this.resultId = resultId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }

    public float getPercentage() { return percentage; }
    public void setPercentage(float percentage) { this.percentage = percentage; }

    public Timestamp getAttemptDate() { return attemptDate; }
    public void setAttemptDate(Timestamp attemptDate) { this.attemptDate = attemptDate; }

    @Override
    public String toString() {
        return "QuizResult{" +
                "resultId=" + resultId +
                ", userId=" + userId +
                ", courseId=" + courseId +
                ", score=" + score +
                '/' + totalQuestions +
                ", percentage=" + percentage + "%" +
                '}';
    }
}

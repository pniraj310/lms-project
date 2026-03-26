import java.time.LocalDate;

/**
 * Enrollment Model for LMS
 * Represents a student's enrollment in a course with progress tracking
 */
public class Enrollment {
    private int enrollmentId;
    private int userId;
    private int courseId;
    private LocalDate enrollmentDate;
    private float progress; // 0.0 to 100.0
    private String status; // Active, Completed, Dropped

    public Enrollment(int enrollmentId, int userId, int courseId, LocalDate enrollmentDate, float progress, String status) {
        this.enrollmentId = enrollmentId;
        this.userId = userId;
        this.courseId = courseId;
        this.enrollmentDate = enrollmentDate;
        this.progress = progress;
        this.status = status;
    }

    public Enrollment(int userId, int courseId, LocalDate enrollmentDate, float progress, String status) {
        this.userId = userId;
        this.courseId = courseId;
        this.enrollmentDate = enrollmentDate;
        this.progress = progress;
        this.status = status;
    }

    public Enrollment(int userId, int courseId, LocalDate enrollmentDate, String status) {
        this.userId = userId;
        this.courseId = courseId;
        this.enrollmentDate = enrollmentDate;
        this.progress = 0.0f;
        this.status = status;
    }

    // Getters and Setters
    public int getEnrollmentId() { return enrollmentId; }
    public void setEnrollmentId(int enrollmentId) { this.enrollmentId = enrollmentId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public LocalDate getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(LocalDate enrollmentDate) { this.enrollmentDate = enrollmentDate; }

    public float getProgress() { return progress; }
    public void setProgress(float progress) { this.progress = progress; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Enrollment{" +
                "enrollmentId=" + enrollmentId +
                ", userId=" + userId +
                ", courseId=" + courseId +
                ", enrollmentDate=" + enrollmentDate +
                ", progress=" + progress + "%" +
                ", status='" + status + '\'' +
                '}';
    }
}

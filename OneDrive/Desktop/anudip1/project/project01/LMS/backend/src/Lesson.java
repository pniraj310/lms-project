/**
 * Lesson Model for LMS
 * Represents a lesson within a course
 */
public class Lesson {
    private int lessonId;
    private int courseId;
    private String lessonTitle;
    private String lessonContent;
    private int lessonOrder;

    public Lesson(int lessonId, int courseId, String lessonTitle, String lessonContent, int lessonOrder) {
        this.lessonId = lessonId;
        this.courseId = courseId;
        this.lessonTitle = lessonTitle;
        this.lessonContent = lessonContent;
        this.lessonOrder = lessonOrder;
    }

    public Lesson(int courseId, String lessonTitle, String lessonContent, int lessonOrder) {
        this.courseId = courseId;
        this.lessonTitle = lessonTitle;
        this.lessonContent = lessonContent;
        this.lessonOrder = lessonOrder;
    }

    // Getters and Setters
    public int getLessonId() { return lessonId; }
    public void setLessonId(int lessonId) { this.lessonId = lessonId; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public String getLessonTitle() { return lessonTitle; }
    public void setLessonTitle(String lessonTitle) { this.lessonTitle = lessonTitle; }

    public String getLessonContent() { return lessonContent; }
    public void setLessonContent(String lessonContent) { this.lessonContent = lessonContent; }

    public int getLessonOrder() { return lessonOrder; }
    public void setLessonOrder(int lessonOrder) { this.lessonOrder = lessonOrder; }

    @Override
    public String toString() {
        return "Lesson{" +
                "lessonId=" + lessonId +
                ", courseId=" + courseId +
                ", lessonTitle='" + lessonTitle + '\'' +
                ", lessonOrder=" + lessonOrder +
                '}';
    }
}

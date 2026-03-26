/**
 * Course Model for LMS
 * Represents a course with instructor and details
 */
public class Course {
    private int courseId;
    private String courseName;
    private String description;
    private int instructorId;
    private int credits;

    public Course(int courseId, String courseName, String description, int instructorId, int credits) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.description = description;
        this.instructorId = instructorId;
        this.credits = credits;
    }

    public Course(String courseName, String description, int instructorId, int credits) {
        this.courseName = courseName;
        this.description = description;
        this.instructorId = instructorId;
        this.credits = credits;
    }

    // Getters and Setters
    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getInstructorId() { return instructorId; }
    public void setInstructorId(int instructorId) { this.instructorId = instructorId; }

    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }

    @Override
    public String toString() {
        return "Course{" +
                "courseId=" + courseId +
                ", courseName='" + courseName + '\'' +
                ", instructorId=" + instructorId +
                ", credits=" + credits +
                '}';
    }
}

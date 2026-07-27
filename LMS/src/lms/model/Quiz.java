package lms.model;

/**
 * Quiz.java
 * ----------
 * Represents a quiz belonging to a course.
 */
public class Quiz {

    private int    id;
    private int    courseId;
    private String title;
    private String courseTitle;   // for display purposes

    public Quiz() {}

    public Quiz(int id, int courseId, String title, String courseTitle) {
        this.id          = id;
        this.courseId    = courseId;
        this.title       = title;
        this.courseTitle = courseTitle;
    }

    public Quiz(int courseId, String title) {
        this.courseId = courseId;
        this.title    = title;
    }

    public int    getId()          { return id; }
    public int    getCourseId()    { return courseId; }
    public String getTitle()       { return title; }
    public String getCourseTitle() { return courseTitle; }

    public void setId(int id)                { this.id          = id; }
    public void setCourseId(int cid)         { this.courseId    = cid; }
    public void setTitle(String title)       { this.title       = title; }
    public void setCourseTitle(String ct)    { this.courseTitle = ct; }

    @Override
    public String toString() { return title + " [" + courseTitle + "]"; }
}

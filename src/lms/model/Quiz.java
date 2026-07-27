package lms.model;

/** Quiz Model */
public class Quiz {
    private int    id;
    private String title;
    private int    courseId;
    private String courseTitle;

    public Quiz() {}
    public Quiz(int id, String title, int courseId, String courseTitle) {
        this.id          = id;
        this.title       = title;
        this.courseId    = courseId;
        this.courseTitle = courseTitle;
    }

    public int    getId()          { return id; }
    public String getTitle()       { return title; }
    public int    getCourseId()    { return courseId; }
    public String getCourseTitle() { return courseTitle; }

    public void setId(int id)                { this.id = id; }
    public void setTitle(String t)           { this.title = t; }
    public void setCourseId(int c)           { this.courseId = c; }
    public void setCourseTitle(String ct)    { this.courseTitle = ct; }

    @Override public String toString() { return title + " (" + courseTitle + ")"; }
}

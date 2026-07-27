package lms.model;

/** Progress Model — tracks student course completion */
public class Progress {
    private int    studentId;
    private int    courseId;
    private String courseTitle;
    private double percentage;

    public Progress() {}

    public Progress(int studentId, int courseId, String courseTitle, double percentage) {
        this.studentId   = studentId;
        this.courseId    = courseId;
        this.courseTitle = courseTitle;
        this.percentage  = percentage;
    }

    public int    getStudentId()   { return studentId; }
    public int    getCourseId()    { return courseId; }
    public String getCourseTitle() { return courseTitle; }
    public double getPercentage()  { return percentage; }

    public void setStudentId(int s)         { this.studentId = s; }
    public void setCourseId(int c)          { this.courseId = c; }
    public void setCourseTitle(String t)    { this.courseTitle = t; }
    public void setPercentage(double p)     { this.percentage = p; }
}

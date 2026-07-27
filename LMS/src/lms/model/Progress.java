package lms.model;

/**
 * Progress.java
 * --------------
 * Tracks a student's completion percentage for a course.
 */
public class Progress {

    private int    id;
    private int    studentId;
    private int    courseId;
    private double completionPercent;
    private String courseTitle;       // for display
    private String lastUpdated;

    public Progress() {}

    public Progress(int id, int studentId, int courseId,
                    double completionPercent, String courseTitle, String lastUpdated) {
        this.id                = id;
        this.studentId         = studentId;
        this.courseId          = courseId;
        this.completionPercent = completionPercent;
        this.courseTitle       = courseTitle;
        this.lastUpdated       = lastUpdated;
    }

    public int    getId()                  { return id; }
    public int    getStudentId()           { return studentId; }
    public int    getCourseId()            { return courseId; }
    public double getCompletionPercent()   { return completionPercent; }
    public String getCourseTitle()         { return courseTitle; }
    public String getLastUpdated()         { return lastUpdated; }

    public void setId(int id)                          { this.id                = id; }
    public void setStudentId(int sid)                  { this.studentId         = sid; }
    public void setCourseId(int cid)                   { this.courseId          = cid; }
    public void setCompletionPercent(double cp)        { this.completionPercent = cp; }
    public void setCourseTitle(String ct)              { this.courseTitle       = ct; }
    public void setLastUpdated(String lu)              { this.lastUpdated       = lu; }

    public String getProgressBar() {
        int filled = (int)(completionPercent / 10);
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < 10; i++) sb.append(i < filled ? "█" : "░");
        sb.append("] ").append(String.format("%.1f%%", completionPercent));
        return sb.toString();
    }
}

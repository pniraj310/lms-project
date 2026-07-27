package lms.model;

/**
 * Course Model
 * Keywords: Encapsulation, POJO, Model Layer
 */
public class Course {
    private int    id;
    private String title;
    private String description;
    private int    teacherId;
    private String teacherName; // joined from users table

    public Course() {}

    public Course(int id, String title, String description, int teacherId, String teacherName) {
        this.id          = id;
        this.title       = title;
        this.description = description;
        this.teacherId   = teacherId;
        this.teacherName = teacherName;
    }

    public int    getId()          { return id; }
    public String getTitle()       { return title; }
    public String getDescription() { return description; }
    public int    getTeacherId()   { return teacherId; }
    public String getTeacherName() { return teacherName; }

    public void setId(int id)                   { this.id = id; }
    public void setTitle(String title)          { this.title = title; }
    public void setDescription(String desc)     { this.description = desc; }
    public void setTeacherId(int tid)           { this.teacherId = tid; }
    public void setTeacherName(String tname)    { this.teacherName = tname; }

    @Override
    public String toString() { return title; }
}

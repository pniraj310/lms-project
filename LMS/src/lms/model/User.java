package lms.model;

/**
 * User.java
 * ----------
 * Model class representing a user in the LMS.
 * Follows OOP encapsulation: private fields + public getters/setters.
 */
public class User {

    private int    id;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String role;        // "student", "teacher", or "admin"

    // ── Constructors ──────────────────────────────────────────

    /** Default constructor */
    public User() {}

    /** Full constructor (used when loading from DB) */
    public User(int id, String username, String password, String fullName, String email, String role) {
        this.id       = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email    = email;
        this.role     = role;
    }

    /** Constructor for registration (no id yet) */
    public User(String username, String password, String fullName, String email, String role) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email    = email;
        this.role     = role;
    }

    // ── Getters ───────────────────────────────────────────────

    public int    getId()       { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getFullName() { return fullName; }
    public String getEmail()    { return email; }
    public String getRole()     { return role; }

    // ── Setters ───────────────────────────────────────────────

    public void setId(int id)             { this.id       = id; }
    public void setUsername(String u)     { this.username = u; }
    public void setPassword(String p)     { this.password = p; }
    public void setFullName(String f)     { this.fullName = f; }
    public void setEmail(String e)        { this.email    = e; }
    public void setRole(String r)         { this.role     = r; }

    // ── Helper ────────────────────────────────────────────────

    public boolean isTeacher() { return "teacher".equalsIgnoreCase(role); }
    public boolean isStudent() { return "student".equalsIgnoreCase(role); }
    public boolean isAdmin()   { return "admin".equalsIgnoreCase(role); }

    @Override
    public String toString() {
        return fullName + " (" + role + ")";
    }
}

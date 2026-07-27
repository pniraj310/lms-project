package lms.model;

/**
 * User Model — Plain Java Object (POJO)
 * Represents a user in the LMS system.
 * Keywords: Encapsulation, POJO, Model Layer
 */
public class User {
    private int id;
    private String name;
    private String username;
    private String email;
    private String password;
    private String role; // student | teacher | admin

    public User() {}

    public User(int id, String name, String username, String email, String password, String role) {
        this.id       = id;
        this.name     = name;
        this.username = username;
        this.email    = email;
        this.password = password;
        this.role     = role;
    }

    // Getters
    public int    getId()       { return id; }
    public String getName()     { return name; }
    public String getUsername() { return username; }
    public String getEmail()    { return email; }
    public String getPassword() { return password; }
    public String getRole()     { return role; }

    // Setters
    public void setId(int id)             { this.id = id; }
    public void setName(String name)      { this.name = name; }
    public void setUsername(String u)     { this.username = u; }
    public void setEmail(String email)    { this.email = email; }
    public void setPassword(String pass)  { this.password = pass; }
    public void setRole(String role)      { this.role = role; }

    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + name + "', role='" + role + "'}";
    }
}

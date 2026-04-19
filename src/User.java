import java.time.LocalDateTime;

public class User {

    private String        id;
    private String        username;
    private String        email;
    private String        role;
    private boolean       active;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;

    public User(String id, String username, String email) {
        this.id        = id;
        this.username  = username;
        this.email     = email;
        this.role      = "USER";
        this.active    = true;
        this.createdAt = LocalDateTime.now();
    }

    public String getId()           { return id; }
    public String getUsername()     { return username; }
    public String getEmail()        { return email; }
    public String getRole()         { return role; }
    public boolean isActive()       { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getLastLogin() { return lastLogin; }

    public void setEmail(String email)      { this.email = email; }
    public void setRole(String role)        { this.role  = role; }
    public void setActive(boolean active)   { this.active = active; }
    public void setLastLogin(LocalDateTime t) { this.lastLogin = t; }

    public boolean isAdmin() { return "ADMIN".equals(role); }

    public void promote() {
        this.role = "ADMIN";
        System.out.println(username + " promoted to ADMIN");
    }

    public void deactivate() {
        this.active = false;
        System.out.println(username + " deactivated");
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", username=" + username
             + ", email=" + email + ", role=" + role
             + ", active=" + active + "}";
    }
}

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AuthService {

    private Map<String, String> userCredentials = new HashMap<>();
    private Map<String, String> activeSessions  = new HashMap<>();

    public String register(String username, String password) {
        if (username == null || username.isBlank())
            throw new IllegalArgumentException("Username cannot be blank");
        if (password == null || password.length() < 8)
            throw new IllegalArgumentException("Password must be at least 8 characters");
        if (userCredentials.containsKey(username))
            throw new IllegalStateException("User already exists: " + username);
        userCredentials.put(username, hashPassword(password));
        System.out.println("Registered user: " + username);
        return username;
    }

    public String login(String username, String password) {
        if (!userCredentials.containsKey(username))
            throw new IllegalArgumentException("User not found: " + username);
        String hashed = hashPassword(password);
        if (!hashed.equals(userCredentials.get(username)))
            throw new SecurityException("Invalid credentials for user: " + username);
        String token = UUID.randomUUID().toString();
        activeSessions.put(token, username);
        System.out.println("Login successful for: " + username);
        return token;
    }

    public boolean validateToken(String token) {
        return activeSessions.containsKey(token);
    }

    public void logout(String token) {
        String user = activeSessions.remove(token);
        if (user != null)
            System.out.println("Logged out: " + user);
    }

    public boolean changePassword(String username, String oldPassword, String newPassword) {
        if (!userCredentials.containsKey(username)) return false;
        if (!hashPassword(oldPassword).equals(userCredentials.get(username))) return false;
        if (newPassword == null || newPassword.length() < 8) return false;
        userCredentials.put(username, hashPassword(newPassword));
        return true;
    }

    public boolean deleteUser(String username) {
        if (!userCredentials.containsKey(username)) return false;
        userCredentials.remove(username);
        activeSessions.entrySet().removeIf(e -> e.getValue().equals(username));
        return true;
    }

    public int getActiveSessionCount() {
        return activeSessions.size();
    }

    public int getRegisteredUserCount() {
        return userCredentials.size();
    }

    private String hashPassword(String password) {
        int hash = 17;
        for (char c : password.toCharArray())
            hash = hash * 31 + c;
        return Integer.toHexString(hash);
    }
}

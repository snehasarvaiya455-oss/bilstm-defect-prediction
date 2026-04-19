import java.util.ArrayList;
import java.util.List;

public class UserManager {

    private List<String> users = new ArrayList<>();

    public void addUser(String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        users.add(username);
        System.out.println("User added: " + username);
    }

    public boolean removeUser(String username) {
        return users.remove(username);
    }

    public boolean userExists(String username) {
        return users.contains(username);
    }

    public int getUserCount() {
        return users.size();
    }

    public List<String> getAllUsers() {
        return new ArrayList<>(users);
    }
}

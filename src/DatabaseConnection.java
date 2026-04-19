import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseConnection {

    private static DatabaseConnection instance;
    private final Map<String, Map<String, Object>> tables = new HashMap<>();
    private boolean connected = false;
    private String host;
    private int    port;
    private String dbName;

    private DatabaseConnection() {}

    public static DatabaseConnection getInstance() {
        if (instance == null)
            instance = new DatabaseConnection();
        return instance;
    }

    public void connect(String host, int port, String dbName) {
        this.host    = host;
        this.port    = port;
        this.dbName  = dbName;
        this.connected = true;
        System.out.println("Connected to " + host + ":" + port + "/" + dbName);
    }

    public void disconnect() {
        this.connected = false;
        System.out.println("Disconnected from database");
    }

    public boolean isConnected() {
        return connected;
    }

    public void insert(String table, String id, Map<String, Object> record) {
        checkConnection();
        tables.computeIfAbsent(table, k -> new HashMap<>()).put(id, record);
    }

    public Map<String, Object> findById(String table, String id) {
        checkConnection();
        Map<String, Object> t = tables.get(table);
        return t != null ? t.get(id) : null;
    }

    public List<Map<String, Object>> findAll(String table) {
        checkConnection();
        Map<String, Object> t = tables.get(table);
        if (t == null) return new ArrayList<>();
        return new ArrayList<>(t.values());
    }

    public boolean delete(String table, String id) {
        checkConnection();
        Map<String, Object> t = tables.get(table);
        if (t == null) return false;
        return t.remove(id) != null;
    }

    public int count(String table) {
        Map<String, Object> t = tables.get(table);
        return t == null ? 0 : t.size();
    }

    private void checkConnection() {
        if (!connected)
            throw new IllegalStateException("Database is not connected");
    }
}

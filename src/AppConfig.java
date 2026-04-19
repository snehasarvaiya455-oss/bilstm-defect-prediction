import java.util.HashMap;
import java.util.Map;

public class AppConfig {

    private static AppConfig             instance;
    private final Map<String, String>    config = new HashMap<>();

    private AppConfig() {
        config.put("app.name",         "BiLSTM Defect Prediction Demo");
        config.put("app.version",      "1.0.0");
        config.put("db.host",          "localhost");
        config.put("db.port",          "5432");
        config.put("db.name",          "defectdb");
        config.put("server.port",      "8080");
        config.put("server.timeout",   "30");
        config.put("auth.token.ttl",   "3600");
        config.put("email.smtp.host",  "smtp.example.com");
        config.put("email.smtp.port",  "587");
        config.put("log.level",        "INFO");
        config.put("cache.enabled",    "true");
        config.put("cache.ttl",        "300");
    }

    public static AppConfig getInstance() {
        if (instance == null) instance = new AppConfig();
        return instance;
    }

    public String get(String key) {
        return config.getOrDefault(key, "");
    }

    public int getInt(String key, int defaultValue) {
        try { return Integer.parseInt(config.get(key)); }
        catch (Exception e) { return defaultValue; }
    }

    public boolean getBoolean(String key) {
        return "true".equalsIgnoreCase(config.get(key));
    }

    public void set(String key, String value) {
        config.put(key, value);
    }

    public boolean has(String key) {
        return config.containsKey(key);
    }
}

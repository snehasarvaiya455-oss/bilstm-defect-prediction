import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Logger {

    public enum Level { DEBUG, INFO, WARN, ERROR }

    private static Logger        instance;
    private final List<String>   logs     = new ArrayList<>();
    private Level                minLevel = Level.INFO;
    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private Logger() {}

    public static Logger getInstance() {
        if (instance == null) instance = new Logger();
        return instance;
    }

    public void setLevel(Level level) { this.minLevel = level; }

    public void debug(String message) { log(Level.DEBUG, message); }
    public void info(String message)  { log(Level.INFO,  message); }
    public void warn(String message)  { log(Level.WARN,  message); }
    public void error(String message) { log(Level.ERROR, message); }

    private void log(Level level, String message) {
        if (level.ordinal() < minLevel.ordinal()) return;
        String entry = "[" + LocalDateTime.now().format(FMT) + "] "
                     + "[" + level + "] " + message;
        logs.add(entry);
        System.out.println(entry);
    }

    public List<String> getLogs()             { return new ArrayList<>(logs); }
    public List<String> getLogs(Level level)  {
        List<String> result = new ArrayList<>();
        for (String l : logs)
            if (l.contains("[" + level + "]")) result.add(l);
        return result;
    }
    public void clearLogs() { logs.clear(); }
    public int  getLogCount() { return logs.size(); }
}

import java.util.HashMap;
import java.util.Map;

public class ApiHandler {

    private final Map<String, RouteHandler> routes = new HashMap<>();
    private int requestCount = 0;

    @FunctionalInterface
    public interface RouteHandler {
        String handle(Map<String, String> params);
    }

    public void registerRoute(String method, String path, RouteHandler handler) {
        routes.put(method.toUpperCase() + ":" + path, handler);
        System.out.println("Registered route: " + method + " " + path);
    }

    public String dispatch(String method, String path, Map<String, String> params) {
        requestCount++;
        String key = method.toUpperCase() + ":" + path;
        RouteHandler handler = routes.get(key);
        if (handler == null)
            return "{\"error\": \"Route not found\", \"status\": 404}";
        try {
            return handler.handle(params != null ? params : new HashMap<>());
        } catch (Exception e) {
            return "{\"error\": \"" + e.getMessage() + "\", \"status\": 500}";
        }
    }

    public int getRequestCount() {
        return requestCount;
    }

    public int getRouteCount() {
        return routes.size();
    }

    public boolean hasRoute(String method, String path) {
        return routes.containsKey(method.toUpperCase() + ":" + path);
    }

    public void clearRoutes() {
        routes.clear();
        System.out.println("All routes cleared");
    }
}

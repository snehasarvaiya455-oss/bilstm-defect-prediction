import java.util.LinkedHashMap;
import java.util.Map;

public class ResponseBuilder {

    private int                    statusCode = 200;
    private final Map<String, String> headers = new LinkedHashMap<>();
    private String                 body       = "";
    private String                 contentType = "application/json";

    public ResponseBuilder status(int code) {
        this.statusCode = code;
        return this;
    }

    public ResponseBuilder header(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public ResponseBuilder body(String body) {
        this.body = body;
        return this;
    }

    public ResponseBuilder contentType(String type) {
        this.contentType = type;
        return this;
    }

    public ResponseBuilder ok(String data) {
        this.statusCode  = 200;
        this.body        = "{\"status\":\"ok\",\"data\":" + data + "}";
        return this;
    }

    public ResponseBuilder error(int code, String message) {
        this.statusCode = code;
        this.body       = "{\"status\":\"error\",\"message\":\"" + message + "\"}";
        return this;
    }

    public String build() {
        StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.1 ").append(statusCode).append("\n");
        sb.append("Content-Type: ").append(contentType).append("\n");
        for (Map.Entry<String, String> h : headers.entrySet())
            sb.append(h.getKey()).append(": ").append(h.getValue()).append("\n");
        sb.append("\n").append(body);
        return sb.toString();
    }

    public int getStatusCode()  { return statusCode; }
    public String getBody()     { return body; }
}

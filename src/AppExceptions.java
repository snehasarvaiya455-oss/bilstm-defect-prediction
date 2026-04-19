public class AppExceptions {

    public static class AuthException extends RuntimeException {
        private final int code;
        public AuthException(String message, int code) {
            super(message);
            this.code = code;
        }
        public int getCode() { return code; }
    }

    public static class DatabaseException extends RuntimeException {
        public DatabaseException(String message) { super(message); }
        public DatabaseException(String message, Throwable cause) { super(message, cause); }
    }

    public static class ValidationException extends RuntimeException {
        private final String field;
        public ValidationException(String field, String message) {
            super(message);
            this.field = field;
        }
        public String getField() { return field; }
    }

    public static class NotFoundException extends RuntimeException {
        private final String resource;
        public NotFoundException(String resource, String id) {
            super(resource + " not found: " + id);
            this.resource = resource;
        }
        public String getResource() { return resource; }
    }

    public static class ServiceUnavailableException extends RuntimeException {
        public ServiceUnavailableException(String service) {
            super("Service unavailable: " + service);
        }
    }
}

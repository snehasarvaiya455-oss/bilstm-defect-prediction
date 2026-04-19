import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TokenManager {

    private static final long TOKEN_TTL_SECONDS = 3600;

    private final Map<String, TokenEntry> tokens = new HashMap<>();

    public String issueToken(String userId) {
        String token = UUID.randomUUID().toString().replace("-", "");
        tokens.put(token, new TokenEntry(userId, Instant.now().getEpochSecond()));
        return token;
    }

    public boolean isValid(String token) {
        if (!tokens.containsKey(token)) return false;
        TokenEntry entry = tokens.get(token);
        return (Instant.now().getEpochSecond() - entry.issuedAt) < TOKEN_TTL_SECONDS;
    }

    public String getUserId(String token) {
        TokenEntry entry = tokens.get(token);
        return entry != null ? entry.userId : null;
    }

    public void revokeToken(String token) {
        tokens.remove(token);
    }

    public void revokeAllForUser(String userId) {
        tokens.entrySet().removeIf(e -> e.getValue().userId.equals(userId));
    }

    public int countActiveTokens() {
        long now = Instant.now().getEpochSecond();
        return (int) tokens.values().stream()
            .filter(e -> (now - e.issuedAt) < TOKEN_TTL_SECONDS)
            .count();
    }

    private static class TokenEntry {
        String userId;
        long   issuedAt;
        TokenEntry(String userId, long issuedAt) {
            this.userId   = userId;
            this.issuedAt = issuedAt;
        }
    }
}

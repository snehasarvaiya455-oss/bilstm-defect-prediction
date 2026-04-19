import java.util.ArrayList;
import java.util.List;

public class EmailService {

    private String        smtpHost;
    private int           smtpPort;
    private String        senderEmail;
    private List<String>  sentLog = new ArrayList<>();

    public EmailService(String smtpHost, int smtpPort, String senderEmail) {
        this.smtpHost    = smtpHost;
        this.smtpPort    = smtpPort;
        this.senderEmail = senderEmail;
    }

    public boolean sendWelcome(String toEmail, String username) {
        String subject = "Welcome to the platform, " + username + "!";
        String body    = "Hi " + username + ",\n\nYour account has been created.\n\nThanks!";
        return send(toEmail, subject, body);
    }

    public boolean sendPasswordReset(String toEmail, String resetToken) {
        String subject = "Password Reset Request";
        String body    = "Use this token to reset your password: " + resetToken
                       + "\n\nThis token expires in 1 hour.";
        return send(toEmail, subject, body);
    }

    public boolean sendOrderConfirmation(String toEmail, String orderId, double amount) {
        String subject = "Order Confirmed: " + orderId;
        String body    = "Your order " + orderId + " has been confirmed.\n"
                       + "Total: $" + String.format("%.2f", amount);
        return send(toEmail, subject, body);
    }

    private boolean send(String to, String subject, String body) {
        String entry = "TO=" + to + " | SUBJECT=" + subject;
        sentLog.add(entry);
        System.out.println("Email sent via " + smtpHost + ":" + smtpPort
                         + " from " + senderEmail + " -> " + to
                         + " | " + subject);
        return true;
    }

    public List<String> getSentLog()   { return new ArrayList<>(sentLog); }
    public int          getSentCount() { return sentLog.size(); }
    public void         clearLog()     { sentLog.clear(); }
}

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ValidationUtils {

    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN =
        Pattern.compile("^\\+?[0-9]{7,15}$");
    private static final Pattern USERNAME_PATTERN =
        Pattern.compile("^[A-Za-z0-9_]{3,20}$");

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean isValidUsername(String username) {
        return username != null && USERNAME_PATTERN.matcher(username).matches();
    }

    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) return false;
        boolean hasUpper = false, hasLower = false, hasDigit = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            if (Character.isLowerCase(c)) hasLower = true;
            if (Character.isDigit(c))     hasDigit = true;
        }
        return hasUpper && hasLower && hasDigit;
    }

    public static boolean isNonEmpty(String value) {
        return value != null && !value.isBlank();
    }

    public static List<String> validateUserInput(String username, String email, String password) {
        List<String> errors = new ArrayList<>();
        if (!isValidUsername(username))
            errors.add("Invalid username: must be 3-20 alphanumeric characters");
        if (!isValidEmail(email))
            errors.add("Invalid email address");
        if (!isValidPassword(password))
            errors.add("Weak password: needs upper, lower, digit, min 8 chars");
        return errors;
    }
}

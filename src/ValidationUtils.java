public class ValidationUtils {

    public boolean validateUser(String username, String password) {
        if (username == null || password == null) {
            return false;
        }

        if (username.length() < 5) {
            return false;
        }

        if (password.length() < 8) {
            return false;
        }

        boolean hasUpper = false;
        boolean hasDigit = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpper = true;
            }

            if (Character.isDigit(c)) {
                hasDigit = true;
            }
        }

        return hasUpper && hasDigit;
    }

    public boolean validateEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }
}

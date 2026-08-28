package shared.auth;

public final class AuthRules {

    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9-]+$";
    private static final String PASSWORD_ALLOWED_PATTERN = "^[a-zA-Z0-9?><,\"';:\\\\/|\\[\\]}{+=()*&^%$#!]+$";
    private static final String LOWERCASE_PATTERN = ".*[a-z].*";
    private static final String UPPERCASE_PATTERN = ".*[A-Z].*";
    private static final String DIGIT_PATTERN = ".*[0-9].*";
    private static final String SPECIAL_PATTERN = ".*[?><,\"';:\\\\/|\\[\\]}{+=()*&^%$#!].*";

    private AuthRules() {
    }

    public static String validateUsername(String username) {
        if (username == null || username.isEmpty()) {
            return "USERNAME_FORMAT";
        }
        if (!username.matches(USERNAME_PATTERN)) {
            return "USERNAME_FORMAT";
        }
        return null;
    }

    public static String validatePassword(String password) {
        if (password == null || password.length() < 8) {
            return "WEAK_PASSWORD_LENGTH";
        }
        if (!password.matches(PASSWORD_ALLOWED_PATTERN)) {
            return "WEAK_PASSWORD_FORMAT";
        }
        if (!password.matches(LOWERCASE_PATTERN)
                || !password.matches(UPPERCASE_PATTERN)
                || !password.matches(DIGIT_PATTERN)
                || !password.matches(SPECIAL_PATTERN)) {
            return "WEAK_PASSWORD_FORMAT";
        }
        return null;
    }

    public static String validatePasswordMatch(String password, String passwordConfirm) {
        if (password == null || !password.equals(passwordConfirm)) {
            return "PASSWORD_MISMATCH";
        }
        return null;
    }

    public static String validateNickname(String nickname) {
        if (nickname == null || nickname.length() < 3 || nickname.length() > 30) {
            return "INVALID_NICKNAME_LENGTH";
        }
        return null;
    }

    public static String validateEmail(String email) {
        if (email == null || email.isEmpty()) {
            return "INVALID_EMAIL";
        }

        String forbidden = "?><,\"';:\\/|[]}{+=()*&^%$#!";
        for (char c : email.toCharArray()) {
            if (forbidden.indexOf(c) >= 0) {
                return "INVALID_EMAIL";
            }
        }

        int atCount = email.length() - email.replace("@", "").length();
        if (atCount != 1) {
            return "INVALID_EMAIL";
        }

        String[] parts = email.split("@", 2);
        String local = parts[0];
        String domain = parts[1];

        if (!isValidEmailLocalPart(local) || !isValidEmailDomain(domain)) {
            return "INVALID_EMAIL";
        }
        return null;
    }

    private static boolean isValidEmailLocalPart(String local) {
        if (local.isEmpty()) {
            return false;
        }
        if (!Character.isLetterOrDigit(local.charAt(0))
                || !Character.isLetterOrDigit(local.charAt(local.length() - 1))) {
            return false;
        }
        if (local.contains("..")) {
            return false;
        }
        return local.matches("^[a-zA-Z0-9._-]+$");
    }

    private static boolean isValidEmailDomain(String domain) {
        if (domain.isEmpty() || !domain.contains(".")) {
            return false;
        }
        if (!Character.isLetterOrDigit(domain.charAt(0))
                || !Character.isLetterOrDigit(domain.charAt(domain.length() - 1))) {
            return false;
        }
        if (domain.contains("..")) {
            return false;
        }
        int lastDot = domain.lastIndexOf('.');
        String tld = domain.substring(lastDot + 1);
        if (tld.length() < 2) {
            return false;
        }
        return domain.matches("^[a-zA-Z0-9.-]+$");
    }

    public static String validateGender(String genderString) {
        if (genderString == null) {
            return "INVALID_GENDER";
        }
        String normalized = genderString.trim().toLowerCase();
        if (!normalized.equals("male") && !normalized.equals("female")) {
            return "INVALID_GENDER";
        }
        return null;
    }

    public static String parseGender(String genderString) {
        return genderString.trim().toLowerCase();
    }
}

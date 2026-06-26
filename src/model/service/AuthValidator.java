package model.service;

import view.messages.ErrorMessages;

public final class AuthValidator {

    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9-]+$";
    private static final String PASSWORD_ALLOWED_PATTERN = "^[a-zA-Z0-9?><,\"';:\\\\/|\\[\\]}{+=()*&^%$#!]+$";
    private static final String LOWERCASE_PATTERN = ".*[a-z].*";
    private static final String UPPERCASE_PATTERN = ".*[A-Z].*";
    private static final String DIGIT_PATTERN = ".*[0-9].*";
    private static final String SPECIAL_PATTERN = ".*[?><,\"';:\\\\/|\\[\\]}{+=()*&^%$#!].*";

    private AuthValidator() {
    }

    public static String validateUsername(String username) {
        if (username == null || username.isEmpty()) {
            return ErrorMessages.USERNAME_FORMAT.getMessage();
        }
        if (!username.matches(USERNAME_PATTERN)) {
            return ErrorMessages.USERNAME_FORMAT.getMessage();
        }
        return null;
    }

    public static String validatePassword(String password) {
        if (password == null || password.length() < 8) {
            return ErrorMessages.WEAK_PASSWORD_LENGTH.getMessage();
        }
        if (!password.matches(PASSWORD_ALLOWED_PATTERN)) {
            return ErrorMessages.WEAK_PASSWORD_FORMAT.getMessage();
        }
        if (!password.matches(LOWERCASE_PATTERN)
                || !password.matches(UPPERCASE_PATTERN)
                || !password.matches(DIGIT_PATTERN)
                || !password.matches(SPECIAL_PATTERN)) {
            return ErrorMessages.WEAK_PASSWORD_FORMAT.getMessage();
        }
        return null;
    }

    public static String validatePasswordMatch(String password, String passwordConfirm) {
        if (password == null || !password.equals(passwordConfirm)) {
            return ErrorMessages.PASSWORD_MISMATCH.getMessage();
        }
        return null;
    }

    public static String validateNickname(String nickname) {
        if (nickname == null || nickname.length() < 3 || nickname.length() > 30) {
            return ErrorMessages.INVALID_NICKNAME_LENGTH.getMessage();
        }
        return null;
    }

    public static String validateEmail(String email) {
        if (email == null || email.isEmpty()) {
            return ErrorMessages.INVALID_EMAIL.getMessage();
        }

        String forbidden = "?><,\"';:\\/|[]}{+=()*&^%$#!";
        for (char c : email.toCharArray()) {
            if (forbidden.indexOf(c) >= 0) {
                return ErrorMessages.INVALID_EMAIL.getMessage();
            }
        }

        int atCount = email.length() - email.replace("@", "").length();
        if (atCount != 1) {
            return ErrorMessages.INVALID_EMAIL.getMessage();
        }

        String[] parts = email.split("@", 2);
        String local = parts[0];
        String domain = parts[1];

        if (!isValidEmailLocalPart(local) || !isValidEmailDomain(domain)) {
            return ErrorMessages.INVALID_EMAIL.getMessage();
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
            return ErrorMessages.INVALID_GENDER.getMessage();
        }
        String normalized = genderString.trim().toLowerCase();
        if (!normalized.equals("male") && !normalized.equals("female")) {
            return ErrorMessages.INVALID_GENDER.getMessage();
        }
        return null;
    }

    public static String parseGender(String genderString) {
        return genderString.trim().toLowerCase();
    }
}

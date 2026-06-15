package view;

public enum LoginError {
    LOGIN_FAILED("The username or password you entered is incorrect."),
    INVALID_SECURITY_ANSWER("Invalid information. Please try again.");
    private final String message;

    LoginError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

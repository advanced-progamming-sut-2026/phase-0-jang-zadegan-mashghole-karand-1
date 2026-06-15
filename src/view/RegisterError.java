package view;

public enum RegisterError {
    USERNAME_TAKEN("username already exists"),
    USERNAME_FORMAT("Username can only contain lowercase letters (a-z)," +
            " uppercase letters (A-Z), numbers (0-9), and hyphen (-)."),
    WEAK_PASSWORD_LENGTH("password must be at least 8 characters"),
    WEAK_PASSWORD_FORMAT("Password must contain at least one lowercase letter (a-z), one uppercase letter (A-Z)," +
            " one number (0-9), and one special character (?  > < , \" ' ; : \\ / | [ ] } { + = ( ) * & ^ % $ # !)"),
    PASSWORD_MISMATCH("passwords do not match"),
    INVALID_NICKNAME_LENGTH("nickname must be between 3 and 30 characters"),
    INVALID_EMAIL("invalid email format"),
    INVALID_GENDER("gender must be male or female");
    private final String message;

    RegisterError(String message) {
        this.message = message;
    }
    public String getMessage(){
        return message;
    }
}

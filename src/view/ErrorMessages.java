package view;

public enum ErrorMessages {
    //meta error
    PLANT_NOT_FOUND("Plant not found."),
    //register error
    USERNAME_TAKEN("username already exists"),
    USERNAME_FORMAT("Username can only contain lowercase letters (a-z)," +
            " uppercase letters (A-Z), numbers (0-9), and hyphen (-)."),
    WEAK_PASSWORD_LENGTH("password must be at least 8 characters"),
    WEAK_PASSWORD_FORMAT("Password must contain at least one lowercase letter (a-z), one uppercase letter (A-Z)," +
            " one number (0-9), and one special character (?  > < , \" ' ; : \\ / | [ ] } { + = ( ) * & ^ % $ # !)"),
    PASSWORD_MISMATCH("passwords do not match"),
    INVALID_NICKNAME_LENGTH("nickname must be between 3 and 30 characters"),
    INVALID_EMAIL("invalid email format"),
    INVALID_GENDER("gender must be male or female"),

    //login error
    LOGIN_FAILED("The username or password you entered is incorrect."),
    INVALID_SECURITY_ANSWER("Invalid information. Please try again."),

    //pick plant error
    PLANT_LOCKED("This plant is locked. Unlock it first."),
    PLANT_ALREADY_ADDED("Plant already exists in your collection."),
    PLANT_NOT_SELECTED("No plant selected to remove."),

    NOT_ENOUGH_GEMS("Not enough gems to boost this plant."),
    CANNOT_BOOST_LOCKED("Cannot boost a locked plant."),
    PLANT_ALREADY_BOOSTED("Plant is already boosted."),

    //profile error
    USERNAME_SAME_AS_CURRENT("New username cannot be the same as the current username."),
    NICKNAME_SAME_AS_CURRENT("New nickname cannot be the same as the current nickname."),
    EMAIL_SAME_AS_CURRENT("New email cannot be the same as the current email."),
    OLD_PASSWORD_INCORRECT("Old password is incorrect."),
    NEW_PASSWORD_SAME_AS_CURRENT("New password cannot be the same as the current password."),

    //collection error
    NOT_ENOUGH_COINS_PURCHASE("Not enough coins to purchase this plant. Required: 2000 coins."),
    PLANT_ALREADY_PURCHASED("Plant already purchased."),

    NOT_ENOUGH_COINS_UPGRADE("Not enough coins to upgrade this plant."),
    NOT_ENOUGH_SEED_PACKETS("Not enough seed packets to upgrade this plant."),
    PLANT_NOT_IN_COLLECTION("Plant not found in your collection."),
    PLANT_AT_MAX_LEVEL("Plant is already at max level.");
    private final String message;

    ErrorMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

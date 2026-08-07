package model.storage.user;

public enum SafetyQuestionType {
    FIRST_PET("What was the name of your first pet?"),
    BIRTH_CITY("What city were you born in?"),
    FAVORITE_PLANT("What is your favorite plant?"),
    CHILDHOOD_NICKNAME("What was your childhood nickname?");

    public final String question;

    SafetyQuestionType(String question) {
        this.question = question;
    }

    public static SafetyQuestionType fromStored(String value) {
        if (value == null || value.isBlank()) {
            return FIRST_PET;
        }
        for (SafetyQuestionType type : values()) {
            if (type.name().equals(value) || type.question.equals(value)) {
                return type;
            }
        }
        return FIRST_PET;
    }
}

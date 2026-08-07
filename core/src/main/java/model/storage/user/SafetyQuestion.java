package model.storage.user;

public class SafetyQuestion {
    public final SafetyQuestionType type;
    public final String answer;

    public SafetyQuestion(SafetyQuestionType type, String answer) {
        this.type = type;
        this.answer = answer;
    }
}

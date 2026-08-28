package shared.dto;

public final class RegisterRequest {
    public String username;
    public String password;
    public String email;
    public String nickname;
    public String gender;
    public String safetyQuestion;
    public String safetyAnswer;

    public RegisterRequest() {
    }

    public RegisterRequest(String username, String password, String email, String nickname,
            String gender, String safetyQuestion, String safetyAnswer) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.nickname = nickname;
        this.gender = gender;
        this.safetyQuestion = safetyQuestion;
        this.safetyAnswer = safetyAnswer;
    }
}

package shared.dto;

public final class ResetPasswordRequest {
    public String username;
    public String email;
    public String safetyAnswer;
    public String newPassword;

    public ResetPasswordRequest() {
    }
}

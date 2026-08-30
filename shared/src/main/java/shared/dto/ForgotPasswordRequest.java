package shared.dto;

public final class ForgotPasswordRequest {
    public String username;
    public String email;

    public ForgotPasswordRequest() {
    }

    public ForgotPasswordRequest(String username, String email) {
        this.username = username;
        this.email = email;
    }
}

package shared.dto;

public final class LoginResponse {
    public boolean ok;
    public String error;
    public String token;
    public UserProfileDto user;

    public static LoginResponse success(String token, UserProfileDto user) {
        LoginResponse r = new LoginResponse();
        r.ok = true;
        r.token = token;
        r.user = user;
        return r;
    }

    public static LoginResponse fail(String error) {
        LoginResponse r = new LoginResponse();
        r.ok = false;
        r.error = error;
        return r;
    }
}

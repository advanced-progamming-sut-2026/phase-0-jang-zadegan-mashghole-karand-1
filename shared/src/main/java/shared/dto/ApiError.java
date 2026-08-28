package shared.dto;

public final class ApiError {
    public boolean ok;
    public String error;

    public ApiError() {
    }

    public ApiError(String error) {
        this.ok = false;
        this.error = error;
    }

    public static ApiError of(String error) {
        return new ApiError(error);
    }
}

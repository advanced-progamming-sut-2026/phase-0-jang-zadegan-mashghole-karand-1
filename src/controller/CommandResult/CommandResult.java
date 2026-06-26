package controller.CommandResult;

public class CommandResult {
    public String message;
    private boolean success;

    public CommandResult(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public CommandResult() {
        success = true;
    }

    public CommandResult(String message) {
        this.message = message;
        success = true;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}

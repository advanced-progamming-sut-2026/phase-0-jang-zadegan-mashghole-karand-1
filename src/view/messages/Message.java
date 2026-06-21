package view.messages;

public interface Message {
    String getMessage();

    String format(Object... args);
}

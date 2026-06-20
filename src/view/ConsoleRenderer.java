package view;

public class ConsoleRenderer implements Renderer {
    @Override
    public void print(String message) {
        System.out.println(message);
    }
}

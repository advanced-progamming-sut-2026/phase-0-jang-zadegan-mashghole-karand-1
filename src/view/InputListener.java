
package view;

import controller.InputHandler;
import java.util.Scanner;

public class InputListener implements Runnable {
    private final InputHandler inputHandler;
    private final Scanner scanner;
    private volatile boolean running = true;
    private final Runnable promptRenderer;
    private Thread thread;

    public InputListener(InputHandler inputHandler, Runnable promptRenderer) {
        this.inputHandler = inputHandler;
        this.scanner = new Scanner(System.in);
        this.promptRenderer = promptRenderer;
    }

    public void start() {
        thread = new Thread(this, "InputListener");
        thread.start();
    }

    public void stop() {
        running = false;
        if (thread != null) {
            thread.interrupt();
        }
    }

    public void run() {
        while (running) {
            try {
                promptRenderer.run();
                String input = scanner.nextLine();
                inputHandler.handleInput(input);
            } catch (Exception e) {
                if (running) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
}
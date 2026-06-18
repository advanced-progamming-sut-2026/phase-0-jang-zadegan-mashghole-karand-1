package view;

import controller.InputHandler;

import java.util.Scanner;

public class InputListener {
    private final Scanner scanner = new Scanner(System.in);
    private final InputHandler inputHandler;

    public InputListener(InputHandler inputHandler) {
        this.inputHandler = inputHandler;
    }

    public void listen() {
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                inputHandler.handleInput(input);
            }
        }
    }
}

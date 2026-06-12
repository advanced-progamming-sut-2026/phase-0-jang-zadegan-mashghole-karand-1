package model.core;

import java.util.Scanner;

public class GameLoop {
    private Scanner scanner;
    private boolean running = true;
    private Runnable onTickHandler;

    public GameLoop() {
        scanner = new Scanner(System.in);
    }

    public void setOnTickHandler(Runnable onTick) {
        this.onTickHandler = onTick;
    }
}
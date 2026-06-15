package model.core;

import java.util.Scanner;

public class GameLoop {
    public static final int TICKS_PER_SECOND = 10;
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
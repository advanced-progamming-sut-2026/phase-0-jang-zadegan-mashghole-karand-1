package view;

import controller.InputHandler;
import java.io.IOException;
import java.io.InputStream;
import view.renderer.Renderer;

public class InputListener implements Runnable {
    private final InputHandler inputHandler;
    private final Renderer renderer;
    private volatile boolean running = true;
    private Thread thread;
    private boolean rawModeEnabled = false;

    public InputListener(InputHandler inputHandler, Renderer renderer) {
        this.inputHandler = inputHandler;
        this.renderer = renderer;
    }

    public void start() {
        enableRawMode();
        thread = new Thread(this, "InputListener");
        thread.start();
    }

    public void stop() {
        running = false;
        if (thread != null) {
            thread.interrupt();
        }
        disableRawMode();
    }

    @Override
    public void run() {
        while (running) {
            try {
                String input = readLine();
                inputHandler.handleInput(input);
            } catch (IOException e) {
                if (running) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    private String readLine() throws IOException {
        StringBuilder buffer = new StringBuilder();
        InputStream in = System.in;
        renderer.renderCommandPrompt("");

        while (running) {
            int b = in.read();
            if (b < 0) {
                throw new IOException("Input stream closed");
            }

            char c = (char) b;
            if (c == '\n') {
                renderer.renderCommandPrompt("");
                return buffer.toString();
            }
            if (c == '\r') {
                continue;
            }
            if (c == 127 || c == '\b') {
                if (buffer.length() > 0) {
                    buffer.deleteCharAt(buffer.length() - 1);
                    renderer.renderCommandPrompt(buffer.toString());
                }
                continue;
            }
            if (c == 3) {
                System.exit(0);
            }
            if (c == 27) {
                handleEscapeSequence(in);
                continue;
            }
            if (c >= 32) {
                buffer.append(c);
                renderer.renderCommandPrompt(buffer.toString());
            }
        }

        return buffer.toString();
    }

    private void handleEscapeSequence(InputStream in) throws IOException {
        int next = in.read();
        if (next < 0) {
            return;
        }
        if (next != '[') {
            return;
        }
        int code = in.read();
        if (code < 0) {
            return;
        }
        if (code == 'A') {
            inputHandler.handleMessageScroll(1);
        } else if (code == 'B') {
            inputHandler.handleMessageScroll(-1);
        }
    }

    private void enableRawMode() {
        if (configureTerminal(true)) {
            rawModeEnabled = true;
        }
    }

    private void disableRawMode() {
        if (rawModeEnabled) {
            configureTerminal(false);
            rawModeEnabled = false;
        }
    }

    private boolean configureTerminal(boolean raw) {
        try {
            ProcessBuilder builder = raw
                    ? new ProcessBuilder("stty", "-icanon", "-echo", "min", "1", "time", "0")
                    : new ProcessBuilder("stty", "icanon", "echo");
            builder.redirectInput(ProcessBuilder.Redirect.INHERIT);
            builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            builder.redirectError(ProcessBuilder.Redirect.INHERIT);
            return builder.start().waitFor() == 0;
        } catch (Exception e) {
            return false;
        }
    }
}

package system;

import javafx.application.*;

public class Timer extends Thread {
    private final Game game;

    public Timer(Game game) {
        this.game = game;
    }

    public void run() {
        while (!game.isFinished()) {
            Platform.runLater(game::nextSecond);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

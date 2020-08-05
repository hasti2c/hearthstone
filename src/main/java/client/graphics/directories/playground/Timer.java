package client.graphics.directories.playground;

import javafx.application.*;

public class Timer extends Thread {
    private final PlayGround playground;

    public Timer(PlayGround playground) {
        this.playground = playground;
    }

    public void run() {
        while (!playground.getGame().isFinished()) {
            Platform.runLater(playground::nextSecond);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

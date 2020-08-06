package client.graphics.directories.playground;

import javafx.application.*;

public class Timer extends Thread {
    private final PlayGround playground;
    private boolean exit;
    private final Object exitMonitor = new Object();

    public Timer(PlayGround playground) {
        this.playground = playground;
    }

    public void run() {
        synchronized (exitMonitor) {
            exit = false;
        }
        while (!playground.getGame().isFinished()) {
            synchronized (exitMonitor) {
                while (exit) {
                    try {
                        exitMonitor.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            Platform.runLater(playground::nextSecond);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void restart() {
        synchronized (exitMonitor) {
            exit = false;
            exitMonitor.notifyAll();
        }
    }

    public void exit() {
        synchronized (exitMonitor) {
            exit = true;
        }
    }
}

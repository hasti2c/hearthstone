package controllers.game;

import cli.Console;

public class HearthStoneConsole {
    private static GameController game;
    private static Console console;

    public static void main(String[] args) {
        game = GameController.getInstance();

        console = new Console(game);
        console.run();
    }

}

package cli;

import gameObjects.Player.Player;

public interface Printable {
    String[] normalPrint(Player currentPlayer);
    String[][] longPrint(Player currentPlayer);
}

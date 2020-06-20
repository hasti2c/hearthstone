package cli;

import gameObjects.player.Player;

public interface Printable {
    String[] normalPrint(Player currentPlayer);
    String[][] longPrint(Player currentPlayer);
}

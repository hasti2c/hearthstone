package cli;

import gameObjects.*;

public interface Printable {
    String[] normalPrint(Player currentPlayer);
    String[][] longPrint(Player currentPlayer);
}

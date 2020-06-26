package gameObjects.heros;

import java.io.*;
import java.util.*;
import com.google.gson.stream.*;
import controllers.game.GameController;
import directories.*;
import directories.collections.Collections;
import directories.game.PlayGround;
import gameObjects.*;
import cli.*;
import cli.Console;
import javafx.scene.image.Image;

public class Hero implements Printable, Configable {
    private int health = 30;
    private String name;
    private HeroClass heroClass;
    private HeroPower heroPower;
    private Image gameImage;

    @Override
    public void initialize(GameController controller) {
        configGameImage();
    }

    @Override
    public String getJsonPath(GameController controller, String name) {
        return "heros/";
    }

    private void configGameImage() {
        try {
            FileInputStream input = new FileInputStream("src/main/resources/assets/heros/game/" + name + ".png");
            gameImage = new Image(input);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public int getHealth() {
        return health;
    }

    public String toString() {
        return this.name;
    }

    public HeroClass getHeroClass() {
        return this.heroClass;
    }

    public Hero clone() {
        //Hero h = new Hero(player, heroClass);
        Hero h = new Hero();
        h.name = name;
        h.heroClass = heroClass;
        h.health = health;
        h.gameImage = gameImage;
        h.heroPower = heroPower;
        return h;
    }

    public HeroPower getHeroPower() {
        return heroPower;
    }

    public Image getGameImage() {
        if (gameImage == null)
            configGameImage();
        return gameImage;
    }

    public String[] normalPrint(Player currentPlayer) {
        String[] ret = new String[3];
        if (currentPlayer.getCurrentDirectory() instanceof Collections && this == currentPlayer.getCurrentHero()) {
            ret[0] = Console.GREEN;
            ret[2] = Console.RESET;
        }
        ret[1] = toString();
        return ret;
    }

    public String[][] longPrint(Player currentPlayer) {
        String[][] ret = new String[16][3];
        for (int i = 0; i < 16; i++)
            switch (i) {
                case 0:
                    if (currentPlayer.getCurrentDirectory() instanceof Collections && this == currentPlayer.getCurrentHero()) {
                        ret[i][0] = Console.GREEN;
                        ret[i][1] = "current hero";
                        ret[i][2] = Console.RESET;
                    } else
                        ret[i][1] = "";
                    break;
                case 1:
                    ret[i][0] = Console.LIGHT_PINK;
                    ret[i][1] = toString();
                    ret[i][2] = Console.RESET;
                    break;
                case 2:
                    ret[i][1] = "hero";
                    break;
                case 4:
                    ret[i][1] = currentPlayer.getHeroDecks(this).size() + "";
                    break;
                case 7:
                    ret[i][1] = health + "";
            }
        return ret;
    }
}

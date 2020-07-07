package elements.heros;

import elements.Playable;
import elements.cards.Minion;
import elements.cards.Spell;
import system.player.Inventory;
import javafx.scene.image.*;
import java.io.*;

public enum HeroClass {
    NEUTRAL,
    MAGE,
    ROGUE,
    WARLOCK,
    HUNTER,
    PRIEST;

    public Hero getHero(Inventory inventory) {
        for (Hero h : inventory.getAllHeros())
            if (h.toString().equalsIgnoreCase(toString()))
                return h;
        return null;
    }

    public ImageView getIcon() {
        if (this == NEUTRAL)
            return null;
        try {
            FileInputStream input = new FileInputStream("src/main/resources/assets/heros/icons/" + toString().toLowerCase() + ".png");
            ImageView imageView = new ImageView(new Image(input));
            imageView.setPreserveRatio(true);
            imageView.setFitHeight(75);
            return imageView;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getManaReduction(Playable playable) {
        if (manaIsReduced(playable))
            return Math.min(2, playable.getMana());
        return 0;
    }

    private boolean manaIsReduced(Playable playable) {
        return switch (this) {
            case MAGE: yield playable instanceof Spell;
            case ROGUE: yield playable.getHeroClass() != ROGUE && playable.getHeroClass() != NEUTRAL;
            case PRIEST: yield playable instanceof Spell spell && spell.isHealingSpell();
            default: yield false;
        };
    }

    public void doHeroAction(Minion minion) {
        if (this != HUNTER)
            return;
        System.out.println(minion);
        minion.setRush(true);
    }
}

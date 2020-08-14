package elements.abilities.targets;

import system.game.characters.Character;

public interface Attackable extends Targetable {
    boolean getHasAttacked();
    void setHasAttacked(boolean hasAttacked);
    void doDamage(Character character, int damage);
    int getAttack(Character character);
}

package fr.isen.rpg.model;

/**
 * A character made of flesh and blood, which can therefore be healed.
 * Gathers the healing implementation in one place instead of repeating it in every living class.
 */
public abstract class LivingCharacter extends GameCharacter implements Healable {

    protected LivingCharacter(String name, Team team, int maxHp, int attack, int defense, int speed) {
        super(name, team, maxHp, attack, defense, speed);
    }

    @Override
    public int heal(int amount) {
        return restoreHp(amount);
    }
}

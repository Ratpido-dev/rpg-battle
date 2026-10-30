package fr.isen.rpg.model;

import fr.isen.rpg.exception.NotEnoughManaException;

import java.util.Comparator;
import java.util.Optional;

public class Mage extends LivingCharacter {

    public static final int FIREBALL_COST = 20;
    public static final int FIREBALL_MULTIPLIER = 2;
    public static final int MANA_REGEN = 10;

    private final int maxMana;
    private int mana;

    public Mage(String name, Team team, int maxHp, int attack, int defense, int speed, int mana) {
        super(name, team, maxHp, attack, defense, speed);
        if (mana < 0) {
            throw new IllegalArgumentException("mana must not be negative for " + name + ": " + mana);
        }
        this.maxMana = mana;
        this.mana = mana;
    }

    @Override
    protected Optional<ActionReport> act(Battlefield battlefield) {
        // Fireballs go to the toughest enemy, where the big damage is not wasted on a nearly dead target.
        return battlefield.targetableEnemiesOf(this).stream()
                .max(Comparator.comparingInt(GameCharacter::getHp))
                .map(target -> {
                    try {
                        return castFireball(target);
                    } catch (NotEnoughManaException e) {
                        return staffHit(target);
                    }
                });
    }

    public ActionReport castFireball(GameCharacter target) throws NotEnoughManaException {
        ensureAlive();
        if (mana < FIREBALL_COST) {
            throw new NotEnoughManaException(getName(), FIREBALL_COST, mana);
        }
        ActionReport report = hit(target, getAttack() * FIREBALL_MULTIPLIER, ActionType.FIREBALL);
        mana -= FIREBALL_COST;
        return report;
    }

    /** A weak melee attack that lets the mage recover some mana for the next fireball. */
    private ActionReport staffHit(GameCharacter target) {
        mana = Math.min(maxMana, mana + MANA_REGEN);
        return hit(target, getAttack() / 2, ActionType.STAFF_HIT);
    }

    public int getMana() {
        return mana;
    }

    public int getMaxMana() {
        return maxMana;
    }
}

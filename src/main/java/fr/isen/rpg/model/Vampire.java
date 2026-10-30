package fr.isen.rpg.model;

import java.util.Comparator;
import java.util.Optional;

/**
 * An undead monster that feeds on its victims. It is not Healable: nobody can heal it,
 * but it restores its own HP with the damage it deals.
 */
public class Vampire extends GameCharacter {

    /** The vampire recovers 1 HP for every LIFESTEAL_DIVISOR points of damage dealt. */
    public static final int LIFESTEAL_DIVISOR = 2;

    public Vampire(String name, Team team, int maxHp, int attack, int defense, int speed) {
        super(name, team, maxHp, attack, defense, speed);
    }

    @Override
    protected Optional<ActionReport> act(Battlefield battlefield) {
        return battlefield.targetableEnemiesOf(this).stream()
                .min(Comparator.comparingInt(GameCharacter::getHp))
                .map(this::drain);
    }

    private ActionReport drain(GameCharacter target) {
        ActionReport report = hit(target, getAttack(), ActionType.DRAIN);
        int stolen = report.amount() / LIFESTEAL_DIVISOR;
        if (stolen > 0) {
            restoreHp(stolen);
        }
        return report;
    }
}

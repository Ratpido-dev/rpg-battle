package fr.isen.rpg.model;

import java.util.Comparator;
import java.util.Optional;

/**
 * Alternates between two turns: it burrows (and cannot be targeted), then emerges with a
 * devastating attack on the toughest enemy.
 */
public class Sandworm extends LivingCharacter {

    public static final int EMERGE_MULTIPLIER = 3;

    private boolean burrowed;

    public Sandworm(String name, Team team, int maxHp, int attack, int defense, int speed) {
        super(name, team, maxHp, attack, defense, speed);
    }

    @Override
    protected Optional<ActionReport> act(Battlefield battlefield) {
        if (!burrowed) {
            burrowed = true;
            return Optional.of(new ActionReport(this, this, ActionType.BURROW, 0));
        }
        burrowed = false;
        return battlefield.targetableEnemiesOf(this).stream()
                .max(Comparator.comparingInt(GameCharacter::getHp))
                .map(target -> hit(target, getAttack() * EMERGE_MULTIPLIER, ActionType.EMERGE));
    }

    @Override
    public boolean isTargetable() {
        return !burrowed;
    }

    public boolean isBurrowed() {
        return burrowed;
    }
}

package fr.isen.rpg.model;

import java.util.Comparator;
import java.util.Optional;

public class Orc extends LivingCharacter {

    public static final double RAGE_THRESHOLD = 0.5;
    public static final double RAGE_MULTIPLIER = 1.5;

    public Orc(String name, Team team, int maxHp, int attack, int defense, int speed) {
        super(name, team, maxHp, attack, defense, speed);
    }

    @Override
    protected Optional<ActionReport> act(Battlefield battlefield) {
        boolean enraged = isEnraged();
        int damage = enraged ? (int) Math.round(getAttack() * RAGE_MULTIPLIER) : getAttack();
        ActionType type = enraged ? ActionType.RAGE_STRIKE : ActionType.STRIKE;
        // Orcs charge whoever looks the most dangerous.
        return battlefield.targetableEnemiesOf(this).stream()
                .max(Comparator.comparingInt(GameCharacter::getAttack))
                .map(target -> hit(target, damage, type));
    }

    public boolean isEnraged() {
        return getHealthRatio() < RAGE_THRESHOLD;
    }
}

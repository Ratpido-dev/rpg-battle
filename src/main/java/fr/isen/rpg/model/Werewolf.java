package fr.isen.rpg.model;

import java.util.Comparator;
import java.util.Optional;

public class Werewolf extends LivingCharacter {

    public static final double WOUNDED_THRESHOLD = 0.5;
    public static final int MAUL_MULTIPLIER = 2;

    public Werewolf(String name, Team team, int maxHp, int attack, int defense, int speed) {
        super(name, team, maxHp, attack, defense, speed);
    }

    @Override
    protected Optional<ActionReport> act(Battlefield battlefield) {
        // It smells blood: the most wounded enemy (relative to its max HP) is hunted first.
        return battlefield.targetableEnemiesOf(this).stream()
                .min(Comparator.comparingDouble(GameCharacter::getHealthRatio))
                .map(this::attack);
    }

    private ActionReport attack(GameCharacter target) {
        if (target.getHealthRatio() < WOUNDED_THRESHOLD) {
            return hit(target, getAttack() * MAUL_MULTIPLIER, ActionType.MAUL);
        }
        return hit(target, getAttack(), ActionType.STRIKE);
    }
}

package fr.isen.rpg.model;

import java.util.Optional;

/**
 * An undead monster. Extends GameCharacter directly, not LivingCharacter, so it cannot be healed.
 */
public class Skeleton extends GameCharacter {

    public Skeleton(String name, Team team, int maxHp, int attack, int defense, int speed) {
        super(name, team, maxHp, attack, defense, speed);
    }

    @Override
    protected Optional<ActionReport> act(Battlefield battlefield) {
        // Skeletons are mindless: they hit whoever comes first in the enemy line-up.
        return battlefield.targetableEnemiesOf(this).stream()
                .findFirst()
                .map(target -> hit(target, getAttack(), ActionType.STRIKE));
    }
}

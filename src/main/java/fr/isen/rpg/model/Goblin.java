package fr.isen.rpg.model;

import java.util.Comparator;
import java.util.Optional;

public class Goblin extends LivingCharacter {

    public Goblin(String name, Team team, int maxHp, int attack, int defense, int speed) {
        super(name, team, maxHp, attack, defense, speed);
    }

    @Override
    protected Optional<ActionReport> act(Battlefield battlefield) {
        // Goblins are weak cowards: they stab the least protected enemy, where their small attack still hurts.
        return battlefield.targetableEnemiesOf(this).stream()
                .min(Comparator.comparingInt(GameCharacter::getDefense))
                .map(target -> hit(target, getAttack(), ActionType.STAB));
    }
}

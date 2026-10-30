package fr.isen.rpg.model;

import java.util.Comparator;
import java.util.Optional;

public class Warrior extends LivingCharacter {

    public Warrior(String name, Team team, int maxHp, int attack, int defense, int speed) {
        super(name, team, maxHp, attack, defense, speed);
    }

    @Override
    protected Optional<ActionReport> act(Battlefield battlefield) {
        // Finishing off the weakest enemy removes its attacks from the fight as soon as possible.
        return battlefield.targetableEnemiesOf(this).stream()
                .min(Comparator.comparingInt(GameCharacter::getHp))
                .map(target -> hit(target, getAttack(), ActionType.STRIKE));
    }
}

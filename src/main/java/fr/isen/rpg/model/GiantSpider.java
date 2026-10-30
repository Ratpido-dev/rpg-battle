package fr.isen.rpg.model;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Picks a prey and keeps biting it: its venom builds up, so each new bite on the same prey
 * hurts more than the previous one. The venom is lost when the spider changes prey.
 */
public class GiantSpider extends LivingCharacter {

    public static final int VENOM_PER_BITE = 4;

    private GameCharacter prey;
    private int venom;

    public GiantSpider(String name, Team team, int maxHp, int attack, int defense, int speed) {
        super(name, team, maxHp, attack, defense, speed);
    }

    @Override
    protected Optional<ActionReport> act(Battlefield battlefield) {
        List<GameCharacter> enemies = battlefield.targetableEnemiesOf(this);
        if (prey == null || !enemies.contains(prey)) {
            // A new prey: the fastest enemy, the one most likely to escape the web.
            prey = enemies.stream().max(Comparator.comparingInt(GameCharacter::getSpeed)).orElse(null);
            venom = 0;
        }
        if (prey == null) {
            return Optional.empty();
        }
        ActionReport report = hit(prey, getAttack() + venom, ActionType.BITE);
        venom += VENOM_PER_BITE;
        return Optional.of(report);
    }

    public Optional<GameCharacter> getPrey() {
        return Optional.ofNullable(prey);
    }

    public int getVenom() {
        return venom;
    }
}

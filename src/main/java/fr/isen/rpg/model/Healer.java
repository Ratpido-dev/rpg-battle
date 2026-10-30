package fr.isen.rpg.model;

import java.util.Comparator;
import java.util.Optional;

public class Healer extends LivingCharacter {

    public static final int HEAL_MULTIPLIER = 2;

    public Healer(String name, Team team, int maxHp, int attack, int defense, int speed) {
        super(name, team, maxHp, attack, defense, speed);
    }

    @Override
    protected Optional<ActionReport> act(Battlefield battlefield) {
        // A healer cannot heal itself: otherwise a lone healer could out-heal the damage it takes forever.
        // The ratio rather than raw HP, so a fragile mage at 30/70 is treated before a warrior at 60/120.
        Optional<GameCharacter> patient = battlefield.aliveAlliesOf(this).stream()
                .filter(ally -> ally != this)
                .filter(ally -> ally instanceof Healable && ally.isInjured())
                .min(Comparator.comparingDouble(GameCharacter::getHealthRatio));
        if (patient.isPresent()) {
            return Optional.of(treat(patient.get()));
        }
        return battlefield.targetableEnemiesOf(this).stream()
                .min(Comparator.comparingInt(GameCharacter::getHp))
                .map(target -> hit(target, getAttack(), ActionType.STRIKE));
    }

    private ActionReport treat(GameCharacter ally) {
        // Safe cast: act() only selects Healable allies.
        int restored = ((Healable) ally).heal(getHealPower());
        return new ActionReport(this, ally, ActionType.HEAL, restored);
    }

    public int getHealPower() {
        return getAttack() * HEAL_MULTIPLIER;
    }
}

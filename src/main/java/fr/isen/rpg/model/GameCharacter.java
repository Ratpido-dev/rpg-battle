package fr.isen.rpg.model;

import fr.isen.rpg.exception.CharacterDeadException;

import java.util.Objects;
import java.util.Optional;

/**
 * Base class of every fighter. It owns the rules shared by all characters (health, damage
 * reduction, death) while each subclass decides what it does on its turn.
 * Named GameCharacter rather than Character to avoid clashing with {@link java.lang.Character}.
 */
public abstract class GameCharacter {

    /** Even a heavily armoured target loses at least this much HP, so attacks are never useless. */
    public static final int MIN_DAMAGE = 1;

    private final String name;
    private final Team team;
    private final int maxHp;
    private final int attack;
    private final int defense;
    private final int speed;
    private int hp;

    protected GameCharacter(String name, Team team, int maxHp, int attack, int defense, int speed) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name must not be blank");
        }
        if (maxHp <= 0) {
            throw new IllegalArgumentException("HP must be positive for " + name + ": " + maxHp);
        }
        requireNonNegative(attack, "attack", name);
        requireNonNegative(defense, "defense", name);
        requireNonNegative(speed, "speed", name);
        this.name = name;
        this.team = Objects.requireNonNull(team, "Team must not be null");
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
    }

    /**
     * Plays this character's turn. Final so that the "dead characters cannot act" rule cannot be
     * forgotten by a subclass: subclasses only implement {@link #act(Battlefield)}.
     *
     * @return what the character did, or empty if it had nothing to do (no valid target)
     */
    public final Optional<ActionReport> performAction(Battlefield battlefield) {
        ensureAlive();
        return act(battlefield);
    }

    protected abstract Optional<ActionReport> act(Battlefield battlefield);

    /**
     * @return the HP actually lost, never more than the HP the character had left
     */
    public int takeDamage(int rawDamage) {
        ensureAlive();
        if (rawDamage < 0) {
            throw new IllegalArgumentException("Damage must not be negative: " + rawDamage);
        }
        int damage = Math.max(MIN_DAMAGE, rawDamage - defense);
        int lost = Math.min(damage, hp);
        hp -= lost;
        return lost;
    }

    /**
     * Protected: only subclasses that are {@link Healable} expose healing to the outside world.
     *
     * @return the HP actually restored, never above max HP
     */
    protected int restoreHp(int amount) {
        ensureAlive();
        if (amount <= 0) {
            throw new IllegalArgumentException("Heal amount must be positive: " + amount);
        }
        int restored = Math.min(amount, maxHp - hp);
        hp += restored;
        return restored;
    }

    protected ActionReport hit(GameCharacter target, int rawDamage, ActionType type) {
        int dealt = target.takeDamage(rawDamage);
        return new ActionReport(this, target, type, dealt);
    }

    protected void ensureAlive() {
        if (!isAlive()) {
            throw new CharacterDeadException(name);
        }
    }

    public boolean isAlive() {
        return hp > 0;
    }

    /** Whether enemies can choose this character as a target; subclasses may hide temporarily. */
    public boolean isTargetable() {
        return true;
    }

    public boolean isInjured() {
        return hp < maxHp;
    }

    public double getHealthRatio() {
        return (double) hp / maxHp;
    }

    public String getName() {
        return name;
    }

    public Team getTeam() {
        return team;
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public int getSpeed() {
        return speed;
    }

    @Override
    public String toString() {
        return name + " the " + getClass().getSimpleName();
    }

    private static void requireNonNegative(int value, String stat, String name) {
        if (value < 0) {
            throw new IllegalArgumentException(stat + " must not be negative for " + name + ": " + value);
        }
    }
}

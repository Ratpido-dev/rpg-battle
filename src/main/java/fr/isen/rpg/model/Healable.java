package fr.isen.rpg.model;

/**
 * Implemented by characters that can receive healing.
 * It is an interface rather than a method of {@link GameCharacter} because some characters
 * (the undead) must never be healed.
 */
public interface Healable {

    /**
     * @return the HP actually restored, which can be lower than {@code amount} near max HP
     */
    int heal(int amount);
}

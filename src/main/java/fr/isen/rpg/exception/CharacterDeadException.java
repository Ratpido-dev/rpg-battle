package fr.isen.rpg.exception;

/**
 * Thrown when a dead character is asked to act, take damage or be healed.
 * Unchecked: the battle engine never does this on purpose, so it signals a broken rule rather than
 * a situation callers are expected to recover from.
 */
public class CharacterDeadException extends RuntimeException {

    public CharacterDeadException(String characterName) {
        super(characterName + " is dead");
    }
}

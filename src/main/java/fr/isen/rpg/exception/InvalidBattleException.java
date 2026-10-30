package fr.isen.rpg.exception;

/**
 * Thrown when a battle cannot start, for instance when fewer than two teams have living members.
 */
public class InvalidBattleException extends RuntimeException {

    public InvalidBattleException(String message) {
        super(message);
    }
}

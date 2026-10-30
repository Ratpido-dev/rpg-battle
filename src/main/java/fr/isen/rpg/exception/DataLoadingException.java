package fr.isen.rpg.exception;

/**
 * Thrown when the characters cannot be loaded (missing or unreadable file, empty file...).
 */
public class DataLoadingException extends Exception {

    public DataLoadingException(String message) {
        super(message);
    }

    public DataLoadingException(String message, Throwable cause) {
        super(message, cause);
    }
}

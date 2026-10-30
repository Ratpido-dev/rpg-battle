package fr.isen.rpg.exception;

/**
 * Thrown when one line of the data file describes an invalid character.
 * Keeps the line number so the user knows exactly what to fix.
 */
public class InvalidCharacterDataException extends DataLoadingException {

    private final int lineNumber;

    public InvalidCharacterDataException(int lineNumber, String reason) {
        super("Line " + lineNumber + ": " + reason);
        this.lineNumber = lineNumber;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}

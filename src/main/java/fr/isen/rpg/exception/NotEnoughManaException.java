package fr.isen.rpg.exception;

/**
 * Thrown when a spell costs more mana than the caster has left.
 * Checked on purpose: running out of mana is a normal part of a fight, so the compiler forces the
 * caster to decide what to do instead (see {@code Mage#act}).
 */
public class NotEnoughManaException extends Exception {

    private final int required;
    private final int available;

    public NotEnoughManaException(String casterName, int required, int available) {
        super(casterName + " needs " + required + " mana but only has " + available);
        this.required = required;
        this.available = available;
    }

    public int getRequired() {
        return required;
    }

    public int getAvailable() {
        return available;
    }
}

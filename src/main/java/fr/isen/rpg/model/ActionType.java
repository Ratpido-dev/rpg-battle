package fr.isen.rpg.model;

public enum ActionType {
    STRIKE(true),
    FIREBALL(true),
    STAFF_HIT(true),
    HEAL(false),
    RAGE_STRIKE(true),
    DRAIN(true),
    MAUL(true),
    STAB(true),
    BITE(true),
    BURROW(false),
    EMERGE(true);

    private final boolean offensive;

    ActionType(boolean offensive) {
        this.offensive = offensive;
    }

    /** Offensive actions count towards the damage statistics of the battle. */
    public boolean isOffensive() {
        return offensive;
    }
}

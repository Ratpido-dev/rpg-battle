package fr.isen.rpg.service;

import fr.isen.rpg.model.ActionReport;
import fr.isen.rpg.model.GameCharacter;

import java.util.List;

/**
 * Notified while a battle is running. Keeps the battle engine free of any display code:
 * the console view implements it, and tests can pass an empty listener.
 */
public interface BattleListener {

    default void onRoundStart(int round) {
    }

    default void onAction(ActionReport report) {
    }

    /**
     * Also called for the last round, which may stop early when a team is wiped out.
     *
     * @param characters every character of the battle, dead ones included
     */
    default void onRoundEnd(int round, List<GameCharacter> characters) {
    }
}

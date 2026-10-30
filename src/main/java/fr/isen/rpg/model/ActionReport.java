package fr.isen.rpg.model;

/**
 * What happened during one character's turn.
 *
 * @param amount damage actually dealt or HP actually restored (after defense and caps)
 */
public record ActionReport(GameCharacter actor, GameCharacter target, ActionType type, int amount) {
}

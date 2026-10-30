package fr.isen.rpg.loader;

import fr.isen.rpg.model.CharacterClass;
import fr.isen.rpg.model.Team;

/**
 * Raw description of a character, independent of the file format it was read from.
 *
 * @param mana only used by mages, ignored for the other classes
 */
public record CharacterData(String name, CharacterClass characterClass, Team team,
                            int hp, int attack, int defense, int speed, int mana) {
}

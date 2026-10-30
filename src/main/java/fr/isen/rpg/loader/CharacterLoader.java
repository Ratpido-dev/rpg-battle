package fr.isen.rpg.loader;

import fr.isen.rpg.exception.DataLoadingException;
import fr.isen.rpg.model.GameCharacter;

import java.util.List;

/**
 * Source of the characters taking part in a battle. The application depends on this abstraction
 * only, so another format (JSON, database...) can be added without touching the battle code.
 */
public interface CharacterLoader {

    List<GameCharacter> load() throws DataLoadingException;
}

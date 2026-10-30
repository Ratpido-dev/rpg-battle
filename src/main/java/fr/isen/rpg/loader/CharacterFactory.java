package fr.isen.rpg.loader;

import fr.isen.rpg.model.GameCharacter;
import fr.isen.rpg.model.GiantSpider;
import fr.isen.rpg.model.Goblin;
import fr.isen.rpg.model.Healer;
import fr.isen.rpg.model.Mage;
import fr.isen.rpg.model.Orc;
import fr.isen.rpg.model.Sandworm;
import fr.isen.rpg.model.Skeleton;
import fr.isen.rpg.model.Vampire;
import fr.isen.rpg.model.Warrior;
import fr.isen.rpg.model.Werewolf;

/**
 * The single place that knows which concrete class matches each {@link fr.isen.rpg.model.CharacterClass}.
 */
public final class CharacterFactory {

    private CharacterFactory() {
    }

    /**
     * @throws IllegalArgumentException if the stats are invalid for a character
     */
    public static GameCharacter create(CharacterData data) {
        // No default branch: adding a new CharacterClass makes this switch fail to compile until it is handled.
        return switch (data.characterClass()) {
            case WARRIOR -> new Warrior(data.name(), data.team(), data.hp(), data.attack(), data.defense(), data.speed());
            case MAGE -> new Mage(data.name(), data.team(), data.hp(), data.attack(), data.defense(), data.speed(), data.mana());
            case HEALER -> new Healer(data.name(), data.team(), data.hp(), data.attack(), data.defense(), data.speed());
            case SKELETON -> new Skeleton(data.name(), data.team(), data.hp(), data.attack(), data.defense(), data.speed());
            case ORC -> new Orc(data.name(), data.team(), data.hp(), data.attack(), data.defense(), data.speed());
            case VAMPIRE -> new Vampire(data.name(), data.team(), data.hp(), data.attack(), data.defense(), data.speed());
            case WEREWOLF -> new Werewolf(data.name(), data.team(), data.hp(), data.attack(), data.defense(), data.speed());
            case GOBLIN -> new Goblin(data.name(), data.team(), data.hp(), data.attack(), data.defense(), data.speed());
            case GIANT_SPIDER -> new GiantSpider(data.name(), data.team(), data.hp(), data.attack(), data.defense(), data.speed());
            case SANDWORM -> new Sandworm(data.name(), data.team(), data.hp(), data.attack(), data.defense(), data.speed());
        };
    }
}

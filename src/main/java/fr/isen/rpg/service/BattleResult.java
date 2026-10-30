package fr.isen.rpg.service;

import fr.isen.rpg.model.GameCharacter;
import fr.isen.rpg.model.Team;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @param winner      empty when the battle ended in a draw
 * @param damageDealt total damage dealt by each character, including those who dealt none
 */
public record BattleResult(Optional<Team> winner, int rounds,
                           Map<GameCharacter, Integer> damageDealt, List<GameCharacter> survivors) {

    public BattleResult {
        damageDealt = Collections.unmodifiableMap(new LinkedHashMap<>(damageDealt));
        survivors = List.copyOf(survivors);
    }

    public boolean isDraw() {
        return winner.isEmpty();
    }

    public int damageDealtBy(GameCharacter character) {
        return damageDealt.getOrDefault(character, 0);
    }

    /** Characters sorted from the highest to the lowest damage dealt. */
    public List<GameCharacter> damageRanking() {
        return damageDealt.entrySet().stream()
                .sorted(Map.Entry.<GameCharacter, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .toList();
    }

    /** The most valuable player is the character who dealt the most damage. */
    public Optional<GameCharacter> mvp() {
        return damageRanking().stream().findFirst();
    }
}

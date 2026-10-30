package fr.isen.rpg.service;

import fr.isen.rpg.model.GameCharacter;
import fr.isen.rpg.model.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Draws a random enemy team from the pool of monsters, so every run is different while the
 * battle rules themselves stay deterministic. The Random is injected so a seed can replay a draft.
 */
public class MonsterDraft {

    public static final int DEFAULT_TEAM_SIZE = 4;

    private final int teamSize;
    private final Random random;

    public MonsterDraft(int teamSize, Random random) {
        if (teamSize <= 0) {
            throw new IllegalArgumentException("Team size must be positive: " + teamSize);
        }
        this.teamSize = teamSize;
        this.random = Objects.requireNonNull(random, "random must not be null");
    }

    /**
     * Keeps every hero and at most {@code teamSize} monsters (all of them if the pool is smaller).
     */
    public List<GameCharacter> draft(List<GameCharacter> roster) {
        List<GameCharacter> monsters = roster.stream()
                .filter(character -> character.getTeam() == Team.MONSTERS)
                .collect(Collectors.toCollection(ArrayList::new));
        Collections.shuffle(monsters, random);
        List<GameCharacter> drafted = monsters.subList(0, Math.min(teamSize, monsters.size()));

        return Stream.concat(
                        roster.stream().filter(character -> character.getTeam() != Team.MONSTERS),
                        drafted.stream())
                .toList();
    }
}

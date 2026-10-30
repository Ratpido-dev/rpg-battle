package fr.isen.rpg.model;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Everything a character can see when choosing its target: who is still alive and on which side.
 */
public class Battlefield {

    private final List<GameCharacter> characters;

    public Battlefield(List<GameCharacter> characters) {
        this.characters = List.copyOf(characters);
    }

    public List<GameCharacter> aliveCharacters() {
        return characters.stream()
                .filter(GameCharacter::isAlive)
                .toList();
    }

    /** Living enemies that can currently be attacked (a burrowed sandworm, for example, cannot). */
    public List<GameCharacter> targetableEnemiesOf(GameCharacter character) {
        return aliveCharacters().stream()
                .filter(other -> other.getTeam() != character.getTeam())
                .filter(GameCharacter::isTargetable)
                .toList();
    }

    /** Includes the character itself. */
    public List<GameCharacter> aliveAlliesOf(GameCharacter character) {
        return aliveCharacters().stream()
                .filter(other -> other.getTeam() == character.getTeam())
                .toList();
    }

    public Set<Team> teamsStillStanding() {
        return aliveCharacters().stream()
                .map(GameCharacter::getTeam)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(Team.class)));
    }

    /** @return the last team with living members, or empty while at least two teams still fight */
    public Optional<Team> winningTeam() {
        Set<Team> standing = teamsStillStanding();
        return standing.size() == 1 ? standing.stream().findFirst() : Optional.empty();
    }
}

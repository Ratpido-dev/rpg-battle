package fr.isen.rpg.service;

import fr.isen.rpg.exception.InvalidBattleException;
import fr.isen.rpg.model.ActionReport;
import fr.isen.rpg.model.Battlefield;
import fr.isen.rpg.model.GameCharacter;
import fr.isen.rpg.model.Team;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Runs a turn-based battle until a single team is left standing.
 * It does not know any concrete character class: each character decides its own action.
 */
public class BattleService {

    /** Two healers can keep each other alive forever, so a battle must end at some point. */
    public static final int DEFAULT_MAX_ROUNDS = 50;

    private final BattleListener listener;
    private final int maxRounds;

    public BattleService(BattleListener listener) {
        this(listener, DEFAULT_MAX_ROUNDS);
    }

    public BattleService(BattleListener listener, int maxRounds) {
        if (maxRounds <= 0) {
            throw new IllegalArgumentException("maxRounds must be positive: " + maxRounds);
        }
        this.listener = Objects.requireNonNull(listener, "listener must not be null");
        this.maxRounds = maxRounds;
    }

    public BattleResult fight(List<GameCharacter> characters) {
        if (characters == null || characters.isEmpty()) {
            throw new InvalidBattleException("A battle needs characters");
        }
        Battlefield battlefield = new Battlefield(characters);
        if (battlefield.teamsStillStanding().size() < 2) {
            throw new InvalidBattleException("A battle needs at least two teams with living characters");
        }

        List<GameCharacter> fighters = List.copyOf(characters);
        Map<GameCharacter, Integer> damageDealt = new LinkedHashMap<>();
        fighters.forEach(character -> damageDealt.put(character, 0));

        for (int round = 1; round <= maxRounds; round++) {
            listener.onRoundStart(round);
            Optional<Team> winner = playRound(battlefield, damageDealt);
            listener.onRoundEnd(round, fighters);
            if (winner.isPresent()) {
                return new BattleResult(winner, round, damageDealt, battlefield.aliveCharacters());
            }
        }
        return new BattleResult(Optional.empty(), maxRounds, damageDealt, battlefield.aliveCharacters());
    }

    /** @return the winning team as soon as the round produces one, the rest of the round being skipped */
    private Optional<Team> playRound(Battlefield battlefield, Map<GameCharacter, Integer> damageDealt) {
        for (GameCharacter actor : turnOrder(battlefield)) {
            // The order is fixed at the start of the round, so an actor may have died since.
            if (!actor.isAlive()) {
                continue;
            }
            actor.performAction(battlefield).ifPresent(report -> record(report, damageDealt));
            Optional<Team> winner = battlefield.winningTeam();
            if (winner.isPresent()) {
                return winner;
            }
        }
        return Optional.empty();
    }

    /** Fastest first; the sort is stable, so ties keep the order of the data file. */
    private List<GameCharacter> turnOrder(Battlefield battlefield) {
        return battlefield.aliveCharacters().stream()
                .sorted(Comparator.comparingInt(GameCharacter::getSpeed).reversed())
                .toList();
    }

    private void record(ActionReport report, Map<GameCharacter, Integer> damageDealt) {
        if (report.type().isOffensive()) {
            damageDealt.merge(report.actor(), report.amount(), Integer::sum);
        }
        listener.onAction(report);
    }
}

package fr.isen.rpg.service;

import fr.isen.rpg.exception.InvalidBattleException;
import fr.isen.rpg.model.GameCharacter;
import fr.isen.rpg.model.Skeleton;
import fr.isen.rpg.model.Team;
import fr.isen.rpg.model.Warrior;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BattleServiceTest {

    private final BattleService battleService = new BattleService(new BattleListener() {
    });

    @Test
    void shouldDeclareStrongerTeamWinner() {
        Warrior hero = new Warrior("Arthur", Team.HEROES, 100, 30, 0, 5);
        Skeleton monster = new Skeleton("Bones", Team.MONSTERS, 50, 10, 0, 1);

        BattleResult result = battleService.fight(List.of(hero, monster));

        assertEquals(Optional.of(Team.HEROES), result.winner());
        assertEquals(2, result.rounds());
        assertEquals(List.of(hero), result.survivors());
        assertFalse(monster.isAlive());
    }

    @Test
    void shouldLetFasterCharacterActFirst() {
        // Both kill in one hit: only the turn order decides the winner.
        Warrior fastHero = new Warrior("Arthur", Team.HEROES, 10, 100, 0, 10);
        Skeleton slowMonster = new Skeleton("Bones", Team.MONSTERS, 10, 100, 0, 1);

        BattleResult result = battleService.fight(List.of(slowMonster, fastHero));

        assertEquals(Optional.of(Team.HEROES), result.winner());
        assertEquals(1, result.rounds());
        assertEquals(0, result.damageDealtBy(slowMonster));
    }

    @Test
    void shouldTrackDamageAndElectMvp() {
        Warrior hero = new Warrior("Arthur", Team.HEROES, 100, 30, 0, 5);
        Skeleton monster = new Skeleton("Bones", Team.MONSTERS, 50, 10, 0, 1);

        BattleResult result = battleService.fight(List.of(hero, monster));

        // The second strike only removes the 20 HP left, not the full 30.
        assertEquals(50, result.damageDealtBy(hero));
        assertEquals(10, result.damageDealtBy(monster));
        assertEquals(Optional.of(hero), result.mvp());
        assertEquals(List.of(hero, monster), result.damageRanking());
    }

    @Test
    void shouldNotifyEndOfEveryRoundIncludingTheLastOne() {
        List<Integer> endedRounds = new ArrayList<>();
        BattleService service = new BattleService(new BattleListener() {
            @Override
            public void onRoundEnd(int round, List<GameCharacter> characters) {
                endedRounds.add(round);
            }
        });
        Warrior hero = new Warrior("Arthur", Team.HEROES, 100, 30, 0, 5);
        Skeleton monster = new Skeleton("Bones", Team.MONSTERS, 50, 10, 0, 1);

        BattleResult result = service.fight(List.of(hero, monster));

        assertEquals(List.of(1, 2), endedRounds);
        assertEquals(result.rounds(), endedRounds.getLast());
    }

    @Test
    void shouldEndInDrawAfterMaxRounds() {
        BattleService shortBattle = new BattleService(new BattleListener() {
        }, 3);
        Skeleton tankA = new Skeleton("Bones", Team.HEROES, 1000, 5, 50, 1);
        Skeleton tankB = new Skeleton("Rattle", Team.MONSTERS, 1000, 5, 50, 1);

        BattleResult result = shortBattle.fight(List.of(tankA, tankB));

        assertTrue(result.isDraw());
        assertEquals(3, result.rounds());
        assertEquals(2, result.survivors().size());
    }

    @Test
    void shouldThrowWhenNoCharacters() {
        assertThrows(InvalidBattleException.class, () -> battleService.fight(List.of()));
        assertThrows(InvalidBattleException.class, () -> battleService.fight(null));
    }

    @Test
    void shouldThrowWhenOnlyOneTeam() {
        List<GameCharacter> heroesOnly = List.of(
                new Warrior("Arthur", Team.HEROES, 100, 30, 0, 5),
                new Warrior("Lancelot", Team.HEROES, 100, 30, 0, 5));

        assertThrows(InvalidBattleException.class, () -> battleService.fight(heroesOnly));
    }

    @Test
    void shouldThrowWhenOneTeamIsAlreadyDead() {
        Warrior hero = new Warrior("Arthur", Team.HEROES, 100, 30, 0, 5);
        Skeleton deadMonster = new Skeleton("Bones", Team.MONSTERS, 10, 10, 0, 1);
        deadMonster.takeDamage(100);

        assertThrows(InvalidBattleException.class, () -> battleService.fight(List.of(hero, deadMonster)));
    }

    @Test
    void shouldRejectNonPositiveMaxRounds() {
        assertThrows(IllegalArgumentException.class, () -> new BattleService(new BattleListener() {
        }, 0));
    }
}

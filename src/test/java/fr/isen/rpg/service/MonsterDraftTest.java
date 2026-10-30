package fr.isen.rpg.service;

import fr.isen.rpg.model.GameCharacter;
import fr.isen.rpg.model.Goblin;
import fr.isen.rpg.model.Skeleton;
import fr.isen.rpg.model.Team;
import fr.isen.rpg.model.Warrior;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MonsterDraftTest {

    private final Warrior arthur = new Warrior("Arthur", Team.HEROES, 120, 24, 10, 5);
    private final Warrior lancelot = new Warrior("Lancelot", Team.HEROES, 110, 22, 12, 4);
    private final List<GameCharacter> monsterPool = IntStream.rangeClosed(1, 10)
            .<GameCharacter>mapToObj(i -> new Goblin("Goblin" + i, Team.MONSTERS, 50, 10, 2, 5))
            .toList();
    private final List<GameCharacter> roster = new ArrayList<>(List.of(arthur, lancelot));

    MonsterDraftTest() {
        roster.addAll(monsterPool);
    }

    @Test
    void shouldKeepAllHeroesAndDrawRequestedNumberOfMonsters() {
        List<GameCharacter> fighters = new MonsterDraft(4, new Random(42)).draft(roster);

        assertTrue(fighters.containsAll(List.of(arthur, lancelot)));
        assertEquals(4, fighters.stream().filter(c -> c.getTeam() == Team.MONSTERS).count());
        assertEquals(6, fighters.size());
    }

    @Test
    void shouldNeverDrawTheSameMonsterTwice() {
        List<GameCharacter> fighters = new MonsterDraft(4, new Random(7)).draft(roster);
        List<GameCharacter> drafted = fighters.subList(2, fighters.size());

        assertEquals(drafted.size(), new HashSet<>(drafted).size());
        assertTrue(monsterPool.containsAll(drafted));
    }

    @Test
    void shouldDrawTheSameTeamWithTheSameSeed() {
        List<GameCharacter> first = new MonsterDraft(4, new Random(123)).draft(roster);
        List<GameCharacter> second = new MonsterDraft(4, new Random(123)).draft(roster);

        assertEquals(first, second);
    }

    @Test
    void shouldTakeEveryMonsterWhenPoolIsSmallerThanTeamSize() {
        Skeleton onlyMonster = new Skeleton("Bones", Team.MONSTERS, 90, 20, 6, 3);

        List<GameCharacter> fighters = new MonsterDraft(4, new Random(1)).draft(List.of(arthur, onlyMonster));

        assertEquals(List.of(arthur, onlyMonster), fighters);
    }

    @Test
    void shouldRejectNonPositiveTeamSize() {
        assertThrows(IllegalArgumentException.class, () -> new MonsterDraft(0, new Random()));
    }
}

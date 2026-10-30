package fr.isen.rpg.model;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WarriorTest {

    private final Warrior warrior = new Warrior("Arthur", Team.HEROES, 100, 20, 0, 5);

    @Test
    void shouldStrikeEnemyWithLowestHp() {
        Skeleton strong = new Skeleton("Grimskull", Team.MONSTERS, 80, 10, 0, 1);
        Skeleton weak = new Skeleton("Bones", Team.MONSTERS, 30, 10, 0, 1);
        Battlefield battlefield = new Battlefield(List.of(warrior, strong, weak));

        ActionReport report = warrior.performAction(battlefield).orElseThrow();

        assertSame(weak, report.target());
        assertEquals(ActionType.STRIKE, report.type());
        assertEquals(20, report.amount());
        assertEquals(10, weak.getHp());
    }

    @Test
    void shouldDoNothingWithoutEnemies() {
        Battlefield battlefield = new Battlefield(List.of(warrior));

        Optional<ActionReport> report = warrior.performAction(battlefield);

        assertTrue(report.isEmpty());
    }
}

package fr.isen.rpg.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class GoblinTest {

    @Test
    void shouldStabEnemyWithLowestDefense() {
        Goblin goblin = new Goblin("Snik", Team.MONSTERS, 55, 16, 3, 10);
        Warrior armoured = new Warrior("Lancelot", Team.HEROES, 110, 22, 12, 4);
        Mage fragile = new Mage("Merlin", Team.HEROES, 70, 18, 3, 7, 60);

        ActionReport report = goblin.performAction(new Battlefield(List.of(goblin, armoured, fragile))).orElseThrow();

        assertSame(fragile, report.target());
        assertEquals(ActionType.STAB, report.type());
        assertEquals(13, report.amount());
    }
}

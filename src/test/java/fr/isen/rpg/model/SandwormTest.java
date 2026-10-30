package fr.isen.rpg.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SandwormTest {

    private final Sandworm sandworm = new Sandworm("Sandmaw", Team.MONSTERS, 160, 10, 0, 1);
    private final Warrior hero = new Warrior("Arthur", Team.HEROES, 120, 24, 0, 5);
    private final Battlefield battlefield = new Battlefield(List.of(sandworm, hero));

    @Test
    void shouldBurrowOnFirstTurnAndBecomeUntargetable() {
        ActionReport report = sandworm.performAction(battlefield).orElseThrow();

        assertEquals(ActionType.BURROW, report.type());
        assertTrue(sandworm.isBurrowed());
        assertFalse(sandworm.isTargetable());
        assertTrue(battlefield.targetableEnemiesOf(hero).isEmpty());
        assertEquals(120, hero.getHp());
    }

    @Test
    void shouldEmergeWithTripleDamageOnSecondTurn() {
        sandworm.performAction(battlefield);

        ActionReport report = sandworm.performAction(battlefield).orElseThrow();

        assertEquals(ActionType.EMERGE, report.type());
        assertSame(hero, report.target());
        assertEquals(30, report.amount());
        assertTrue(sandworm.isTargetable());
    }

    @Test
    void shouldLeaveHeroWithoutTargetWhileBurrowed() {
        sandworm.performAction(battlefield);

        assertTrue(hero.performAction(battlefield).isEmpty());
    }
}

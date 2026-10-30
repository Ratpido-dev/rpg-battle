package fr.isen.rpg.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrcTest {

    private final Orc orc = new Orc("Gruk", Team.MONSTERS, 100, 20, 0, 4);
    private final Warrior weakHitter = new Warrior("Lancelot", Team.HEROES, 100, 10, 0, 4);
    private final Warrior strongHitter = new Warrior("Arthur", Team.HEROES, 100, 30, 0, 5);
    private final Battlefield battlefield = new Battlefield(List.of(orc, weakHitter, strongHitter));

    @Test
    void shouldChargeEnemyWithHighestAttack() {
        ActionReport report = orc.performAction(battlefield).orElseThrow();

        assertSame(strongHitter, report.target());
        assertEquals(ActionType.STRIKE, report.type());
        assertEquals(20, report.amount());
    }

    @Test
    void shouldStayCalmAtExactlyHalfHp() {
        orc.takeDamage(50);

        assertFalse(orc.isEnraged());
    }

    @Test
    void shouldDealMoreDamageWhenEnraged() {
        orc.takeDamage(51);

        ActionReport report = orc.performAction(battlefield).orElseThrow();

        assertTrue(orc.isEnraged());
        assertEquals(ActionType.RAGE_STRIKE, report.type());
        assertEquals(30, report.amount());
    }
}

package fr.isen.rpg.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class HealerTest {

    private final Healer healer = new Healer("Elena", Team.HEROES, 80, 10, 0, 5);
    private final Skeleton enemy = new Skeleton("Bones", Team.MONSTERS, 100, 10, 0, 1);

    @Test
    void shouldHealAllyWithLowestHealthRatio() {
        Warrior warrior = new Warrior("Arthur", Team.HEROES, 100, 20, 0, 5);
        Mage mage = new Mage("Merlin", Team.HEROES, 50, 15, 0, 5, 20);
        warrior.takeDamage(40); // 60%
        mage.takeDamage(30);    // 40%, more injured even though it has fewer HP missing
        Battlefield battlefield = new Battlefield(List.of(healer, warrior, mage, enemy));

        ActionReport report = healer.performAction(battlefield).orElseThrow();

        assertSame(mage, report.target());
        assertEquals(ActionType.HEAL, report.type());
        assertEquals(healer.getHealPower(), report.amount());
        assertEquals(40, mage.getHp());
    }

    @Test
    void shouldReportOnlyHpActuallyRestored() {
        Warrior warrior = new Warrior("Arthur", Team.HEROES, 100, 20, 0, 5);
        warrior.takeDamage(5);
        Battlefield battlefield = new Battlefield(List.of(healer, warrior, enemy));

        ActionReport report = healer.performAction(battlefield).orElseThrow();

        assertEquals(5, report.amount());
    }

    @Test
    void shouldNotHealUndeadAlly() {
        Skeleton undeadAlly = new Skeleton("Rattle", Team.HEROES, 100, 10, 0, 1);
        undeadAlly.takeDamage(50);
        Battlefield battlefield = new Battlefield(List.of(healer, undeadAlly, enemy));

        ActionReport report = healer.performAction(battlefield).orElseThrow();

        assertEquals(ActionType.STRIKE, report.type());
        assertSame(enemy, report.target());
        assertEquals(50, undeadAlly.getHp());
    }

    @Test
    void shouldNotHealItself() {
        healer.takeDamage(40);
        Battlefield battlefield = new Battlefield(List.of(healer, enemy));

        ActionReport report = healer.performAction(battlefield).orElseThrow();

        assertEquals(ActionType.STRIKE, report.type());
        assertEquals(40, healer.getHp());
    }

    @Test
    void shouldAttackWhenNoAllyIsInjured() {
        Battlefield battlefield = new Battlefield(List.of(healer, enemy));

        ActionReport report = healer.performAction(battlefield).orElseThrow();

        assertEquals(ActionType.STRIKE, report.type());
        assertEquals(90, enemy.getHp());
    }
}

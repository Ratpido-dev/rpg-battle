package fr.isen.rpg.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class VampireTest {

    private final Vampire vampire = new Vampire("Vlad", Team.MONSTERS, 100, 20, 0, 7);
    private final Warrior victim = new Warrior("Arthur", Team.HEROES, 100, 10, 0, 5);
    private final Battlefield battlefield = new Battlefield(List.of(vampire, victim));

    @Test
    void shouldRecoverHalfOfTheDamageDealt() {
        vampire.takeDamage(30);

        ActionReport report = vampire.performAction(battlefield).orElseThrow();

        assertEquals(ActionType.DRAIN, report.type());
        assertEquals(20, report.amount());
        assertEquals(80, vampire.getHp());
    }

    @Test
    void shouldNotExceedMaxHpWhenDraining() {
        vampire.takeDamage(5);

        vampire.performAction(battlefield);

        assertEquals(100, vampire.getHp());
    }

    @Test
    void shouldNotBeHealableByOthers() {
        Healer zog = new Healer("Zog", Team.MONSTERS, 70, 10, 0, 5);
        vampire.takeDamage(50);

        ActionReport report = zog.performAction(new Battlefield(List.of(zog, vampire, victim))).orElseThrow();

        assertFalse(Healable.class.isInstance(vampire));
        assertEquals(ActionType.STRIKE, report.type());
        assertEquals(50, vampire.getHp());
    }
}

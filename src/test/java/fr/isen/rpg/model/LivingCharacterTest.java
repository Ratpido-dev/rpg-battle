package fr.isen.rpg.model;

import fr.isen.rpg.exception.CharacterDeadException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LivingCharacterTest {

    // No defense, so takeDamage(x) removes exactly x HP.
    private final Warrior warrior = new Warrior("Arthur", Team.HEROES, 100, 20, 0, 5);

    @Test
    void shouldRestoreHp() {
        warrior.takeDamage(40);

        int restored = warrior.heal(30);

        assertEquals(30, restored);
        assertEquals(90, warrior.getHp());
    }

    @Test
    void shouldNotHealAboveMaxHp() {
        warrior.takeDamage(10);

        int restored = warrior.heal(50);

        assertEquals(10, restored);
        assertEquals(100, warrior.getHp());
        assertFalse(warrior.isInjured());
    }

    @Test
    void shouldThrowWhenHealingDeadCharacter() {
        warrior.takeDamage(100);

        assertThrows(CharacterDeadException.class, () -> warrior.heal(20));
    }

    @Test
    void shouldRejectNonPositiveHealAmount() {
        assertThrows(IllegalArgumentException.class, () -> warrior.heal(0));
    }
}

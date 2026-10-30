package fr.isen.rpg.model;

import fr.isen.rpg.exception.CharacterDeadException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameCharacterTest {

    private final Warrior warrior = new Warrior("Arthur", Team.HEROES, 100, 20, 5, 5);

    @Test
    void shouldReduceDamageByDefense() {
        int lost = warrior.takeDamage(20);

        assertEquals(15, lost);
        assertEquals(85, warrior.getHp());
    }

    @Test
    void shouldDealMinimumDamageWhenDefenseIsHigherThanAttack() {
        int lost = warrior.takeDamage(2);

        assertEquals(GameCharacter.MIN_DAMAGE, lost);
        assertEquals(99, warrior.getHp());
    }

    @Test
    void shouldNotDropBelowZeroHp() {
        int lost = warrior.takeDamage(500);

        assertEquals(100, lost);
        assertEquals(0, warrior.getHp());
        assertFalse(warrior.isAlive());
    }

    @Test
    void shouldThrowWhenDamagingDeadCharacter() {
        warrior.takeDamage(500);

        assertThrows(CharacterDeadException.class, () -> warrior.takeDamage(10));
    }

    @Test
    void shouldRejectNegativeDamage() {
        assertThrows(IllegalArgumentException.class, () -> warrior.takeDamage(-1));
    }

    @Test
    void shouldPreventDeadCharacterFromActing() {
        Skeleton enemy = new Skeleton("Bones", Team.MONSTERS, 50, 10, 0, 1);
        Battlefield battlefield = new Battlefield(List.of(warrior, enemy));
        warrior.takeDamage(500);

        assertThrows(CharacterDeadException.class, () -> warrior.performAction(battlefield));
    }

    @Test
    void shouldRejectInvalidStats() {
        assertThrows(IllegalArgumentException.class, () -> new Warrior("Arthur", Team.HEROES, 0, 20, 5, 5));
        assertThrows(IllegalArgumentException.class, () -> new Warrior("Arthur", Team.HEROES, 100, -1, 5, 5));
        assertThrows(IllegalArgumentException.class, () -> new Warrior(" ", Team.HEROES, 100, 20, 5, 5));
    }

    @Test
    void shouldComputeHealthRatio() {
        warrior.takeDamage(55);

        assertTrue(warrior.isInjured());
        assertEquals(0.5, warrior.getHealthRatio());
    }
}

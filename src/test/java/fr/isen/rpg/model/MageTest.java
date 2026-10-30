package fr.isen.rpg.model;

import fr.isen.rpg.exception.NotEnoughManaException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MageTest {

    private final Skeleton target = new Skeleton("Bones", Team.MONSTERS, 100, 10, 0, 1);

    @Test
    void shouldCastFireballForDoubleDamageAndConsumeMana() throws NotEnoughManaException {
        Mage mage = new Mage("Merlin", Team.HEROES, 70, 15, 0, 5, 30);

        ActionReport report = mage.castFireball(target);

        assertEquals(ActionType.FIREBALL, report.type());
        assertEquals(30, report.amount());
        assertEquals(30 - Mage.FIREBALL_COST, mage.getMana());
    }

    @Test
    void shouldThrowWhenNotEnoughManaForFireball() {
        Mage mage = new Mage("Merlin", Team.HEROES, 70, 15, 0, 5, 10);

        NotEnoughManaException e = assertThrows(NotEnoughManaException.class, () -> mage.castFireball(target));

        assertEquals(Mage.FIREBALL_COST, e.getRequired());
        assertEquals(10, e.getAvailable());
        assertEquals(10, mage.getMana());
        assertEquals(100, target.getHp());
    }

    @Test
    void shouldFallBackToStaffHitAndRegainManaWhenOutOfMana() {
        Mage mage = new Mage("Merlin", Team.HEROES, 70, 15, 0, 5, 20);
        Battlefield battlefield = new Battlefield(List.of(mage, target));

        ActionReport first = mage.performAction(battlefield).orElseThrow();
        ActionReport second = mage.performAction(battlefield).orElseThrow();

        assertEquals(ActionType.FIREBALL, first.type());
        assertEquals(ActionType.STAFF_HIT, second.type());
        assertEquals(7, second.amount());
        assertEquals(Mage.MANA_REGEN, mage.getMana());
    }

    @Test
    void shouldNotRegainManaAboveMaximum() {
        Mage mage = new Mage("Merlin", Team.HEROES, 70, 15, 0, 5, 15);
        Battlefield battlefield = new Battlefield(List.of(mage, target));

        mage.performAction(battlefield);

        assertEquals(15, mage.getMana());
    }

    @Test
    void shouldTargetEnemyWithHighestHp() {
        Mage mage = new Mage("Merlin", Team.HEROES, 70, 15, 0, 5, 20);
        Skeleton weak = new Skeleton("Rattle", Team.MONSTERS, 20, 10, 0, 1);
        Battlefield battlefield = new Battlefield(List.of(mage, weak, target));

        ActionReport report = mage.performAction(battlefield).orElseThrow();

        assertSame(target, report.target());
    }

    @Test
    void shouldRejectNegativeMana() {
        assertThrows(IllegalArgumentException.class, () -> new Mage("Merlin", Team.HEROES, 70, 15, 0, 5, -1));
    }
}

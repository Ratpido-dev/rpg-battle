package fr.isen.rpg.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class WerewolfTest {

    private final Werewolf werewolf = new Werewolf("Fenrir", Team.MONSTERS, 110, 20, 0, 9);

    @Test
    void shouldHuntMostWoundedEnemyByRatio() {
        Warrior tank = new Warrior("Arthur", Team.HEROES, 200, 10, 0, 5);
        Mage mage = new Mage("Merlin", Team.HEROES, 50, 10, 0, 7, 0);
        tank.takeDamage(80); // 120/200 = 60%, but 80 HP missing
        mage.takeDamage(25); // 25/50 = 50%, only 25 HP missing

        ActionReport report = werewolf.performAction(new Battlefield(List.of(werewolf, tank, mage))).orElseThrow();

        assertSame(mage, report.target());
    }

    @Test
    void shouldStrikeNormallyAHealthyTarget() {
        Warrior target = new Warrior("Arthur", Team.HEROES, 100, 10, 0, 5);

        ActionReport report = werewolf.performAction(new Battlefield(List.of(werewolf, target))).orElseThrow();

        assertEquals(ActionType.STRIKE, report.type());
        assertEquals(20, report.amount());
    }

    @Test
    void shouldMaulTargetBelowHalfHp() {
        Warrior target = new Warrior("Arthur", Team.HEROES, 100, 10, 0, 5);
        target.takeDamage(60);

        ActionReport report = werewolf.performAction(new Battlefield(List.of(werewolf, target))).orElseThrow();

        assertEquals(ActionType.MAUL, report.type());
        assertEquals(40, report.amount());
        assertEquals(0, target.getHp());
    }
}

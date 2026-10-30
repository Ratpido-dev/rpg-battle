package fr.isen.rpg.model;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class GiantSpiderTest {

    private final GiantSpider spider = new GiantSpider("Arachna", Team.MONSTERS, 85, 10, 0, 7);
    private final Warrior slow = new Warrior("Lancelot", Team.HEROES, 200, 10, 0, 4);
    private final Mage fast = new Mage("Merlin", Team.HEROES, 200, 10, 0, 7, 0);
    private final Battlefield battlefield = new Battlefield(List.of(spider, slow, fast));

    @Test
    void shouldChooseFastestEnemyAsPrey() {
        ActionReport report = spider.performAction(battlefield).orElseThrow();

        assertSame(fast, report.target());
        assertEquals(Optional.of(fast), spider.getPrey());
    }

    @Test
    void shouldBuildUpVenomOnTheSamePrey() {
        int first = spider.performAction(battlefield).orElseThrow().amount();
        int second = spider.performAction(battlefield).orElseThrow().amount();
        int third = spider.performAction(battlefield).orElseThrow().amount();

        assertEquals(10, first);
        assertEquals(10 + GiantSpider.VENOM_PER_BITE, second);
        assertEquals(10 + 2 * GiantSpider.VENOM_PER_BITE, third);
    }

    @Test
    void shouldResetVenomWhenPreyDies() {
        spider.performAction(battlefield);
        spider.performAction(battlefield);
        fast.takeDamage(1000);

        ActionReport report = spider.performAction(battlefield).orElseThrow();

        assertSame(slow, report.target());
        assertEquals(10, report.amount());
    }
}

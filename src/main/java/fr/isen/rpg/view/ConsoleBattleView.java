package fr.isen.rpg.view;

import fr.isen.rpg.model.ActionReport;
import fr.isen.rpg.model.GameCharacter;
import fr.isen.rpg.model.Mage;
import fr.isen.rpg.model.Team;
import fr.isen.rpg.service.BattleListener;
import fr.isen.rpg.service.BattleResult;
import fr.isen.rpg.view.Ansi.Color;

import java.io.PrintStream;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Everything the user sees in the console. No game rule lives here.
 */
public class ConsoleBattleView implements BattleListener {

    private static final int LINE_WIDTH = 60;
    private static final int BAR_WIDTH = 20;
    private static final String FULL = "█";
    private static final String EMPTY = "░";

    private final PrintStream out;
    private final Ansi ansi;

    public ConsoleBattleView(PrintStream out, Ansi ansi) {
        this.out = out;
        this.ansi = ansi;
    }

    public void showRoster(List<GameCharacter> characters) {
        printBanner("⚔  RPG BATTLE  ⚔", Color.YELLOW);
        int width = nameWidth(characters);
        byTeam(characters).forEach((team, members) -> {
            out.println();
            out.println(teamHeader(team));
            members.forEach(member -> out.printf("    %s  %-11s  HP %3d  ATK %2d  DEF %2d  SPD %2d%s%n",
                    paddedName(member, width), member.getClass().getSimpleName(), member.getMaxHp(),
                    member.getAttack(), member.getDefense(), member.getSpeed(), manaInfo(member)));
        });
    }

    public void showSeed(long seed) {
        out.println();
        out.println(ansi.color("  Seed " + seed + " · replay this exact battle with --seed " + seed, Color.GRAY));
    }

    @Override
    public void onRoundStart(int round) {
        String title = " Round " + round + " ";
        int side = (LINE_WIDTH - title.length()) / 2;
        out.println();
        out.println(ansi.bold("─".repeat(side) + title + "─".repeat(LINE_WIDTH - side - title.length())));
    }

    @Override
    public void onAction(ActionReport report) {
        GameCharacter actor = report.actor();
        GameCharacter target = report.target();
        String actorName = name(actor);
        String targetName = name(target);
        String description = switch (report.type()) {
            case STRIKE -> "⚔  " + actorName + " strikes " + targetName;
            case FIREBALL -> "✦  " + actorName + " casts a fireball on " + targetName;
            case STAFF_HIT -> "✧  " + actorName + " is low on mana and hits " + targetName + " with a staff";
            case HEAL -> "✚  " + actorName + " heals " + targetName;
            case RAGE_STRIKE -> "‼  " + actorName + " flies into a rage and smashes " + targetName;
            case DRAIN -> "☾  " + actorName + " drains the blood of " + targetName;
            case MAUL -> "✶  " + actorName + " smells blood and mauls " + targetName;
            case STAB -> "»  " + actorName + " stabs " + targetName + " where the armour is weakest";
            case BITE -> "☠  " + actorName + " bites " + targetName + " with its venom";
            case BURROW -> "≈  " + actorName + " burrows into the sand";
            case EMERGE -> "≈  " + actorName + " bursts out of the sand under " + targetName;
        };
        String effect = switch (report.type()) {
            case BURROW -> ansi.color("(cannot be targeted until it emerges)", Color.GRAY);
            case HEAL -> ansi.bold(ansi.color("+" + report.amount() + " HP", Color.GREEN)) + " " + remainingHp(target);
            case DRAIN -> damage(report) + " " + remainingHp(target)
                    + ansi.color(" · " + actor.getName() + " now " + actor.getHp() + "/" + actor.getMaxHp(), Color.GRAY);
            default -> damage(report) + " " + remainingHp(target);
        };

        out.println("  " + description + "  " + effect);
        if (!target.isAlive()) {
            out.println("  " + ansi.bold(ansi.color("✝  " + target.getName() + " is defeated!", Color.GRAY)));
        }
    }

    /** Shows the state of both teams, so the viewer sees who is winning without reading every line. */
    @Override
    public void onRoundEnd(int round, List<GameCharacter> characters) {
        int width = nameWidth(characters);
        out.println();
        byTeam(characters).forEach((team, members) -> {
            out.println(teamHeader(team));
            members.forEach(member -> out.println("    " + statusLine(member, width)));
        });
    }

    public void showResult(BattleResult result) {
        out.println();
        result.winner().ifPresentOrElse(
                team -> printBanner("VICTORY  ·  " + team + " win in " + result.rounds() + " rounds", teamColor(team)),
                () -> printBanner("DRAW  ·  nobody won within " + result.rounds() + " rounds", Color.YELLOW));

        out.println();
        out.println(ansi.bold("  Damage dealt"));
        List<GameCharacter> ranking = result.damageRanking();
        int width = nameWidth(ranking);
        int best = ranking.isEmpty() ? 0 : result.damageDealtBy(ranking.getFirst());
        ranking.forEach(character -> {
            int damage = result.damageDealtBy(character);
            int length = best == 0 ? 0 : Math.round((float) damage / best * BAR_WIDTH);
            out.printf("    %s  %s %d%n", paddedName(character, width),
                    ansi.color(FULL.repeat(length), teamColor(character.getTeam())), damage);
        });

        result.mvp().ifPresent(mvp -> {
            out.println();
            out.println(ansi.bold(ansi.color("  ★ MVP: " + mvp + " (" + result.damageDealtBy(mvp) + " damage)", Color.YELLOW)));
        });
        out.println();
    }

    private String damage(ActionReport report) {
        return ansi.bold(ansi.color("-" + report.amount() + " HP", Color.RED));
    }

    private String remainingHp(GameCharacter character) {
        return ansi.color("(" + character.getHp() + "/" + character.getMaxHp() + ")", Color.GRAY);
    }

    private String statusLine(GameCharacter character, int width) {
        if (!character.isAlive()) {
            return ansi.color(pad(character.getName(), width) + "  ✝ defeated", Color.GRAY);
        }
        String hidden = character.isTargetable() ? "" : ansi.color("  ≈ underground", Color.GRAY);
        return paddedName(character, width) + "  " + healthBar(character)
                + String.format(" %3d/%-3d", character.getHp(), character.getMaxHp()) + manaInfo(character) + hidden;
    }

    private String healthBar(GameCharacter character) {
        // At least one block while alive, so a character on 1 HP does not look dead.
        int filled = Math.max(1, (int) Math.round(character.getHealthRatio() * BAR_WIDTH));
        return ansi.color(FULL.repeat(filled), healthColor(character))
                + ansi.color(EMPTY.repeat(BAR_WIDTH - filled), Color.GRAY);
    }

    private String manaInfo(GameCharacter character) {
        if (character instanceof Mage mage) {
            return ansi.color("  MP " + mage.getMana() + "/" + mage.getMaxMana(), Color.BLUE);
        }
        return "";
    }

    private String teamHeader(Team team) {
        String label = switch (team) {
            case HEROES -> "HEROES · your team";
            case MONSTERS -> "MONSTERS · the enemy";
        };
        return ansi.bold(ansi.color("  ■ " + label, teamColor(team)));
    }

    private void printBanner(String text, Color color) {
        int inner = LINE_WIDTH - 2;
        int left = (inner - text.length()) / 2;
        String content = " ".repeat(left) + text + " ".repeat(inner - left - text.length());
        out.println(ansi.color("╔" + "═".repeat(inner) + "╗", color));
        out.println(ansi.color("║", color) + ansi.bold(ansi.color(content, color)) + ansi.color("║", color));
        out.println(ansi.color("╚" + "═".repeat(inner) + "╝", color));
    }

    private String name(GameCharacter character) {
        return ansi.bold(ansi.color(character.getName(), teamColor(character.getTeam())));
    }

    /** Pads before coloring: escape codes are invisible but would break printf's width computation. */
    private String paddedName(GameCharacter character, int width) {
        return ansi.bold(ansi.color(pad(character.getName(), width), teamColor(character.getTeam())));
    }

    private static Color teamColor(Team team) {
        return switch (team) {
            case HEROES -> Color.CYAN;
            case MONSTERS -> Color.RED;
        };
    }

    private static Color healthColor(GameCharacter character) {
        double ratio = character.getHealthRatio();
        if (ratio > 0.5) {
            return Color.GREEN;
        }
        return ratio > 0.25 ? Color.YELLOW : Color.RED;
    }

    private static Map<Team, List<GameCharacter>> byTeam(List<GameCharacter> characters) {
        return characters.stream()
                .collect(Collectors.groupingBy(GameCharacter::getTeam, () -> new EnumMap<>(Team.class), Collectors.toList()));
    }

    private static int nameWidth(List<GameCharacter> characters) {
        return characters.stream().mapToInt(character -> character.getName().length()).max().orElse(0);
    }

    private static String pad(String text, int width) {
        return text + " ".repeat(Math.max(0, width - text.length()));
    }
}

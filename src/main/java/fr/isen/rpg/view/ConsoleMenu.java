package fr.isen.rpg.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Asks the user how the battle should be displayed.
 */
public class ConsoleMenu {

    private final BufferedReader in;
    private final PrintStream out;

    public ConsoleMenu(BufferedReader in, PrintStream out) {
        this.in = in;
        this.out = out;
    }

    /** Asks again until the answer is valid. */
    public BattleMode askBattleMode() {
        while (true) {
            out.println();
            out.println("Choose a mode:");
            out.println("  1 - Automatic");
            out.println("  2 - Step by step (press Enter between rounds)");
            out.print("> ");

            String answer;
            try {
                answer = in.readLine();
            } catch (IOException e) {
                out.println("Cannot read the console, starting in automatic mode.");
                return BattleMode.AUTO;
            }
            // No input at all (e.g. input redirected from an empty file): nobody could press Enter anyway.
            if (answer == null) {
                return BattleMode.AUTO;
            }

            BattleMode mode = switch (answer.trim()) {
                case "1" -> BattleMode.AUTO;
                case "2" -> BattleMode.STEP_BY_STEP;
                default -> null;
            };
            if (mode != null) {
                return mode;
            }
            out.println("Please type 1 or 2.");
        }
    }
}

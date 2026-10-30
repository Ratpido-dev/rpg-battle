package fr.isen.rpg.view;

import fr.isen.rpg.model.ActionReport;
import fr.isen.rpg.model.GameCharacter;
import fr.isen.rpg.service.BattleListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Objects;

/**
 * Decorator that waits for Enter before every round after the first one, so a viewer can follow
 * the battle. The display itself is left to the wrapped listener, and BattleService is unaware
 * of the pauses.
 */
public class PausingBattleListener implements BattleListener {

    private final BattleListener delegate;
    private final BufferedReader in;
    private final PrintStream out;
    private boolean inputAvailable = true;

    public PausingBattleListener(BattleListener delegate, BufferedReader in, PrintStream out) {
        this.delegate = Objects.requireNonNull(delegate, "delegate must not be null");
        this.in = in;
        this.out = out;
    }

    @Override
    public void onRoundStart(int round) {
        if (round > 1) {
            waitForEnter(round);
        }
        delegate.onRoundStart(round);
    }

    @Override
    public void onAction(ActionReport report) {
        delegate.onAction(report);
    }

    @Override
    public void onRoundEnd(int round, List<GameCharacter> characters) {
        delegate.onRoundEnd(round, characters);
    }

    private void waitForEnter(int round) {
        if (!inputAvailable) {
            return;
        }
        out.println();
        out.print("[Press Enter for round " + round + "]");
        try {
            if (in.readLine() == null) {
                stopPausing();
            }
        } catch (IOException e) {
            stopPausing();
        }
    }

    /** Once the input is closed, pausing again would be pointless: the battle finishes on its own. */
    private void stopPausing() {
        inputAvailable = false;
        out.println();
        out.println("(no more input, continuing automatically)");
    }
}

package fr.isen.rpg.view;

import fr.isen.rpg.model.GameCharacter;
import fr.isen.rpg.service.BattleListener;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PausingBattleListenerTest {

    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private final List<Integer> roundsSeenByDelegate = new ArrayList<>();
    private final List<Integer> roundsEndedByDelegate = new ArrayList<>();
    private final BattleListener delegate = new BattleListener() {
        @Override
        public void onRoundStart(int round) {
            roundsSeenByDelegate.add(round);
        }

        @Override
        public void onRoundEnd(int round, List<GameCharacter> characters) {
            roundsEndedByDelegate.add(round);
        }
    };

    private PausingBattleListener listenerWithInput(String input) {
        return new PausingBattleListener(delegate, new BufferedReader(new StringReader(input)), new PrintStream(output));
    }

    @Test
    void shouldPauseBeforeEveryRoundExceptTheFirst() {
        PausingBattleListener listener = listenerWithInput("\n\n");

        listener.onRoundStart(1);
        listener.onRoundStart(2);
        listener.onRoundStart(3);

        String printed = output.toString();
        assertFalse(printed.contains("round 1"));
        assertTrue(printed.contains("[Press Enter for round 2]"));
        assertTrue(printed.contains("[Press Enter for round 3]"));
        assertEquals(List.of(1, 2, 3), roundsSeenByDelegate);
    }

    @Test
    void shouldForwardEndOfRoundToDelegate() {
        PausingBattleListener listener = listenerWithInput("");

        listener.onRoundEnd(1, List.of());

        // Without forwarding, the status board would silently disappear in step-by-step mode.
        assertEquals(List.of(1), roundsEndedByDelegate);
    }

    @Test
    void shouldContinueAutomaticallyWhenInputIsClosed() {
        PausingBattleListener listener = listenerWithInput("");

        listener.onRoundStart(2);
        listener.onRoundStart(3);
        listener.onRoundStart(4);

        String printed = output.toString();
        assertTrue(printed.contains("continuing automatically"));
        // Only the first pause is attempted, the next rounds run without waiting.
        assertFalse(printed.contains("round 3"));
        assertEquals(List.of(2, 3, 4), roundsSeenByDelegate);
    }
}

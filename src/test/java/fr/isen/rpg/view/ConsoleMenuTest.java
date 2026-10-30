package fr.isen.rpg.view;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsoleMenuTest {

    private final ByteArrayOutputStream output = new ByteArrayOutputStream();

    private ConsoleMenu menuWithInput(String input) {
        return new ConsoleMenu(new BufferedReader(new StringReader(input)), new PrintStream(output));
    }

    @Test
    void shouldChooseAutomaticMode() {
        assertEquals(BattleMode.AUTO, menuWithInput("1\n").askBattleMode());
    }

    @Test
    void shouldChooseStepByStepModeIgnoringSpaces() {
        assertEquals(BattleMode.STEP_BY_STEP, menuWithInput("  2 \n").askBattleMode());
    }

    @Test
    void shouldAskAgainAfterInvalidAnswer() {
        BattleMode mode = menuWithInput("3\nhello\n2\n").askBattleMode();

        assertEquals(BattleMode.STEP_BY_STEP, mode);
        assertTrue(output.toString().contains("Please type 1 or 2."));
    }

    @Test
    void shouldFallBackToAutomaticModeWithoutInput() {
        assertEquals(BattleMode.AUTO, menuWithInput("").askBattleMode());
    }
}

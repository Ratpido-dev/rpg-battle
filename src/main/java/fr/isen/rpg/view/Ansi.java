package fr.isen.rpg.view;

/**
 * Wraps text in ANSI escape codes. Can be disabled (see the NO_COLOR convention) so the output
 * stays readable in terminals or files that do not support colors.
 */
public class Ansi {

    public enum Color {
        RED("31"),
        GREEN("32"),
        YELLOW("33"),
        BLUE("34"),
        CYAN("36"),
        GRAY("90");

        private final String code;

        Color(String code) {
            this.code = code;
        }
    }

    private static final String ESCAPE = "\u001B[";
    private static final String RESET = ESCAPE + "0m";
    private static final String BOLD = "1";

    private final boolean enabled;

    public Ansi(boolean enabled) {
        this.enabled = enabled;
    }

    public String color(String text, Color color) {
        return wrap(text, color.code);
    }

    public String bold(String text) {
        return wrap(text, BOLD);
    }

    private String wrap(String text, String code) {
        return enabled ? ESCAPE + code + "m" + text + RESET : text;
    }
}

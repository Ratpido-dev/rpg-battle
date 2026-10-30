package fr.isen.rpg;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Command line options: {@code [path/to/characters.csv] [--seed <number>]}.
 *
 * @param dataFile empty to use the data file bundled with the application
 * @param seed     empty to draw a new random monster team
 */
public record AppArguments(Optional<Path> dataFile, Optional<Long> seed) {

    public static final String USAGE = "Usage: java -jar rpg-battle.jar [path/to/characters.csv] [--seed <number>]";

    private static final String SEED_OPTION = "--seed";

    /**
     * @throws IllegalArgumentException if the arguments do not match {@link #USAGE}
     */
    public static AppArguments parse(String... args) {
        Path dataFile = null;
        Long seed = null;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals(SEED_OPTION)) {
                if (i + 1 >= args.length) {
                    throw new IllegalArgumentException(SEED_OPTION + " must be followed by a number");
                }
                seed = parseSeed(args[++i]);
            } else if (dataFile == null) {
                dataFile = Path.of(args[i]);
            } else {
                throw new IllegalArgumentException("Unexpected argument: " + args[i]);
            }
        }
        return new AppArguments(Optional.ofNullable(dataFile), Optional.ofNullable(seed));
    }

    private static long parseSeed(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("'" + value + "' is not a valid seed");
        }
    }
}

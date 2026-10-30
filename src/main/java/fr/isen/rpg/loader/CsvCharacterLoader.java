package fr.isen.rpg.loader;

import fr.isen.rpg.exception.DataLoadingException;
import fr.isen.rpg.exception.InvalidCharacterDataException;
import fr.isen.rpg.model.CharacterClass;
import fr.isen.rpg.model.GameCharacter;
import fr.isen.rpg.model.Team;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Reads characters from a semicolon-separated file:
 * {@code name;class;team;hp;attack;defense;speed;mana}.
 * The first line is a header; blank lines and lines starting with {@code #} are ignored.
 */
public class CsvCharacterLoader implements CharacterLoader {

    private static final String SEPARATOR = ";";
    private static final int EXPECTED_COLUMNS = 8;
    private static final String COMMENT_PREFIX = "#";

    /** Like a Supplier, but allowed to throw IOException when the source cannot be opened. */
    @FunctionalInterface
    private interface ReaderSource {
        Reader open() throws IOException;
    }

    private final String sourceName;
    private final ReaderSource source;

    private CsvCharacterLoader(String sourceName, ReaderSource source) {
        this.sourceName = sourceName;
        this.source = source;
    }

    public static CsvCharacterLoader fromFile(Path path) {
        return new CsvCharacterLoader(path.toString(), () -> Files.newBufferedReader(path, StandardCharsets.UTF_8));
    }

    /** Reads a file bundled with the application, which still works once packaged in a jar. */
    public static CsvCharacterLoader fromClasspath(String resourceName) {
        return new CsvCharacterLoader(resourceName, () -> {
            InputStream in = CsvCharacterLoader.class.getClassLoader().getResourceAsStream(resourceName);
            if (in == null) {
                throw new FileNotFoundException("resource not found");
            }
            return new InputStreamReader(in, StandardCharsets.UTF_8);
        });
    }

    @Override
    public List<GameCharacter> load() throws DataLoadingException {
        try (BufferedReader reader = new BufferedReader(source.open())) {
            return parse(reader);
        } catch (FileNotFoundException | NoSuchFileException e) {
            throw new DataLoadingException("File not found: " + sourceName, e);
        } catch (IOException e) {
            throw new DataLoadingException("Cannot read " + sourceName + " (" + e.getMessage() + ")", e);
        }
    }

    private List<GameCharacter> parse(BufferedReader reader) throws IOException, DataLoadingException {
        List<GameCharacter> characters = new ArrayList<>();
        Set<String> names = new HashSet<>();
        String line;
        int lineNumber = 0;
        while ((line = reader.readLine()) != null) {
            lineNumber++;
            if (lineNumber == 1 || line.isBlank() || line.startsWith(COMMENT_PREFIX)) {
                continue;
            }
            GameCharacter character = parseLine(line, lineNumber);
            // Names identify characters in the battle log, so two characters cannot share one.
            if (!names.add(character.getName())) {
                throw new InvalidCharacterDataException(lineNumber, "duplicate name '" + character.getName() + "'");
            }
            characters.add(character);
        }
        if (characters.isEmpty()) {
            throw new DataLoadingException(sourceName + " contains no character");
        }
        return characters;
    }

    private GameCharacter parseLine(String line, int lineNumber) throws InvalidCharacterDataException {
        String[] columns = line.split(SEPARATOR, -1);
        if (columns.length != EXPECTED_COLUMNS) {
            throw new InvalidCharacterDataException(lineNumber,
                    "expected " + EXPECTED_COLUMNS + " columns but found " + columns.length);
        }
        try {
            CharacterData data = new CharacterData(
                    columns[0].trim(),
                    parseEnum(CharacterClass.class, columns[1], "class"),
                    parseEnum(Team.class, columns[2], "team"),
                    parseInt(columns[3], "hp"),
                    parseInt(columns[4], "attack"),
                    parseInt(columns[5], "defense"),
                    parseInt(columns[6], "speed"),
                    parseInt(columns[7], "mana"));
            return CharacterFactory.create(data);
        } catch (IllegalArgumentException e) {
            // Bad numbers, unknown enum values and invalid stats all end up here.
            throw new InvalidCharacterDataException(lineNumber, e.getMessage());
        }
    }

    private static int parseInt(String value, String column) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("'" + value.trim() + "' is not a valid number for " + column);
        }
    }

    private static <E extends Enum<E>> E parseEnum(Class<E> type, String value, String column) {
        try {
            return Enum.valueOf(type, value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("unknown " + column + " '" + value.trim() + "'");
        }
    }
}

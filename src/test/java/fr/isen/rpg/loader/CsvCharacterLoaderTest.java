package fr.isen.rpg.loader;

import fr.isen.rpg.exception.DataLoadingException;
import fr.isen.rpg.exception.InvalidCharacterDataException;
import fr.isen.rpg.model.GameCharacter;
import fr.isen.rpg.model.Mage;
import fr.isen.rpg.model.Skeleton;
import fr.isen.rpg.model.Team;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CsvCharacterLoaderTest {

    private static final String HEADER = "name;class;team;hp;attack;defense;speed;mana";

    @TempDir
    Path tempDir;

    private CsvCharacterLoader loaderFor(String... lines) throws IOException {
        Path file = tempDir.resolve("characters.csv");
        Files.write(file, List.of(lines));
        return CsvCharacterLoader.fromFile(file);
    }

    @Test
    void shouldLoadCharactersWithTheirStats() throws Exception {
        CsvCharacterLoader loader = loaderFor(HEADER,
                "Merlin;MAGE;HEROES;70;18;3;7;60",
                "Bones;skeleton;monsters;90;20;6;3;0");

        List<GameCharacter> characters = loader.load();

        assertEquals(2, characters.size());
        Mage merlin = assertInstanceOf(Mage.class, characters.get(0));
        assertEquals("Merlin", merlin.getName());
        assertEquals(Team.HEROES, merlin.getTeam());
        assertEquals(70, merlin.getMaxHp());
        assertEquals(60, merlin.getMana());
        assertInstanceOf(Skeleton.class, characters.get(1));
    }

    @Test
    void shouldIgnoreBlankLinesAndComments() throws Exception {
        CsvCharacterLoader loader = loaderFor(HEADER,
                "# heroes",
                "",
                "Arthur;WARRIOR;HEROES;120;24;10;5;0");

        assertEquals(1, loader.load().size());
    }

    @Test
    void shouldLoadDefaultFileFromClasspath() throws Exception {
        List<GameCharacter> characters = CsvCharacterLoader.fromClasspath("characters.csv").load();

        assertFalse(characters.isEmpty());
    }

    @Test
    void shouldRejectUnknownClassWithLineNumber() throws IOException {
        CsvCharacterLoader loader = loaderFor(HEADER,
                "Arthur;WARRIOR;HEROES;120;24;10;5;0",
                "Robin;ARCHER;HEROES;80;20;5;8;0");

        InvalidCharacterDataException e = assertThrows(InvalidCharacterDataException.class, loader::load);

        assertEquals(3, e.getLineNumber());
        assertTrue(e.getMessage().contains("ARCHER"));
    }

    @Test
    void shouldRejectWrongNumberOfColumns() throws IOException {
        CsvCharacterLoader loader = loaderFor(HEADER, "Arthur;WARRIOR;HEROES;120");

        assertThrows(InvalidCharacterDataException.class, loader::load);
    }

    @Test
    void shouldRejectNonNumericStat() throws IOException {
        CsvCharacterLoader loader = loaderFor(HEADER, "Arthur;WARRIOR;HEROES;lots;24;10;5;0");

        InvalidCharacterDataException e = assertThrows(InvalidCharacterDataException.class, loader::load);

        assertTrue(e.getMessage().contains("hp"));
    }

    @Test
    void shouldRejectInvalidStatValue() throws IOException {
        CsvCharacterLoader loader = loaderFor(HEADER, "Arthur;WARRIOR;HEROES;-5;24;10;5;0");

        assertThrows(InvalidCharacterDataException.class, loader::load);
    }

    @Test
    void shouldRejectDuplicateNames() throws IOException {
        CsvCharacterLoader loader = loaderFor(HEADER,
                "Arthur;WARRIOR;HEROES;120;24;10;5;0",
                "Arthur;MAGE;MONSTERS;70;18;3;7;60");

        InvalidCharacterDataException e = assertThrows(InvalidCharacterDataException.class, loader::load);

        assertEquals(3, e.getLineNumber());
    }

    @Test
    void shouldFailWhenFileHasNoCharacter() throws IOException {
        CsvCharacterLoader loader = loaderFor(HEADER);

        assertThrows(DataLoadingException.class, loader::load);
    }

    @Test
    void shouldFailWhenFileDoesNotExist() {
        CsvCharacterLoader loader = CsvCharacterLoader.fromFile(tempDir.resolve("missing.csv"));

        DataLoadingException e = assertThrows(DataLoadingException.class, loader::load);

        // A missing file is a loading problem, not a problem in the data itself.
        assertEquals(DataLoadingException.class, e.getClass());
    }
}

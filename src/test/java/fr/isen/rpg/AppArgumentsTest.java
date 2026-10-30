package fr.isen.rpg;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppArgumentsTest {

    @Test
    void shouldUseDefaultsWithoutArguments() {
        AppArguments arguments = AppArguments.parse();

        assertTrue(arguments.dataFile().isEmpty());
        assertTrue(arguments.seed().isEmpty());
    }

    @Test
    void shouldReadDataFileAndSeedInAnyOrder() {
        AppArguments arguments = AppArguments.parse("--seed", "42", "my.csv");

        assertEquals(Optional.of(Path.of("my.csv")), arguments.dataFile());
        assertEquals(Optional.of(42L), arguments.seed());
    }

    @Test
    void shouldRejectSeedWithoutValue() {
        assertThrows(IllegalArgumentException.class, () -> AppArguments.parse("--seed"));
    }

    @Test
    void shouldRejectNonNumericSeed() {
        assertThrows(IllegalArgumentException.class, () -> AppArguments.parse("--seed", "abc"));
    }

    @Test
    void shouldRejectSecondDataFile() {
        assertThrows(IllegalArgumentException.class, () -> AppArguments.parse("a.csv", "b.csv"));
    }
}

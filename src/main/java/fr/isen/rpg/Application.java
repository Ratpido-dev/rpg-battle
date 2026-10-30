package fr.isen.rpg;

import fr.isen.rpg.exception.DataLoadingException;
import fr.isen.rpg.exception.InvalidBattleException;
import fr.isen.rpg.loader.CharacterLoader;
import fr.isen.rpg.loader.CsvCharacterLoader;
import fr.isen.rpg.model.GameCharacter;
import fr.isen.rpg.service.BattleListener;
import fr.isen.rpg.service.BattleResult;
import fr.isen.rpg.service.BattleService;
import fr.isen.rpg.service.MonsterDraft;
import fr.isen.rpg.view.Ansi;
import fr.isen.rpg.view.BattleMode;
import fr.isen.rpg.view.ConsoleBattleView;
import fr.isen.rpg.view.ConsoleMenu;
import fr.isen.rpg.view.PausingBattleListener;

import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Entry point: only wires the components together and reports fatal errors.
 * See {@link AppArguments#USAGE} for the command line options.
 */
public final class Application {

    private static final String DEFAULT_DATA_FILE = "characters.csv";
    /** Generated seeds stay short so they are easy to retype with --seed. */
    private static final long MAX_GENERATED_SEED = 100_000;

    private Application() {
    }

    public static void main(String[] args) {
        AppArguments arguments;
        try {
            arguments = AppArguments.parse(args);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.err.println(AppArguments.USAGE);
            System.exit(2);
            return;
        }

        CharacterLoader loader = arguments.dataFile()
                .<CharacterLoader>map(CsvCharacterLoader::fromFile)
                .orElseGet(() -> CsvCharacterLoader.fromClasspath(DEFAULT_DATA_FILE));
        long seed = arguments.seed().orElseGet(() -> ThreadLocalRandom.current().nextLong(MAX_GENERATED_SEED));

        // Explicit UTF-8 so the box-drawing characters and health bars display on every platform.
        PrintStream out = new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8);
        // NO_COLOR is a common convention to disable colored output (https://no-color.org).
        ConsoleBattleView view = new ConsoleBattleView(out, new Ansi(System.getenv("NO_COLOR") == null));
        // A single reader shared by the menu and the pauses, so no typed input is lost between them.
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

        try {
            List<GameCharacter> roster = loader.load();
            List<GameCharacter> fighters = new MonsterDraft(MonsterDraft.DEFAULT_TEAM_SIZE, new Random(seed)).draft(roster);
            view.showRoster(fighters);
            view.showSeed(seed);
            BattleMode mode = new ConsoleMenu(console, out).askBattleMode();
            BattleListener listener = mode == BattleMode.STEP_BY_STEP
                    ? new PausingBattleListener(view, console, out)
                    : view;
            BattleResult result = new BattleService(listener).fight(fighters);
            view.showResult(result);
        } catch (DataLoadingException e) {
            System.err.println("Could not load the characters: " + e.getMessage());
            System.exit(1);
        } catch (InvalidBattleException e) {
            System.err.println("The battle cannot start: " + e.getMessage());
            System.exit(1);
        }
    }
}

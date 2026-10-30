# RPG Battle Simulator

Turn-based console battle: 4 heroes against 4 monsters drawn at random from a pool.
Characters are loaded from a CSV file, the fight runs until one team is wiped out.

## Run

Java 21+ and Maven, from the `rpg-battle` folder:

```bash
mvn compile exec:java                          # default data file
mvn compile exec:java -Dexec.args="--seed 42"  # replay a specific monster draw
mvn test                                       # run the tests
```

At startup you choose **automatic** mode or **step by step** (Enter between rounds).
`NO_COLOR=1` disables colors.

## Features

- Damage = `max(1, attack - defense)`, characters act by speed, draw after 50 rounds.
- Each class has its own behaviour:

| Class | Behaviour |
|---|---|
| Warrior | hits the enemy with the lowest HP |
| Mage | fireball (20 mana, x2), staff hit + mana regen when out of mana |
| Healer | heals the most injured ally (never itself) |
| Skeleton | undead, hits the first enemy |
| Orc | x1.5 damage below 50% HP |
| Vampire | undead, steals half the damage dealt |
| Werewolf | x2 damage on enemies below 50% HP |
| Goblin | targets the lowest defense |
| GiantSpider | venom stacks on the same prey |
| Sandworm | burrows (untargetable), then emerges for x3 damage |

- End of battle: winner, damage ranking, MVP.

## Structure

```
model/      GameCharacter, LivingCharacter, the 10 classes, Healable, Battlefield
loader/     CharacterLoader, CsvCharacterLoader, CharacterFactory
service/    BattleService, BattleResult, MonsterDraft, BattleListener
view/       ConsoleBattleView, ConsoleMenu, PausingBattleListener, Ansi
exception/  custom exceptions
```

## OOP

**Inheritance**: `GameCharacter` (abstract) → `LivingCharacter` (abstract, `Healable`) → Warrior, Mage,
Healer, Orc, Werewolf, Goblin, GiantSpider, Sandworm. `Skeleton` and `Vampire` extend `GameCharacter`
directly, so they can't be healed.

**Polymorphism**: `BattleService` loops over a `List<GameCharacter>` and calls `performAction()`;
each subclass runs its own `act()`.

**Abstract class vs interface**: `GameCharacter` is abstract because it holds shared state (HP, stats)
and code (`takeDamage`). `Healable` is an interface because only some characters can be healed.
`CharacterLoader` and `BattleListener` are interfaces to decouple loading and display.

## Exceptions

- `DataLoadingException`: file missing or empty
- `InvalidCharacterDataException`: bad CSV line (gives the line number)
- `NotEnoughManaException`: checked, caught by `Mage` which falls back to a staff hit
- `CharacterDeadException`: a dead character acts or is healed
- `InvalidBattleException`: fewer than two teams

## Data loading

`src/main/resources/characters.csv` is read at startup (another file can be passed as argument):

```
name;class;team;hp;attack;defense;speed;mana
Merlin;MAGE;HEROES;70;18;3;7;60
```

Each line is validated, then `CharacterFactory` creates the right subclass.

## Technical choices

1. **Randomness only in the monster draw**: battle rules are deterministic, so tests use exact values.
   `MonsterDraft` receives its `Random`, which allows `--seed` and seeded tests.
2. **`performAction()` is final**: it checks the character is alive, then calls `act()`.
3. **Healers can't heal themselves**: otherwise a lone healer could stall the fight forever.

## SOLID

- **S**: loading, creation, battle, display and arguments are in separate classes.
- **O**: the 6 monsters were added without changing `BattleService`; step-by-step mode is a decorator.
- **L**: any subclass works wherever a `GameCharacter` is expected.
- **I**: `Healable` has one method, only for characters that can be healed.
- **D**: `Application` uses `CharacterLoader`, `BattleService` uses `BattleListener`, `MonsterDraft` gets its `Random` injected.

## Tests

77 JUnit tests in `src/test` (character behaviours, battle, CSV loading, draft, arguments), covering
normal cases, edge cases and exceptions. Run with `mvn test`.

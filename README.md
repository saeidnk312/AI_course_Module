# Decimal → Roman Numeral Converter (Java)

A Java program that asks for a whole number and prints it in Roman numerals:

```
Enter an integer: 1653
Decimal 1653 = Roman MDCLIII
```

It can run as a **command-line tool** or as a **Swing window with buttons**.

## Quick start

Requires JDK 17 or newer (developed on OpenJDK 21). No other dependencies.

```bash
./build.sh            # compile, run the tests, build roman.jar
java -jar roman.jar   # interactive prompt (type q to quit)
java -jar roman.jar 1653 2024   # convert arguments and exit
java -jar roman.jar --gui       # open the window with Convert / Clear buttons
```

On Windows without bash:

```bat
mkdir out\main
javac -d out\main src\main\java\roman\*.java
java -cp out\main roman.RomanCli
```

## Project layout

| File | Purpose |
|------|---------|
| `src/main/java/roman/RomanNumeralConverter.java` | Core logic: `toRoman`, `fromRoman`, input parsing and validation |
| `src/main/java/roman/RomanCli.java` | Command-line entry point (interactive, arguments, `--gui`, `--help`) |
| `src/main/java/roman/RomanNumeralOutOfRangeException.java` | Exception thrown for numbers outside 1–3999; reports the rejected value and the limits |
| `src/main/java/roman/RomanGui.java` | Swing GUI: text field, **Convert** and **Clear** buttons, result label |
| `src/test/java/roman/RomanNumeralConverterTest.java` | JUnit 5 suite covering all rules (76 tests), run with `mvn test` |
| `build.sh` | Compile with warnings treated as errors, run the tests, package the jar |

## Error checking

| Input | Behaviour |
|-------|-----------|
| `1653`, ` 1653 `, `+1653` | `Decimal 1653 = Roman MDCLIII` |
| empty line | `Error: Please enter a number.` and the prompt is shown again |
| `abc`, `12.5`, `1,653`, `MDCLIII` | `Error: '…' is not a whole number.` and the prompt is shown again |
| `0`, negative numbers, `4000` and above | `invalid input` |
| the author's birth year (`BIRTH_YEAR`) | `Decimal <year> = Roman SAEID KERMANI` |
| a number too large for an `int` | rejected as not a whole number, with no crash |
| `q` / `quit` / `exit`, or end of input (Ctrl+D) | exits cleanly |
| a bad command-line argument | message on stderr, exit code 1 |
| `--gui` with no display | clear message, exit code 1 (no stack trace) |

**Why 1–3999?** Standard Roman numerals have no zero and no negative numbers. Without overbar notation, the largest numeral you can write is 3999 (`MMMCMXCIX`).

---

## How it was built: the 4 Ds

### a. Delegation: which tools, and how much automation

| Task | Tool | Why |
|------|------|-----|
| Requirements analysis, design, writing the code | AI coding assistant (Claude Code) | Fast to produce complete, idiomatic code with validation already included |
| Compiling | `javac -Xlint:all -Werror` | Treats every compiler warning as a build failure |
| Testing | A plain-Java test class run by `build.sh` | Needs no network or JUnit download, so it runs on any machine with a JDK |
| Packaging | `jar` with a `Main-Class` | Gives a single runnable file, `java -jar roman.jar` |
| Version control | git and GitHub | Keeps the history and backs up the code |

The build is fully automated: one command (`./build.sh`) compiles, tests, and packages. If any test fails, the build stops and nothing is packaged.

The **human** kept these decisions: what the requirements are, what range is acceptable, and whether the finished output is correct.

### b. Description: what each tool was told

- **To the AI assistant:** the assignment text, plus these constraints:
  - finished code, not a sketch
  - full error checking for empty, non-numeric and out-of-range input
  - both a CLI and a GUI option
  - the exact output format `Decimal N = Roman R`
  - no external dependencies
- **To the compiler:** `-Xlint:all -Werror`, so no warning gets through.
- **To the test suite:** the expected values for known cases, including the assignment's own example of 1653 → MDCLIII.

**Design choices made along the way:**

- **Core logic separate from the interfaces.** All conversion and validation is in `RomanNumeralConverter`, so the CLI and the GUI share it and it can be tested without user input.
- **Greedy conversion algorithm.** It uses a value/symbol table that includes the six subtractive pairs (CM, CD, XC, XL, IX, IV).
- **A reverse converter, `fromRoman`.** It was added only to check the results (see Discernment below).

### c. Discernment: how we know it is correct

The output is never trusted just because it compiled. Each kind of check covers a different way the program could be wrong:

1. **Known answers.** 22 hand-checked values are tested, including every subtractive form and both ends of the range (1 → I, 3999 → MMMCMXCIX). The assignment's example, `Decimal 1653 = Roman MDCLIII`, is checked word for word.
2. **An independent second algorithm.** All 3,999 values are compared against a separately written digit-by-digit lookup. A bug in one method would almost never be repeated by a completely different method.
3. **Round trip.** For every n from 1 to 3999, `fromRoman(toRoman(n)) == n`.
4. **Negative tests.** Invalid inputs must be rejected, not converted into something that looks plausible:
   - numbers: 0, negative numbers, 4000, `Integer.MAX_VALUE` and `Integer.MIN_VALUE`
   - text: empty input, letters, decimals, commas, an overflowing number
   - malformed numerals: `IIII`, `IC`, `MMMM`
5. **Manual runs of the real program.** Input was piped into the jar to check the exact on-screen output: valid numbers, invalid numbers, blank lines, `q`, end of input and command-line arguments. Exit codes were also checked: 0 on success, 1 on bad input.
6. **Strict compiler.** The build fails on any warning.

Result: **56 checks passed, 0 failed.**

**Not verified:** the GUI was compiled, and its behaviour with no display was tested, but the window itself was not opened or screenshotted. Check it by hand with `java -jar roman.jar --gui`:

- type `1653` and press **Convert** (or Enter): the result appears in green
- type `abc`: a red error message appears

### d. Diligence: how it was built, step by step

1. Checked the environment: OpenJDK 21 was installed, and the repository was empty.
2. Wrote the core converter class with input validation.
3. Wrote the CLI. It loops until `q` or end of input, and also accepts numbers as arguments.
4. Wrote the Swing GUI. It reuses the same converter, uses the **Convert** button as the default button so Enter works, and shows errors in red.
   - One bug was fixed during review: Enter was wired to both the text field and the default button, so a single press could convert twice. The text field's listener was removed.
5. Wrote the test suite and `build.sh`.
6. Built and ran it: 56 checks passed. Then ran the jar by hand with valid and invalid input (results under Discernment).
7. Wrote this README and committed everything to git.

**Responsibility:** AI tools wrote the code, and a person reviewed it, ran it and accepted it. The tests and this document record exactly what was checked, so anyone can run `./build.sh` and get the same result.

---

## Module 2: new requirements, test-first (TDD)

The JUnit 5 tests are in `src/test/java/roman/RomanNumeralConverterJUnitTest.java`. Run them with `mvn test`.

**Spec 1: bounds limits.**
- Out-of-range input must throw a dedicated `RomanNumeralOutOfRangeException`. This applies to `toRoman` with 0, negative numbers, 4000 and above, and `Integer.MIN_VALUE`/`MAX_VALUE`, and to `parseInput`.
- The exception must extend `IllegalArgumentException`, so existing callers keep working.
- It must expose `getValue()`, `getMin()` and `getMax()`.
- The limits themselves (1 and 3999) must still convert.

**Spec 2: parents' birth years.** Converts each year and checks the result line and the conversion back to a number. The years are placeholders (1945 → MCMXLV and 1963 → MCMLXIII) until they're replaced with the real ones.

**Status: Red.** `mvn test` fails at compile time with `cannot find symbol: class RomanNumeralOutOfRangeException`, because the exception and its handling don't exist yet. In TDD, a test that doesn't compile counts as a failing test. The original self-contained suite (`./build.sh test`) still passes.

**Note:** the Module 1 code already rejected out-of-range numbers with a generic `IllegalArgumentException`, and could already convert any year. So the birth-year tests are expected to pass as soon as the suite compiles. The new behaviour that actually needs code is the specific exception type with its value and limits.

**Status: Green.** The minimal implementation adds one new class, `RomanNumeralOutOfRangeException`, and changes the two existing range checks (in `toRoman` and `parseInput`) to throw it. No other behaviour changed:
- `mvn test`: 20 JUnit tests pass.
- `./build.sh`: the original 56 checks still pass.

**Refactor (tests stay green).** Structure was cleaned up with no change in behaviour:
- **Converter:**
  - A single `requireInRange` / `isInRange` helper replaces the duplicated range checks.
  - The two parallel value/symbol arrays became one table of `Numeral(value, symbol)` records.
  - A redundant position check in `fromRoman` was removed.
- **Shared label:** the CLI and GUI now share one `RANGE_DESCRIPTION` constant ("1-3999").
- **CLI:**
  - A shared `convert` helper now serves both the arguments and interactive modes.
  - The quit words (`q`, `quit`, `exit`) are now one `QUIT_COMMANDS` set.
  - A small `isOnlyFlag` helper handles the `--gui` and `--help` checks.
- **GUI:**
  - Clearer names: `numberField`, `resultLabel`, `inputRow`, `convertButton`, `clearButton`.
  - Separate `clear()` and `showResult()` methods.
  - Named colour constants for success and error.
- **Fix:** case conversion now uses `Locale.ROOT`, so lowercase numerals and the quit words also work on Turkish-locale machines.

After the refactor, all 20 JUnit tests and all 56 original checks still pass, and the CLI output is identical.

---

## Module 3: changed requirements (TDD Red)

**Rule A.** Only 1–3999 is accepted. Anything outside that range outputs exactly `invalid input`.

**Rule B.** The author's birth year outputs `Decimal <year> = Roman SAEID KERMANI` instead of its numeral. The year is the constant `MY_BIRTH_YEAR`, currently the placeholder 1980.

**Tests.** All tests are now in one JUnit suite, `src/test/java/roman/RomanNumeralConverterTest.java`. The old self-contained checks and the Module 2 JUnit file were merged into it, and `build.sh` now runs the tests with `mvn test`.
- The checks over every value from 1 to 3999 skip the birth year on purpose.
- The interactive CLI is tested end to end by feeding it input and capturing what it prints.

**Status: Red.** 76 tests ran and 18 failed.
- All 15 Rule A tests fail. The exception message and the CLI output are still the old "Number must be between…" text.
- 3 of the 5 Rule B tests fail. The birth year still converts to its numeral.
- The 2 remaining Rule B tests pass on purpose. They check that the years next to the birth year, and the parents' years, are not affected.
- All 56 tests of the existing rules pass.

**Status: Green.** The minimal change is 9 lines added and 1 removed, across three files:
- The exception message is now `invalid input`.
- `toRoman` returns `SAEID KERMANI` for `BIRTH_YEAR`, which is the placeholder 1980 until the real year is filled in.
- The interactive CLI prints the out-of-range message on its own.

All 76 tests pass.

**Known gaps, not covered by any test yet:**
- **Argument mode:** prints `Error: invalid input`, not just `invalid input`.
- **GUI:** shows `Error: invalid input`, not just `invalid input`.
- ~~**`fromRoman("MCMLXXX")`:** now rejects the numeral for the birth year.~~ Fixed in the refactor below.

**Refactor (tests stay green).**
- **Conversion algorithm on its own:** the plain conversion is now a private `toNumeral` method. `toRoman` checks the range, then applies the birth-year rule.
- **Simpler loop:** `toNumeral` appends each symbol with `repeat(remaining / value)` and then `remaining %= value`. That's one step per symbol instead of repeated subtraction.
- **Tidier `parseInput`:** `requireInRange` now returns the number it checked, so `parseInput` needs no temporary variable.
- **Named message:** the `"invalid input"` text is now the named constant `RomanNumeralOutOfRangeException.MESSAGE`.
- **One deliberate behaviour change:** `fromRoman` now checks numerals with `toNumeral` rather than `toRoman`, so `fromRoman("MCMLXXX")` returns 1980 again. Before, it was rejected as a side effect of the birth-year rule.

After the refactor, all 76 tests pass and the CLI output is unchanged.

package roman;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Full specification of the Roman numeral converter.
 *
 * <p>New rules (Module 3):
 * <ul>
 *   <li>Rule A: only 1..3999 is accepted; anything outside prints {@code invalid input}.</li>
 *   <li>Rule B: the author's birth year converts to {@code SAEID KERMANI}.</li>
 * </ul>
 * Existing rules (Modules 1 and 2) are kept so a regression shows up here too.
 */
class RomanNumeralConverterTest {

    // TODO: replace with the actual birth year.
    private static final int MY_BIRTH_YEAR = 1980;
    private static final String MY_BIRTH_YEAR_OUTPUT = "SAEID KERMANI";

    // TODO: replace these placeholders with your parents' actual birth years.
    private static final int MOTHER_BIRTH_YEAR = 1945;
    private static final String MOTHER_BIRTH_YEAR_ROMAN = "MCMXLV";
    private static final int FATHER_BIRTH_YEAR = 1963;
    private static final String FATHER_BIRTH_YEAR_ROMAN = "MCMLXIII";

    private static final String INVALID_INPUT = "invalid input";

    // ------------------------------------------------------------------
    // New rule A: bounds -> "invalid input"
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("Rule A: out-of-range input gives 'invalid input'")
    class RuleAInvalidInput {

        @ParameterizedTest(name = "toRoman({0}) -> invalid input")
        @ValueSource(ints = {0, -1, -1653, Integer.MIN_VALUE, 4000, 4001, 10000, Integer.MAX_VALUE})
        void toRomanOutOfRangeReportsInvalidInput(int number) {
            RomanNumeralOutOfRangeException e = assertThrows(
                    RomanNumeralOutOfRangeException.class,
                    () -> RomanNumeralConverter.toRoman(number));
            assertEquals(INVALID_INPUT, e.getMessage());
        }

        @ParameterizedTest(name = "parseInput(\"{0}\") -> invalid input")
        @ValueSource(strings = {"0", "-5", "4000", " 4000 ", "99999"})
        void parseInputOutOfRangeReportsInvalidInput(String text) {
            RomanNumeralOutOfRangeException e = assertThrows(
                    RomanNumeralOutOfRangeException.class,
                    () -> RomanNumeralConverter.parseInput(text));
            assertEquals(INVALID_INPUT, e.getMessage());
        }

        @Test
        void cliPrintsExactlyInvalidInputForOutOfRange() {
            assertEquals(
                    List.of(INVALID_INPUT, INVALID_INPUT, INVALID_INPUT, INVALID_INPUT),
                    runCli("0\n-5\n4000\n10000\n"));
        }

        @Test
        void cliKeepsGoingAfterInvalidInput() {
            assertEquals(
                    List.of(INVALID_INPUT, "Decimal 1653 = Roman MDCLIII"),
                    runCli("4000\n1653\n"));
        }
    }

    // ------------------------------------------------------------------
    // New rule B: author's birth year -> "SAEID KERMANI"
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("Rule B: birth year gives 'SAEID KERMANI'")
    class RuleBBirthYear {

        @Test
        void toRomanReturnsNameForBirthYear() {
            assertEquals(MY_BIRTH_YEAR_OUTPUT, RomanNumeralConverter.toRoman(MY_BIRTH_YEAR));
        }

        @Test
        void resultLineUsesNameForBirthYear() {
            assertEquals("Decimal " + MY_BIRTH_YEAR + " = Roman " + MY_BIRTH_YEAR_OUTPUT,
                    RomanNumeralConverter.formatResult(MY_BIRTH_YEAR));
        }

        @Test
        void cliPrintsNameForBirthYear() {
            assertEquals(
                    List.of("Decimal " + MY_BIRTH_YEAR + " = Roman " + MY_BIRTH_YEAR_OUTPUT),
                    runCli(MY_BIRTH_YEAR + "\n"));
        }

        @Test
        void neighbouringYearsStillConvertNormally() {
            assertAll(
                    () -> assertEquals(oracle(MY_BIRTH_YEAR - 1),
                            RomanNumeralConverter.toRoman(MY_BIRTH_YEAR - 1)),
                    () -> assertEquals(oracle(MY_BIRTH_YEAR + 1),
                            RomanNumeralConverter.toRoman(MY_BIRTH_YEAR + 1)));
        }

        @Test
        void parentsBirthYearsAreNotAffected() {
            assertAll(
                    () -> assertNotEquals(MY_BIRTH_YEAR, MOTHER_BIRTH_YEAR),
                    () -> assertNotEquals(MY_BIRTH_YEAR, FATHER_BIRTH_YEAR));
        }
    }

    // ------------------------------------------------------------------
    // Existing rules (regression)
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("Existing: conversion")
    class Conversion {

        @ParameterizedTest(name = "toRoman({0}) = {1}")
        @CsvSource({
                "1, I", "3, III", "4, IV", "5, V", "9, IX", "10, X", "14, XIV",
                "40, XL", "44, XLIV", "49, XLIX", "90, XC", "99, XCIX", "400, CD",
                "444, CDXLIV", "500, D", "900, CM", "1000, M", "1666, MDCLXVI",
                "1994, MCMXCIV", "2024, MMXXIV", "3888, MMMDCCCLXXXVIII",
                "3999, MMMCMXCIX",
        })
        void knownValues(int number, String expected) {
            assertEquals(expected, RomanNumeralConverter.toRoman(number));
        }

        @Test
        void assignmentExample() {
            assertEquals("Decimal 1653 = Roman MDCLIII", RomanNumeralConverter.formatResult(1653));
        }

        @Test
        void everyValueExceptBirthYearMatchesIndependentOracle() {
            for (int n = 1; n <= 3999; n++) {
                if (n != MY_BIRTH_YEAR) {
                    assertEquals(oracle(n), RomanNumeralConverter.toRoman(n), "toRoman(" + n + ")");
                }
            }
        }

        @Test
        void everyValueExceptBirthYearRoundTrips() {
            for (int n = 1; n <= 3999; n++) {
                if (n != MY_BIRTH_YEAR) {
                    assertEquals(n, RomanNumeralConverter.fromRoman(RomanNumeralConverter.toRoman(n)),
                            "round trip " + n);
                }
            }
        }

        @Test
        void outOfRangeExceptionIsAnIllegalArgumentExceptionWithLimits() {
            IllegalArgumentException e = assertThrows(
                    IllegalArgumentException.class,
                    () -> RomanNumeralConverter.toRoman(4000));
            RomanNumeralOutOfRangeException outOfRange = (RomanNumeralOutOfRangeException) e;
            assertAll(
                    () -> assertEquals(4000, outOfRange.getValue()),
                    () -> assertEquals(1, outOfRange.getMin()),
                    () -> assertEquals(3999, outOfRange.getMax()));
        }
    }

    @Nested
    @DisplayName("Existing: parents' birth years")
    class ParentsBirthYears {

        @Test
        void convertToRoman() {
            assertAll(
                    () -> assertEquals(MOTHER_BIRTH_YEAR_ROMAN,
                            RomanNumeralConverter.toRoman(MOTHER_BIRTH_YEAR)),
                    () -> assertEquals(FATHER_BIRTH_YEAR_ROMAN,
                            RomanNumeralConverter.toRoman(FATHER_BIRTH_YEAR)));
        }

        @Test
        void formatAsResultLine() {
            assertAll(
                    () -> assertEquals("Decimal " + MOTHER_BIRTH_YEAR + " = Roman "
                                    + MOTHER_BIRTH_YEAR_ROMAN,
                            RomanNumeralConverter.formatResult(MOTHER_BIRTH_YEAR)),
                    () -> assertEquals("Decimal " + FATHER_BIRTH_YEAR + " = Roman "
                                    + FATHER_BIRTH_YEAR_ROMAN,
                            RomanNumeralConverter.formatResult(FATHER_BIRTH_YEAR)));
        }

        @Test
        void roundTrip() {
            assertAll(
                    () -> assertEquals(MOTHER_BIRTH_YEAR,
                            RomanNumeralConverter.fromRoman(MOTHER_BIRTH_YEAR_ROMAN)),
                    () -> assertEquals(FATHER_BIRTH_YEAR,
                            RomanNumeralConverter.fromRoman(FATHER_BIRTH_YEAR_ROMAN)));
        }
    }

    @Nested
    @DisplayName("Existing: input parsing")
    class InputParsing {

        @Test
        void acceptsTrimmedAndSignedNumbers() {
            assertAll(
                    () -> assertEquals(1653, RomanNumeralConverter.parseInput("  1653 ")),
                    () -> assertEquals(7, RomanNumeralConverter.parseInput("+7")));
        }

        @ParameterizedTest(name = "parseInput(\"{0}\") is rejected")
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "abc", "12.5", "1,653", "99999999999", "MDCLIII"})
        void rejectsNonNumbers(String text) {
            assertThrows(IllegalArgumentException.class,
                    () -> RomanNumeralConverter.parseInput(text));
        }

        @Test
        void cliKeepsNotAWholeNumberMessage() {
            assertEquals(
                    List.of("Error: 'abc' is not a whole number. Please try again."),
                    runCli("abc\n"));
        }

        @ParameterizedTest(name = "CLI exits on \"{0}\"")
        @ValueSource(strings = {"q", "quit", "exit", "QUIT"})
        void cliQuitCommands(String command) {
            assertEquals(List.of(), runCli(command + "\n1653\n"));
        }
    }

    @Nested
    @DisplayName("Existing: fromRoman")
    class FromRoman {

        @Test
        void acceptsLowercase() {
            assertEquals(1653, RomanNumeralConverter.fromRoman("mdcliii"));
        }

        @ParameterizedTest(name = "fromRoman(\"{0}\") is rejected")
        @NullAndEmptySource
        @ValueSource(strings = {"IIII", "VV", "IC", "IM", "XM", "MMMM", "ABC", "MCMC", "IXI",
                "SAEID KERMANI"})
        void rejectsMalformed(String roman) {
            assertThrows(IllegalArgumentException.class,
                    () -> RomanNumeralConverter.fromRoman(roman));
        }
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;

    @AfterEach
    void restoreStandardStreams() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    /**
     * Runs the interactive CLI with the given input and returns the lines it
     * printed in response, without the banner and prompts.
     */
    private List<String> runCli(String input) {
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        System.setOut(new PrintStream(captured, true, StandardCharsets.UTF_8));
        RomanCli.main(new String[0]);
        System.setOut(originalOut);
        return captured.toString(StandardCharsets.UTF_8)
                .replace("Enter an integer: ", "")
                .lines()
                .skip(1)                 // banner
                .filter(line -> !line.isEmpty())
                .toList();
    }

    /** Independent digit-by-digit algorithm, used to check the real one. */
    private static String oracle(int n) {
        String[] thousands = {"", "M", "MM", "MMM"};
        String[] hundreds = {"", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"};
        String[] tens = {"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};
        String[] ones = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};
        return thousands[n / 1000] + hundreds[n / 100 % 10] + tens[n / 10 % 10] + ones[n % 10];
    }
}

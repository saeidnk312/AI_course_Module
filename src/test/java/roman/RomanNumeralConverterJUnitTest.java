package roman;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Module 2 requirements, written test-first (TDD "Red" step).
 *
 * <p>These tests specify behaviour that does not exist yet:
 * out-of-range input must raise a dedicated
 * {@code RomanNumeralOutOfRangeException} (a subclass of
 * {@link IllegalArgumentException}) that reports the rejected value and the
 * allowed limits.
 */
class RomanNumeralConverterJUnitTest {

    // TODO: replace these placeholders with your parents' actual birth years.
    private static final int MOTHER_BIRTH_YEAR = 1945;
    private static final String MOTHER_BIRTH_YEAR_ROMAN = "MCMXLV";
    private static final int FATHER_BIRTH_YEAR = 1963;
    private static final String FATHER_BIRTH_YEAR_ROMAN = "MCMLXIII";

    @Nested
    @DisplayName("Spec 1: bounds limits")
    class BoundsLimits {

        @ParameterizedTest(name = "toRoman({0}) is rejected as below the lower limit")
        @ValueSource(ints = {0, -1, -1653, Integer.MIN_VALUE})
        void belowLowerLimitThrowsOutOfRange(int number) {
            RomanNumeralOutOfRangeException e = assertThrows(
                    RomanNumeralOutOfRangeException.class,
                    () -> RomanNumeralConverter.toRoman(number));
            assertAll(
                    () -> assertEquals(number, e.getValue()),
                    () -> assertEquals(1, e.getMin()),
                    () -> assertEquals(3999, e.getMax()));
        }

        @ParameterizedTest(name = "toRoman({0}) is rejected as above the upper limit")
        @ValueSource(ints = {4000, 4001, 10000, Integer.MAX_VALUE})
        void aboveUpperLimitThrowsOutOfRange(int number) {
            RomanNumeralOutOfRangeException e = assertThrows(
                    RomanNumeralOutOfRangeException.class,
                    () -> RomanNumeralConverter.toRoman(number));
            assertAll(
                    () -> assertEquals(number, e.getValue()),
                    () -> assertEquals(1, e.getMin()),
                    () -> assertEquals(3999, e.getMax()));
        }

        @Test
        void outOfRangeExceptionIsAnIllegalArgumentException() {
            // Existing callers that catch IllegalArgumentException must keep working.
            IllegalArgumentException e = assertThrows(
                    IllegalArgumentException.class,
                    () -> RomanNumeralConverter.toRoman(4000));
            assertEquals(RomanNumeralOutOfRangeException.class, e.getClass());
        }

        @Test
        void outOfRangeMessageNamesValueAndLimits() {
            RomanNumeralOutOfRangeException e = assertThrows(
                    RomanNumeralOutOfRangeException.class,
                    () -> RomanNumeralConverter.toRoman(0));
            assertEquals("Number must be between 1 and 3999 (got 0).", e.getMessage());
        }

        @ParameterizedTest(name = "parseInput(\"{0}\") is rejected as out of range")
        @ValueSource(strings = {"0", "-5", "4000", " 4000 "})
        void parseInputOutOfRangeThrowsOutOfRange(String text) {
            assertThrows(RomanNumeralOutOfRangeException.class,
                    () -> RomanNumeralConverter.parseInput(text));
        }

        @ParameterizedTest(name = "toRoman({0}) at the limit is accepted")
        @CsvSource({"1, I", "3999, MMMCMXCIX"})
        void limitsThemselvesAreAccepted(int number, String expected) {
            assertEquals(expected, RomanNumeralConverter.toRoman(number));
        }
    }

    @Nested
    @DisplayName("Spec 2: parents' birth years")
    class ParentsBirthYears {

        @Test
        void mothersBirthYear() {
            assertEquals(MOTHER_BIRTH_YEAR_ROMAN,
                    RomanNumeralConverter.toRoman(MOTHER_BIRTH_YEAR));
        }

        @Test
        void fathersBirthYear() {
            assertEquals(FATHER_BIRTH_YEAR_ROMAN,
                    RomanNumeralConverter.toRoman(FATHER_BIRTH_YEAR));
        }

        @Test
        void birthYearsFormatAsResultLine() {
            assertAll(
                    () -> assertEquals("Decimal " + MOTHER_BIRTH_YEAR + " = Roman "
                                    + MOTHER_BIRTH_YEAR_ROMAN,
                            RomanNumeralConverter.formatResult(MOTHER_BIRTH_YEAR)),
                    () -> assertEquals("Decimal " + FATHER_BIRTH_YEAR + " = Roman "
                                    + FATHER_BIRTH_YEAR_ROMAN,
                            RomanNumeralConverter.formatResult(FATHER_BIRTH_YEAR)));
        }

        @Test
        void birthYearsRoundTrip() {
            assertAll(
                    () -> assertEquals(MOTHER_BIRTH_YEAR,
                            RomanNumeralConverter.fromRoman(MOTHER_BIRTH_YEAR_ROMAN)),
                    () -> assertEquals(FATHER_BIRTH_YEAR,
                            RomanNumeralConverter.fromRoman(FATHER_BIRTH_YEAR_ROMAN)));
        }
    }
}

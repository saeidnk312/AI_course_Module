package roman;

import java.util.Locale;

/**
 * Converts between decimal integers and standard Roman numerals.
 *
 * <p>Standard Roman numerals have no symbol for zero or negative numbers and,
 * without overbar notation, cannot represent values above 3999 (MMMCMXCIX).
 * The supported range is therefore 1 to 3999 inclusive.
 */
public final class RomanNumeralConverter {

    public static final int MIN_VALUE = 1;
    public static final int MAX_VALUE = 3999;
    /** Human-readable supported range, e.g. for prompts and labels. */
    public static final String RANGE_DESCRIPTION = MIN_VALUE + "-" + MAX_VALUE;
    /** The author's birth year, which is shown as a name instead of a numeral. */
    static final int BIRTH_YEAR = 1980;
    static final String BIRTH_YEAR_OUTPUT = "SAEID KERMANI";

    private record Numeral(int value, String symbol) {
    }

    // Ordered largest to smallest, including the six subtractive pairs.
    private static final Numeral[] NUMERALS = {
            new Numeral(1000, "M"), new Numeral(900, "CM"),
            new Numeral(500, "D"), new Numeral(400, "CD"),
            new Numeral(100, "C"), new Numeral(90, "XC"),
            new Numeral(50, "L"), new Numeral(40, "XL"),
            new Numeral(10, "X"), new Numeral(9, "IX"),
            new Numeral(5, "V"), new Numeral(4, "IV"),
            new Numeral(1, "I"),
    };

    private RomanNumeralConverter() {
    }

    /**
     * Converts a decimal integer to a Roman numeral.
     *
     * @throws RomanNumeralOutOfRangeException if {@code number} is outside 1..3999
     */
    public static String toRoman(int number) {
        requireInRange(number);
        return number == BIRTH_YEAR ? BIRTH_YEAR_OUTPUT : toNumeral(number);
    }

    /**
     * Converts a Roman numeral back to a decimal integer. Only canonical
     * numerals are accepted, so malformed input such as "IIII" or "IC" is
     * rejected.
     *
     * @throws IllegalArgumentException if {@code roman} is not a valid numeral
     */
    public static int fromRoman(String roman) {
        if (roman == null || roman.isBlank()) {
            throw new IllegalArgumentException("Roman numeral must not be empty.");
        }
        String numeralText = roman.trim().toUpperCase(Locale.ROOT);
        int total = 0;
        int position = 0;
        for (Numeral numeral : NUMERALS) {
            while (numeralText.startsWith(numeral.symbol(), position)) {
                total += numeral.value();
                position += numeral.symbol().length();
            }
        }
        // Any leftover or non-canonical text makes the round trip differ.
        if (!isInRange(total) || !toNumeral(total).equals(numeralText)) {
            throw new IllegalArgumentException("Invalid Roman numeral: " + roman);
        }
        return total;
    }

    /**
     * Parses user-supplied text as a whole number in the supported range.
     *
     * @throws IllegalArgumentException with a user-friendly message if the
     *         text is empty or not an integer
     * @throws RomanNumeralOutOfRangeException if the number is out of range
     */
    public static int parseInput(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Please enter a number.");
        }
        String numberText = text.trim();
        try {
            return requireInRange(Integer.parseInt(numberText));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "'" + numberText + "' is not a whole number.");
        }
    }

    /** Formats the result line, e.g. {@code Decimal 1653 = Roman MDCLIII}. */
    public static String formatResult(int number) {
        return "Decimal " + number + " = Roman " + toRoman(number);
    }

    /** Standard greedy conversion; assumes {@code number} is in range. */
    private static String toNumeral(int number) {
        StringBuilder roman = new StringBuilder();
        int remaining = number;
        for (Numeral numeral : NUMERALS) {
            roman.append(numeral.symbol().repeat(remaining / numeral.value()));
            remaining %= numeral.value();
        }
        return roman.toString();
    }

    private static boolean isInRange(int number) {
        return number >= MIN_VALUE && number <= MAX_VALUE;
    }

    private static int requireInRange(int number) {
        if (!isInRange(number)) {
            throw new RomanNumeralOutOfRangeException(number, MIN_VALUE, MAX_VALUE);
        }
        return number;
    }
}

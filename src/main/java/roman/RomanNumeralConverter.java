package roman;

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

    // Ordered largest to smallest, including the six subtractive pairs.
    private static final int[] VALUES =
            {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
    private static final String[] SYMBOLS =
            {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

    private RomanNumeralConverter() {
    }

    /**
     * Converts a decimal integer to a Roman numeral.
     *
     * @throws RomanNumeralOutOfRangeException if {@code number} is outside 1..3999
     */
    public static String toRoman(int number) {
        if (number < MIN_VALUE || number > MAX_VALUE) {
            throw new RomanNumeralOutOfRangeException(number, MIN_VALUE, MAX_VALUE);
        }
        StringBuilder roman = new StringBuilder();
        int remaining = number;
        for (int i = 0; i < VALUES.length; i++) {
            while (remaining >= VALUES[i]) {
                roman.append(SYMBOLS[i]);
                remaining -= VALUES[i];
            }
        }
        return roman.toString();
    }

    /**
     * Converts a Roman numeral back to a decimal integer. Only canonical
     * numerals (the exact form {@link #toRoman(int)} produces) are accepted,
     * so malformed input such as "IIII" or "IC" is rejected.
     *
     * @throws IllegalArgumentException if {@code roman} is not a valid numeral
     */
    public static int fromRoman(String roman) {
        if (roman == null || roman.isBlank()) {
            throw new IllegalArgumentException("Roman numeral must not be empty.");
        }
        String upper = roman.trim().toUpperCase();
        int total = 0;
        int pos = 0;
        for (int i = 0; i < VALUES.length && pos < upper.length(); i++) {
            while (upper.startsWith(SYMBOLS[i], pos)) {
                total += VALUES[i];
                pos += SYMBOLS[i].length();
            }
        }
        if (pos != upper.length() || total < MIN_VALUE || total > MAX_VALUE
                || !toRoman(total).equals(upper)) {
            throw new IllegalArgumentException("Invalid Roman numeral: " + roman);
        }
        return total;
    }

    /**
     * Parses user-supplied text as a whole number in the supported range.
     *
     * @throws IllegalArgumentException with a user-friendly message if the
     *         text is empty, not an integer, or out of range
     */
    public static int parseInput(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Please enter a number.");
        }
        String trimmed = text.trim();
        int number;
        try {
            number = Integer.parseInt(trimmed);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "'" + trimmed + "' is not a whole number.");
        }
        if (number < MIN_VALUE || number > MAX_VALUE) {
            throw new RomanNumeralOutOfRangeException(number, MIN_VALUE, MAX_VALUE);
        }
        return number;
    }

    /** Formats the result line, e.g. {@code Decimal 1653 = Roman MDCLIII}. */
    public static String formatResult(int number) {
        return "Decimal " + number + " = Roman " + toRoman(number);
    }
}

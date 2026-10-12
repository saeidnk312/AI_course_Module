package roman;

/**
 * Dependency-free test suite (no JUnit needed). Run via ./build.sh test.
 * Exits non-zero if any check fails.
 */
public final class RomanNumeralConverterTest {

    private static int passed;
    private static int failed;

    public static void main(String[] args) {
        knownValues();
        requiredExample();
        exhaustiveAgainstIndependentOracle();
        roundTripWholeRange();
        rejectsOutOfRange();
        parseInputValidation();
        fromRomanRejectsMalformed();

        System.out.println(passed + " checks passed, " + failed + " failed.");
        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void knownValues() {
        Object[][] cases = {
                {1, "I"}, {3, "III"}, {4, "IV"}, {5, "V"}, {9, "IX"}, {10, "X"},
                {14, "XIV"}, {40, "XL"}, {44, "XLIV"}, {49, "XLIX"}, {90, "XC"},
                {99, "XCIX"}, {400, "CD"}, {444, "CDXLIV"}, {500, "D"},
                {900, "CM"}, {1000, "M"}, {1666, "MDCLXVI"}, {1994, "MCMXCIV"},
                {2024, "MMXXIV"}, {3888, "MMMDCCCLXXXVIII"}, {3999, "MMMCMXCIX"},
        };
        for (Object[] c : cases) {
            check("toRoman(" + c[0] + ")", c[1],
                    RomanNumeralConverter.toRoman((Integer) c[0]));
        }
    }

    private static void requiredExample() {
        check("assignment example", "Decimal 1653 = Roman MDCLIII",
                RomanNumeralConverter.formatResult(1653));
    }

    /**
     * Compares every value 1..3999 against a second, independently written
     * algorithm (digit-by-digit lookup tables) so a bug in the greedy
     * algorithm can't hide behind a matching bug in the test.
     */
    private static void exhaustiveAgainstIndependentOracle() {
        String[] thousands = {"", "M", "MM", "MMM"};
        String[] hundreds = {"", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"};
        String[] tens = {"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};
        String[] ones = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};
        int mismatches = 0;
        for (int n = 1; n <= 3999; n++) {
            String expected = thousands[n / 1000] + hundreds[n / 100 % 10]
                    + tens[n / 10 % 10] + ones[n % 10];
            if (!expected.equals(RomanNumeralConverter.toRoman(n))) {
                mismatches++;
            }
        }
        check("all 3999 values match oracle", 0, mismatches);
    }

    private static void roundTripWholeRange() {
        int mismatches = 0;
        for (int n = 1; n <= 3999; n++) {
            if (RomanNumeralConverter.fromRoman(RomanNumeralConverter.toRoman(n)) != n) {
                mismatches++;
            }
        }
        check("fromRoman(toRoman(n)) == n for 1..3999", 0, mismatches);
    }

    private static void rejectsOutOfRange() {
        for (int n : new int[] {0, -1, -1653, 4000, Integer.MAX_VALUE, Integer.MIN_VALUE}) {
            final int value = n;
            expectThrows("toRoman(" + n + ")", () -> RomanNumeralConverter.toRoman(value));
        }
    }

    private static void parseInputValidation() {
        check("parseInput trims", 1653, RomanNumeralConverter.parseInput("  1653 "));
        check("parseInput +sign", 7, RomanNumeralConverter.parseInput("+7"));
        for (String bad : new String[] {null, "", "   ", "abc", "12.5", "1,653",
                "0", "-5", "4000", "99999999999", "MDCLIII"}) {
            expectThrows("parseInput(" + bad + ")",
                    () -> RomanNumeralConverter.parseInput(bad));
        }
    }

    private static void fromRomanRejectsMalformed() {
        check("fromRoman lowercase", 1653, RomanNumeralConverter.fromRoman("mdcliii"));
        for (String bad : new String[] {null, "", "IIII", "VV", "IC", "IM", "XM",
                "MMMM", "ABC", "MCMC", "IXI"}) {
            expectThrows("fromRoman(" + bad + ")",
                    () -> RomanNumeralConverter.fromRoman(bad));
        }
    }

    private static void check(String name, Object expected, Object actual) {
        if (expected.equals(actual)) {
            passed++;
        } else {
            failed++;
            System.out.println("FAIL " + name + ": expected " + expected + ", got " + actual);
        }
    }

    private static void expectThrows(String name, Runnable action) {
        try {
            action.run();
            failed++;
            System.out.println("FAIL " + name + ": expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            passed++;
        }
    }
}

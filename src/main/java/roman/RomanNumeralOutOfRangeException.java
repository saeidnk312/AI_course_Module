package roman;

/** Thrown when a number is outside the range Roman numerals can represent. */
public class RomanNumeralOutOfRangeException extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    /** The message shown to the user for any out-of-range number. */
    public static final String MESSAGE = "invalid input";

    private final int value;
    private final int min;
    private final int max;

    public RomanNumeralOutOfRangeException(int value, int min, int max) {
        super(MESSAGE);
        this.value = value;
        this.min = min;
        this.max = max;
    }

    public int getValue() {
        return value;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }
}

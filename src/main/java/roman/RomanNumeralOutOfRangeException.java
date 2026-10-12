package roman;

/** Thrown when a number is outside the range Roman numerals can represent. */
public class RomanNumeralOutOfRangeException extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    private final int value;
    private final int min;
    private final int max;

    public RomanNumeralOutOfRangeException(int value, int min, int max) {
        super("invalid input");
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

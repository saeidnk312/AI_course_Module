package roman;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Set;

/**
 * Command-line entry point.
 *
 * <pre>
 *   java -cp out roman.RomanCli            # interactive prompt
 *   java -cp out roman.RomanCli 1653 42    # convert arguments and exit
 *   java -cp out roman.RomanCli --gui      # open the Swing window
 * </pre>
 */
public final class RomanCli {

    private static final Set<String> QUIT_COMMANDS = Set.of("q", "quit", "exit");

    private RomanCli() {
    }

    public static void main(String[] args) {
        if (isOnlyFlag(args, "--gui", "-g")) {
            RomanGui.launch();
            return;
        }
        if (isOnlyFlag(args, "--help", "-h")) {
            printUsage(System.out);
            return;
        }
        int exitCode = args.length > 0 ? convertArguments(args) : runInteractive();
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }

    private static boolean isOnlyFlag(String[] args, String longFlag, String shortFlag) {
        return args.length == 1 && (args[0].equals(longFlag) || args[0].equals(shortFlag));
    }

    /** Converts each argument; returns 1 if any argument was invalid. */
    private static int convertArguments(String[] args) {
        int exitCode = 0;
        for (String arg : args) {
            try {
                System.out.println(convert(arg));
            } catch (IllegalArgumentException e) {
                System.err.println("Error: " + e.getMessage());
                exitCode = 1;
            }
        }
        return exitCode;
    }

    /** Parses and converts one input, returning the formatted result line. */
    private static String convert(String text) {
        return RomanNumeralConverter.formatResult(RomanNumeralConverter.parseInput(text));
    }

    /** Prompts repeatedly until the user types 'q' or input ends. */
    private static int runInteractive() {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8));
        System.out.println("Decimal to Roman numeral converter ("
                + RomanNumeralConverter.RANGE_DESCRIPTION + "). Type 'q' to quit.");
        while (true) {
            System.out.print("Enter an integer: ");
            System.out.flush();
            String line;
            try {
                line = reader.readLine();
            } catch (IOException e) {
                System.err.println("Error reading input: " + e.getMessage());
                return 1;
            }
            if (line == null) {          // EOF (Ctrl+D / end of piped input)
                System.out.println();
                return 0;
            }
            if (QUIT_COMMANDS.contains(line.trim().toLowerCase(Locale.ROOT))) {
                return 0;
            }
            try {
                System.out.println(convert(line));
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage() + " Please try again.");
            }
        }
    }

    private static void printUsage(PrintStream out) {
        out.println("Usage: java -cp out roman.RomanCli [--gui | --help | NUMBER...]");
        out.println("  (no args)  prompt for numbers interactively");
        out.println("  NUMBER...  convert each number and exit");
        out.println("  --gui      open the graphical converter");
    }
}

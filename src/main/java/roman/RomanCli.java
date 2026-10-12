package roman;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

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

    private RomanCli() {
    }

    public static void main(String[] args) {
        if (args.length == 1 && (args[0].equals("--gui") || args[0].equals("-g"))) {
            RomanGui.launch();
            return;
        }
        if (args.length == 1 && (args[0].equals("--help") || args[0].equals("-h"))) {
            printUsage(System.out);
            return;
        }
        int exitCode = args.length > 0 ? convertArgs(args) : interactive();
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }

    /** Converts each argument; returns 1 if any argument was invalid. */
    private static int convertArgs(String[] args) {
        int exitCode = 0;
        for (String arg : args) {
            try {
                System.out.println(RomanNumeralConverter.formatResult(
                        RomanNumeralConverter.parseInput(arg)));
            } catch (IllegalArgumentException e) {
                System.err.println("Error: " + e.getMessage());
                exitCode = 1;
            }
        }
        return exitCode;
    }

    /** Prompts repeatedly until the user types 'q' or input ends. */
    private static int interactive() {
        BufferedReader in = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8));
        System.out.println("Decimal to Roman numeral converter ("
                + RomanNumeralConverter.MIN_VALUE + "-"
                + RomanNumeralConverter.MAX_VALUE + "). Type 'q' to quit.");
        while (true) {
            System.out.print("Enter an integer: ");
            System.out.flush();
            String line;
            try {
                line = in.readLine();
            } catch (IOException e) {
                System.err.println("Error reading input: " + e.getMessage());
                return 1;
            }
            if (line == null) {          // EOF (Ctrl+D / end of piped input)
                System.out.println();
                return 0;
            }
            String trimmed = line.trim();
            if (trimmed.equalsIgnoreCase("q") || trimmed.equalsIgnoreCase("quit")
                    || trimmed.equalsIgnoreCase("exit")) {
                return 0;
            }
            try {
                int number = RomanNumeralConverter.parseInput(trimmed);
                System.out.println(RomanNumeralConverter.formatResult(number));
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

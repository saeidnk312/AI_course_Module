package roman;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/** Simple Swing window: a text field, Convert/Clear buttons and a result label. */
public final class RomanGui {

    private static final Color SUCCESS_COLOR = new Color(0, 110, 0);
    private static final Color ERROR_COLOR = Color.RED;

    private final JTextField numberField = new JTextField(10);
    private final JLabel resultLabel = new JLabel(" ");

    private RomanGui() {
    }

    public static void launch() {
        if (GraphicsEnvironment.isHeadless()) {
            System.err.println("Error: no display available for the GUI. "
                    + "Run without --gui to use the command-line version.");
            System.exit(1);
        }
        SwingUtilities.invokeLater(() -> new RomanGui().show());
    }

    public static void main(String[] args) {
        launch();
    }

    private void show() {
        JFrame frame = new JFrame("Decimal to Roman Converter");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JButton convertButton = new JButton("Convert");
        JButton clearButton = new JButton("Clear");
        convertButton.addActionListener(e -> convert());
        clearButton.addActionListener(e -> clear());

        JPanel inputRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputRow.add(new JLabel("Integer (" + RomanNumeralConverter.RANGE_DESCRIPTION + "):"));
        inputRow.add(numberField);
        inputRow.add(convertButton);
        inputRow.add(clearButton);

        resultLabel.setFont(resultLabel.getFont().deriveFont(Font.BOLD, 16f));
        resultLabel.setBorder(BorderFactory.createEmptyBorder(8, 10, 10, 10));

        frame.add(inputRow, BorderLayout.NORTH);
        frame.add(resultLabel, BorderLayout.CENTER);
        frame.getRootPane().setDefaultButton(convertButton);   // Enter key converts
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void convert() {
        try {
            int number = RomanNumeralConverter.parseInput(numberField.getText());
            showResult(RomanNumeralConverter.formatResult(number), SUCCESS_COLOR);
        } catch (IllegalArgumentException e) {
            showResult("Error: " + e.getMessage(), ERROR_COLOR);
        }
        numberField.selectAll();
        numberField.requestFocusInWindow();
    }

    private void clear() {
        numberField.setText("");
        resultLabel.setText(" ");
        numberField.requestFocusInWindow();
    }

    private void showResult(String text, Color color) {
        resultLabel.setForeground(color);
        resultLabel.setText(text);
    }
}

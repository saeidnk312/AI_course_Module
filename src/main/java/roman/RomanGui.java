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

    private final JTextField input = new JTextField(10);
    private final JLabel result = new JLabel(" ");

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

        JButton convert = new JButton("Convert");
        JButton clear = new JButton("Clear");
        convert.addActionListener(e -> convert());
        clear.addActionListener(e -> {
            input.setText("");
            result.setText(" ");
            input.requestFocusInWindow();
        });

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Integer (" + RomanNumeralConverter.MIN_VALUE + "-"
                + RomanNumeralConverter.MAX_VALUE + "):"));
        top.add(input);
        top.add(convert);
        top.add(clear);

        result.setFont(result.getFont().deriveFont(Font.BOLD, 16f));
        result.setBorder(BorderFactory.createEmptyBorder(8, 10, 10, 10));

        frame.add(top, BorderLayout.NORTH);
        frame.add(result, BorderLayout.CENTER);
        frame.getRootPane().setDefaultButton(convert);   // Enter key converts
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void convert() {
        try {
            int number = RomanNumeralConverter.parseInput(input.getText());
            result.setForeground(new Color(0, 110, 0));
            result.setText(RomanNumeralConverter.formatResult(number));
        } catch (IllegalArgumentException e) {
            result.setForeground(Color.RED);
            result.setText("Error: " + e.getMessage());
        }
        input.selectAll();
        input.requestFocusInWindow();
    }
}

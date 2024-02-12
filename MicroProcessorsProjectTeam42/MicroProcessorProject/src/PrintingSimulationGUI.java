import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;

public class PrintingSimulationGUI extends JFrame {
    private JTextArea consoleTextArea;
    private JTextField inputField;
    private PrintStream consolePrintStream;
    private BufferedReader consoleReader;
    private String userInput;

    public PrintingSimulationGUI() {
        setTitle("Printing Simulation");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Add window listener to handle maximizing the frame
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                setExtendedState(JFrame.MAXIMIZED_BOTH);
            }
        });

        // Set the preferred size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setPreferredSize(screenSize);

        // Set undecorated to false for exit buttons
        setUndecorated(false);

        initComponents();
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding

        consoleTextArea = new JTextArea();
        consoleTextArea.setEditable(false);
        consoleTextArea.setFont(new Font("Consolas", Font.PLAIN, 16)); // Change font
        consoleTextArea.setBackground(new Color(245, 245, 245)); // Set a light background
        JScrollPane scrollPane = new JScrollPane(consoleTextArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY)); // Add border to scroll pane
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);

        // Redirect System.out to update the GUI
        consolePrintStream = new PrintStream(new CustomOutputStream(consoleTextArea));
        System.setOut(consolePrintStream);
    }



    public String getUserInput(String msg) {
        return showInputDialog(msg);
    }


    // Custom OutputStream implementation that updates the GUI component
    private class CustomOutputStream extends OutputStream {
        private JTextArea textArea;

        public CustomOutputStream(JTextArea textArea) {
            this.textArea = textArea;
        }

        @Override
        public void write(int b) {
            // Append the byte value to the text area
            textArea.append(String.valueOf((char) b));
            // Scroll to the bottom of the text area
            textArea.setCaretPosition(textArea.getDocument().getLength());
        }
    }


    private String showInputDialog(String message) {
        JDialog dialog = new JDialog(this, "Input Required", true);
        dialog.setLayout(new FlowLayout());

        JLabel label = new JLabel(message);
        JTextField textField = new JTextField(20);
        JButton button = createStyledButton("Submit");
        button.addActionListener(e -> dialog.dispose());

        dialog.add(label);
        dialog.add(textField);
        dialog.add(button);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        return textField.getText();
    }


    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);

        // Font and Size
        button.setFont(new Font("Arial", Font.BOLD, 14));

        // Colors and Borders
        button.setBackground(new Color(70, 130, 180)); // e.g., Steel Blue
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15)); // Padding
        button.setFocusPainted(false);

        // Rounded Corners
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);

        // Hover Effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(100, 149, 237)); // Lighter shade when hovered
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(70, 130, 180)); // Original color
            }
        });

        return button;
    }





    public static void main(String[] args) {
//        PrintingSimulationGUI p = new PrintingSimulationGUI();
//        Scheduler.schedule();
    }

    // ...
}

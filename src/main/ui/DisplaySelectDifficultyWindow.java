package ui;
import javax.swing.*;


public class DisplaySelectDifficultyWindow {
    public static void main(String[] args) {
        // Create a new JFrame
        JFrame frame = new JFrame("Select Difficulty");

        // Create a new JPanel
        JPanel panel = new JPanel();

        // Create a new JLabel
        JLabel label = new JLabel("Select Difficulty:");

        // Create a new JComboBox
        String[] difficulties = {"Easy", "Medium", "Hard"};
        JComboBox<String> comboBox = new JComboBox<>(difficulties);

        // Add the label and combo box to the panel
        panel.add(label);
        panel.add(comboBox);

        // Add the panel to the frame
        frame.add(panel);

        // Set the size of the frame
        frame.setSize(300, 100);

        // Set the frame to be visible
        frame.setVisible(true);
    }
}

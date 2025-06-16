package chitchat;

import javax.swing.SwingUtilities;

/**
 * Main class for the ChitChat application.
 * It launches the graphical user interface.
 * * @author Kitso Litelu (Updated by me for Finale)
 * @version 2025-06-13
 */
public class ChitChat {

    public static void main(String[] args) {
        // Run the GUI on the Event Dispatch Thread for thread safety
        SwingUtilities.invokeLater(() -> {
            // Create and show the Login UI, which is the new entry point
            new LoginUI().setVisible(true);
        });
    }
}

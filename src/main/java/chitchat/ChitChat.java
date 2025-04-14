package chitchat;

import javax.swing.JOptionPane;

public class ChitChat 
{
    public static void main(String[] args) {
        // Display welcome message
        JOptionPane.showMessageDialog(null, "Welcome to ChitChat");

        // Collect registration details
        String username = JOptionPane.showInputDialog("Enter your username:");
        String password = JOptionPane.showInputDialog("Enter your password:");
        String cellphone = JOptionPane.showInputDialog("Enter your cellphone number:");
        String firstName = JOptionPane.showInputDialog("Enter your first name:");
        String lastName = JOptionPane.showInputDialog("Enter your last name:");

        // Create RegistrationLogin instance and attempt registration
        RegistrationLogin registrationLoginFeature = new RegistrationLogin();
        String feedback = registrationLoginFeature.registerUser(username, password, cellphone, firstName, lastName);
        JOptionPane.showMessageDialog(null, feedback);

        // Proceed to login if registration was successful
        if (registrationLoginFeature.isRegistered()) {
            String loginUsername = JOptionPane.showInputDialog("Enter your username to log in:");
            String loginPassword = JOptionPane.showInputDialog("Enter your password to log in:");
            registrationLoginFeature.loginUser(loginUsername, loginPassword);
            String loginStatus = registrationLoginFeature.returnLoginStatus();
            JOptionPane.showMessageDialog(null, loginStatus);
        }

        // Display final message
        JOptionPane.showMessageDialog(null, "Messaging feature coming soon");
    }
}
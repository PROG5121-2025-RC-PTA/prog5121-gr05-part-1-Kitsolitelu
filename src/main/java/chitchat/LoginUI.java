package chitchat;

import javax.swing.*;
import java.awt.*;

/**
 * A JFrame-based user interface for registration and login.
 * This is the main entry point of the graphical application.
 * I have fixed a bug here that prevented registration from working.
 *
 * @author Kitso Litelu (Created and fixed by me for Finale)
 * @version 2025-06-13
 */
public class LoginUI extends JFrame {

    // A me for Finale-inspired color palette
    private static final Color PRIMARY_COLOR = new Color(26, 28, 59); // Deep Blue
    private static final Color SECONDARY_COLOR = new Color(80, 101, 218); // Brighter Blue
    private static final Color TEXT_COLOR = new Color(230, 230, 255); // Light Lavender
    private static final Color FIELD_BG_COLOR = new Color(40, 43, 84);

    private UserManager userManager;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    
    // Fields for Login Panel
    private JTextField loginUsernameField;
    private JPasswordField loginPasswordField;
    
    // Fields for Register Panel
    private JTextField regFirstNameField, regLastNameField, regCellNumberField, regUsernameField;
    private JPasswordField regPasswordField;

    private JTextArea feedbackArea;

    public LoginUI() {
        super("ChitChat - Welcome");
        this.userManager = new UserManager();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 720);
        //setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(PRIMARY_COLOR);

        JPanel loginPanel = createLoginPanel();
        JPanel registerPanel = createRegisterPanel();

        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(registerPanel, "REGISTER");

        add(mainPanel);
        cardLayout.show(mainPanel, "LOGIN");
    }
    
    private JPanel createLoginPanel() {
        JPanel panel = createStyledPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = createGBC();

        addTitle(panel, gbc, "ChitChat Login");

        // Username
        gbc.gridy++;
        panel.add(createStyledLabel("Username:"), gbc);
        gbc.gridx = 1;
        loginUsernameField = createStyledTextField();
        panel.add(loginUsernameField, gbc);

        // Password
        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(createStyledLabel("Password:"), gbc);
        gbc.gridx = 1;
        loginPasswordField = createStyledPasswordField();
        panel.add(loginPasswordField, gbc);
        
        // Feedback Area
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        feedbackArea = createFeedbackArea();
        panel.add(new JScrollPane(feedbackArea), gbc);

        // Buttons
        gbc.gridy++;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton loginButton = createStyledButton("Login");
        panel.add(loginButton, gbc);

        gbc.gridy++;
        JButton switchToRegisterButton = createLinkButton("Don't have an account? Register");
        panel.add(switchToRegisterButton, gbc);

        // --- Action Listeners ---
        loginButton.addActionListener(e -> handleLogin());
        switchToRegisterButton.addActionListener(e -> cardLayout.show(mainPanel, "REGISTER"));

        return panel;
    }

    private JPanel createRegisterPanel() {
        JPanel panel = createStyledPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = createGBC();
        
        addTitle(panel, gbc, "Create Account");
        
        gbc.gridy++;
        panel.add(createStyledLabel("First Name:"), gbc);
        gbc.gridx = 1;
        regFirstNameField = createStyledTextField();
        panel.add(regFirstNameField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(createStyledLabel("Last Name:"), gbc);
        gbc.gridx = 1;
        regLastNameField = createStyledTextField();
        panel.add(regLastNameField, gbc);
        
        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(createStyledLabel("Cellphone (+27...):"), gbc);
        gbc.gridx = 1;
        regCellNumberField = createStyledTextField();
        panel.add(regCellNumberField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(createStyledLabel("Username (e.g. user_1):"), gbc);
        gbc.gridx = 1;
        regUsernameField = createStyledTextField();
        panel.add(regUsernameField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(createStyledLabel("Password:"), gbc);
        gbc.gridx = 1;
        regPasswordField = createStyledPasswordField();
        panel.add(regPasswordField, gbc);
        
        // Buttons
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton registerButton = createStyledButton("Register");
        panel.add(registerButton, gbc);

        gbc.gridy++;
        JButton switchToLoginButton = createLinkButton("Already have an account? Login");
        panel.add(switchToLoginButton, gbc);

        // --- Action Listeners ---
        registerButton.addActionListener(e -> handleRegister());
        switchToLoginButton.addActionListener(e -> cardLayout.show(mainPanel, "LOGIN"));
        
        return panel;
    }
    
    private void handleLogin() {
        String username = loginUsernameField.getText();
        String password = new String(loginPasswordField.getPassword());
        RegistrationLogin user = userManager.findUser(username);

        if (user != null && user.loginUser(username, password)) {
            SwingUtilities.invokeLater(() -> {
                new ChatUI(user).setVisible(true);
                this.dispose();
            });
        } else {
            feedbackArea.setForeground(Color.ORANGE);
            feedbackArea.setText("Login failed: Invalid username or password.");
        }
    }
    
    private void handleRegister() {
        String firstName = regFirstNameField.getText();
        String lastName = regLastNameField.getText();
        String cell = regCellNumberField.getText();
        String username = regUsernameField.getText();
        String password = new String(regPasswordField.getPassword());

        RegistrationLogin newUser = new RegistrationLogin();
        String feedback = userManager.registerUser(newUser, username, password, cell, firstName, lastName);
        
        feedbackArea.setText(feedback.replace("\n", " ").trim());
        
        if (feedback.contains("Registration successful")) {
            feedbackArea.setForeground(Color.GREEN);
            cardLayout.show(mainPanel, "LOGIN");
        } else {
            feedbackArea.setForeground(Color.ORANGE);
        }
    }
    
    // --- Helper methods for consistent styling ---
    private GridBagConstraints createGBC() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 15, 8, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        return gbc;
    }
    
    private void addTitle(JPanel panel, GridBagConstraints gbc, String title) {
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_COLOR);
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.ipady = 20;
        panel.add(titleLabel, gbc);
        gbc.gridwidth = 1;
        gbc.ipady = 0;
    }

    private JPanel createStyledPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(PRIMARY_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        return panel;
    }
    
    private JTextArea createFeedbackArea() {
        JTextArea area = new JTextArea(3, 20);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setForeground(Color.ORANGE);
        area.setBackground(FIELD_BG_COLOR);
        area.setFont(new Font("SansSerif", Font.PLAIN, 12));
        area.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        return area;
    }
    
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setForeground(TEXT_COLOR);
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        field.setBackground(FIELD_BG_COLOR);
        field.setForeground(TEXT_COLOR);
        field.setCaretColor(TEXT_COLOR);
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, SECONDARY_COLOR),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField(20);
        field.setBackground(FIELD_BG_COLOR);
        field.setForeground(TEXT_COLOR);
        field.setCaretColor(TEXT_COLOR);
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, SECONDARY_COLOR),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return field;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setBackground(SECONDARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    private JButton createLinkButton(String text) {
        JButton button = new JButton(text);
        button.setForeground(SECONDARY_COLOR);
        button.setBackground(PRIMARY_COLOR);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFont(new Font("SansSerif", Font.PLAIN, 12));
        return button;
    }
}

package chitchat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

/**
 * The main chat interface, shown after a user successfully logs in.
 * From here, users can send messages and open the reports window.
 *
 * @author Kitso Litelu (Created by me for Finale)
 * @version 2025-06-13
 */
public class ChatUI extends JFrame {
    
    // Using the same me for Finale-inspired color palette
    private static final Color PRIMARY_COLOR = new Color(26, 28, 59);
    private static final Color SECONDARY_COLOR = new Color(80, 101, 218);
    private static final Color TEXT_COLOR = new Color(230, 230, 255);
    private static final Color FIELD_BG_COLOR = new Color(40, 43, 84);

    private RegistrationLogin currentUser;
    private ArrayList<Message> allMessages;

    private JTextField recipientField;
    private JTextArea payloadArea;
    private JLabel feedbackLabel;

    public ChatUI(RegistrationLogin user) {
        super("ChitChat - Messenger");
        this.currentUser = user;
        
        // Load all messages from the JSON file on startup
        this.allMessages = MessageManager.loadAllMessages();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 720);
        setLocationRelativeTo(null);
        
        // Add a window listener to save messages when the user closes the app
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                MessageManager.saveAllMessages(allMessages);
                System.out.println("All messages saved. Exiting.");
            }
        });

        // --- UI Components ---
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(PRIMARY_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getFirstName() + "!", SwingConstants.LEFT);
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        welcomeLabel.setForeground(TEXT_COLOR);
        
        JButton logoutButton = new JButton("Logout");
        styleSecondaryButton(logoutButton);
        logoutButton.addActionListener(e -> handleLogout());
        
        headerPanel.add(welcomeLabel, BorderLayout.CENTER);
        headerPanel.add(logoutButton, BorderLayout.EAST);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Center Panel for sending messages
        JPanel messagePanel = new JPanel();
        messagePanel.setOpaque(false);
        messagePanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        messagePanel.add(createStyledLabel("Recipient's Cell (+27...):"), gbc);
        
        gbc.gridx = 1;
        recipientField = createStyledTextField();
        messagePanel.add(recipientField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.NORTH;
        messagePanel.add(createStyledLabel("Message (max 250 chars):"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        payloadArea = new JTextArea(10, 30);
        payloadArea.setBackground(FIELD_BG_COLOR);
        payloadArea.setForeground(TEXT_COLOR);
        payloadArea.setCaretColor(TEXT_COLOR);
        payloadArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        payloadArea.setLineWrap(true);
        payloadArea.setWrapStyleWord(true);
        payloadArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SECONDARY_COLOR),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        JScrollPane scrollPane = new JScrollPane(payloadArea);
        scrollPane.setBorder(null);
        messagePanel.add(scrollPane, gbc);
        
        mainPanel.add(messagePanel, BorderLayout.CENTER);

        // Footer Panel for buttons
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        footerPanel.setOpaque(false);
        
        JButton sendButton = createStyledButton("Send Message");
        JButton storeButton = createStyledButton("Store for Later");
        JButton reportsButton = createStyledButton("View Reports");
        
        feedbackLabel = new JLabel(" ", SwingConstants.CENTER);
        feedbackLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
        feedbackLabel.setForeground(Color.LIGHT_GRAY);
        
        footerPanel.add(sendButton);
        footerPanel.add(storeButton);
        footerPanel.add(reportsButton);

        JPanel bottomContainer = new JPanel(new BorderLayout());
        bottomContainer.setOpaque(false);
        bottomContainer.add(footerPanel, BorderLayout.CENTER);
        bottomContainer.add(feedbackLabel, BorderLayout.SOUTH);

        mainPanel.add(bottomContainer, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // --- Action Listeners ---
        sendButton.addActionListener(e -> handleSendMessage());
        storeButton.addActionListener(e -> handleStoreMessage());
        reportsButton.addActionListener(e -> openReportsWindow());
    }
    
    private void handleSendMessage() {
        String recipient = recipientField.getText();
        String payload = payloadArea.getText();
        
        Message message = new Message(currentUser.getCellPhoneNumber(), recipient, payload);
        
        // Find the highest current index to determine the next one
        int nextIndex = allMessages.stream().mapToInt(Message::getIndex).max().orElse(0) + 1;
        
        String result = message.sendMessage(nextIndex);
        
        if (result.contains("successfully")) {
            feedbackLabel.setForeground(Color.GREEN);
            allMessages.add(message);
            
            // Clear fields for next message
            recipientField.setText("");
            payloadArea.setText("");
            
            // Save immediately after a successful action
            MessageManager.saveAllMessages(allMessages);
        } else {
            feedbackLabel.setForeground(Color.ORANGE);
        }
        
        feedbackLabel.setText(result);
    }
    
    private void handleStoreMessage() {
        String recipient = recipientField.getText();
        String payload = payloadArea.getText();

        if (payload.trim().isEmpty() || recipient.trim().isEmpty()) {
            feedbackLabel.setForeground(Color.ORANGE);
            feedbackLabel.setText("Recipient and message cannot be empty to store.");
            return;
        }

        Message message = new Message(currentUser.getCellPhoneNumber(), recipient, payload);
        int nextIndex = allMessages.stream().mapToInt(Message::getIndex).max().orElse(0) + 1;
        
        message.storeMessage(nextIndex);
        allMessages.add(message);
        
        feedbackLabel.setForeground(Color.CYAN);
        feedbackLabel.setText("Message stored successfully!");
        
        recipientField.setText("");
        payloadArea.setText("");
        
        // Save immediately
        MessageManager.saveAllMessages(allMessages);
    }

    private void openReportsWindow() {
        // We pass the current list of messages to the reports UI
        ReportsUI reportsUI = new ReportsUI(this.allMessages);
        reportsUI.setVisible(true);
        // We don't close the chat window, just open the reports on top.
    }
    
    private void handleLogout() {
        // Save any pending changes before logging out
        MessageManager.saveAllMessages(allMessages);
        
        // Open a new login window
        SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true));
        
        // Close this window
        this.dispose();
    }

    // --- Styling helpers ---
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

    private void styleSecondaryButton(JButton button) {
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setBackground(new Color(100, 100, 120));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}

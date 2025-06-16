package chitchat;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * A dedicated JFrame for displaying reports as required by POE Part 3.
 *
 * @author Kitso Litelu (Created by me for Finale)
 * @version 2025-06-13
 */
public class ReportsUI extends JFrame {

    // Consistent me for Finale-inspired theme
    private static final Color PRIMARY_COLOR = new Color(26, 28, 59);
    private static final Color SECONDARY_COLOR = new Color(80, 101, 218);
    private static final Color TEXT_COLOR = new Color(230, 230, 255);
    private static final Color FIELD_BG_COLOR = new Color(40, 43, 84);

    private ReportManager reportManager;
    private ArrayList<Message> allMessages; // Keep a reference to update it after deletion
    private JTextArea reportArea;

    public ReportsUI(ArrayList<Message> messages) {
        super("ChitChat - Reports");
        this.allMessages = messages;
        this.reportManager = new ReportManager(messages);

        // Use DISPOSE_ON_CLOSE so it doesn't close the whole app
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1280, 720);
        setLocationRelativeTo(null); // Center on screen

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(PRIMARY_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Title
        JLabel titleLabel = new JLabel("Message Reports", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Report display area
        reportArea = new JTextArea();
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        reportArea.setEditable(false);
        reportArea.setBackground(FIELD_BG_COLOR);
        reportArea.setForeground(TEXT_COLOR);
        reportArea.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(reportArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(SECONDARY_COLOR));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel for action buttons
        JPanel buttonPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        buttonPanel.setOpaque(false);
        
        JButton sentDetailsBtn = createStyledButton("Sent Details");
        JButton longestMsgBtn = createStyledButton("Display Longest Message");
        JButton searchIdBtn = createStyledButton("Search by ID");
        JButton searchRecipientBtn = createStyledButton("Search by Recipient");
        JButton deleteHashBtn = createStyledButton("Delete by Hash");
        JButton fullReportBtn = createStyledButton("Display Full Report");

        buttonPanel.add(sentDetailsBtn);
        buttonPanel.add(longestMsgBtn);
        buttonPanel.add(searchIdBtn);
        buttonPanel.add(searchRecipientBtn);
        buttonPanel.add(deleteHashBtn);
        buttonPanel.add(fullReportBtn);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);

        // --- Action Listeners ---
        sentDetailsBtn.addActionListener(e -> reportArea.setText(reportManager.getSentMessagesDetails()));
        longestMsgBtn.addActionListener(e -> reportArea.setText(reportManager.findLongestMessage()));
        fullReportBtn.addActionListener(e -> reportArea.setText(reportManager.generateFullReport()));
        
        searchIdBtn.addActionListener(e -> {
            String id = JOptionPane.showInputDialog(this, "Enter Message ID to search for:", "Search by ID", JOptionPane.PLAIN_MESSAGE);
            if (id != null && !id.trim().isEmpty()) {
                reportArea.setText(reportManager.searchMessageById(id.trim()));
            }
        });
        
        searchRecipientBtn.addActionListener(e -> {
            String recipient = JOptionPane.showInputDialog(this, "Enter Recipient's Cell to search for:", "Search by Recipient", JOptionPane.PLAIN_MESSAGE);
            if (recipient != null && !recipient.trim().isEmpty()) {
                reportArea.setText(reportManager.searchMessagesByRecipient(recipient.trim()));
            }
        });

        deleteHashBtn.addActionListener(e -> {
            String hash = JOptionPane.showInputDialog(this, "Enter Message Hash to delete:", "Delete by Hash", JOptionPane.PLAIN_MESSAGE);
            if (hash != null && !hash.trim().isEmpty()) {
                String result = reportManager.deleteMessageByHash(hash.trim());
                reportArea.setText(result);
                // If deletion was successful, we must save the updated message list to the file.
                if (result.contains("successfully deleted")) {
                    MessageManager.saveAllMessages(this.allMessages);
                }
            }
        });
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBackground(SECONDARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
}

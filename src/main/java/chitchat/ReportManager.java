package chitchat;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Handles all business logic for generating reports based on the POE Part 3 requirements.
 *
 * @author Kitso Litelu (Created by me for Finale)
 * @version 2025-06-13
 */
public class ReportManager {

    private ArrayList<Message> allMessages;

    public ReportManager(ArrayList<Message> allMessages) {
        this.allMessages = allMessages;
    }
    
    /**
     * Gets the list of all sent messages (not stored or disregarded).
     */
    private ArrayList<Message> getSentMessages() {
        ArrayList<Message> sent = new ArrayList<>();
        for (Message msg : allMessages) {
            if (msg.isSent()) {
                sent.add(msg);
            }
        }
        return sent;
    }

    /**
     * (POE 2a) Displays the sender and recipient of all sent messages.
     */
    public String getSentMessagesDetails() {
        StringBuilder report = new StringBuilder("--- Sent Messages ---\n");
        ArrayList<Message> sent = getSentMessages();
        if (sent.isEmpty()) {
            return "No messages have been sent.";
        }
        for (Message msg : sent) {
            report.append(String.format("From: %s, To: %s, Message: \"%s\"\n",
                msg.getSender(), msg.getRecipient(), msg.getPayload()));
        }
        return report.toString();
    }

    /**
     * (POE 2b) Finds and displays the longest sent message.
     */
    public String findLongestMessage() {
        ArrayList<Message> sent = getSentMessages();
        if (sent.isEmpty()) {
            return "No sent messages to compare.";
        }
        
        Message longest = sent.stream()
            .max(Comparator.comparingInt(msg -> msg.getPayload().length()))
            .orElse(null);

        return longest != null ? "Longest Message: \"" + longest.getPayload() + "\"" : "Could not determine the longest message.";
    }

    /**
     * (POE 2c) Searches for a message by its ID.
     */
    public String searchMessageById(String id) {
        for (Message msg : allMessages) {
            if (msg.getId().equals(id)) {
                return String.format("--- Message Found ---\nRecipient: %s\nMessage: \"%s\"",
                    msg.getRecipient(), msg.getPayload());
            }
        }
        return "No message found with ID: " + id;
    }

    /**
     * (POE 2d) Searches for all messages sent to a particular recipient.
     */
    public String searchMessagesByRecipient(String recipientCell) {
        StringBuilder report = new StringBuilder("--- Messages for " + recipientCell + " ---\n");
        boolean found = false;
        for (Message msg : allMessages) {
            if (msg.getRecipient().equals(recipientCell) && (msg.isSent() || msg.isStored())) {
                report.append(String.format("Status: %s >> Message: \"%s\"\n", 
                    msg.isSent() ? "Sent" : "Stored", msg.getPayload()));
                found = true;
            }
        }
        if (!found) {
            return "No messages found for recipient: " + recipientCell;
        }
        return report.toString();
    }

    /**
     * (POE 2e) Deletes a message using its hash.
     * This method removes the message from the internal list.
     */
    public String deleteMessageByHash(String hash) {
        for (int i = 0; i < allMessages.size(); i++) {
            Message msg = allMessages.get(i);
            if (msg.getHash() != null && msg.getHash().equalsIgnoreCase(hash)) {
                String deletedPayload = msg.getPayload();
                allMessages.remove(i);
                // Important: After deleting, we must save the changes.
                // The UI will call MessageManager.saveAllMessages(this.allMessages).
                return "Message \"" + deletedPayload + "\" successfully deleted.";
            }
        }
        return "Message with hash '" + hash + "' not found for deletion.";
    }

    /**
     * (POE 2f) Displays a full report of all sent messages.
     */
    public String generateFullReport() {
        StringBuilder report = new StringBuilder("--- Full Sent Message Report ---\n");
        ArrayList<Message> sent = getSentMessages();
        if (sent.isEmpty()) {
            return "No sent messages to report.";
        }
        for (Message msg : sent) {
            report.append("---------------------------------\n");
            report.append("Message Hash: ").append(msg.getHash()).append("\n");
            report.append("Recipient: ").append(msg.getRecipient()).append("\n");
            report.append("Message: \"").append(msg.getPayload()).append("\"\n");
        }
        report.append("---------------------------------\n");
        return report.toString();
    }
}

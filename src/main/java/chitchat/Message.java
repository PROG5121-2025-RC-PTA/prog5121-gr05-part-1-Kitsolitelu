package chitchat;

/**
 * Represents a message in the ChitChat system.
 * This class holds all information about a message and handles its validation and hash generation.
 *
 * @author Kitso Litelu (Updated by me for Finale)
 * @version 2025-06-13
 */
public class Message {

    private final String id;
    private final String sender;
    private final String recipient;
    private final String payload;
    private int index;
    private String hash;

    // Flags for Part 3 reports
    private boolean sent;
    private boolean stored;
    private boolean disregarded;

    /**
     * Creates a new message with a randomly generated 10-digit ID.
     *
     * @param sender    The sender's cell number.
     * @param recipient The recipient's cell number.
     * @param payload   The message content.
     */
    public Message(String sender, String recipient, String payload) {
        this.id = String.format("%010d", (long) (Math.random() * 10000000000L));
        this.sender = sender;
        this.recipient = recipient;
        this.payload = payload;
        this.index = 0; // Index is set when the message is processed (sent/stored)
        this.hash = ""; // Hash is generated when the message is processed
        this.sent = false;
        this.stored = false;
        this.disregarded = false;
    }

    /**
     * Constructor to load a message from persistent storage.
     */
    public Message(String id, String sender, String recipient, String payload, int index, String hash, boolean sent, boolean stored, boolean disregarded) {
        this.id = id;
        this.sender = sender;
        this.recipient = recipient;
        this.payload = payload;
        this.index = index;
        this.hash = hash;
        this.sent = sent;
        this.stored = stored;
        this.disregarded = disregarded;
    }

    /**
     * Checks if the message ID is a valid 10-digit number.
     */
    public boolean checkMessageID() {
        if (this.id == null || this.id.length() != 10) {
            return false;
        }
        return this.id.matches("\\d{10}"); // Check if it contains only digits
    }

    /**
     * Validates a cell number (+27 followed by 9 digits).
     */
    public int checkCellNumber(String cellNumber) {
        if (cellNumber != null && cellNumber.matches("^\\+27\\d{9}$")) {
            return 1;
        }
        return 403; // Invalid
    }

    /**
     * Generates a hash from the message ID, index, and payload.
     * Format: <first two digits of ID>:<index>:<first word><last word> (uppercase).
     */
    public String createMessageHash() {
        if (this.id == null || this.payload == null) {
            return "";
        }
        
        String firstTwoOfId = this.id.length() >= 2 ? this.id.substring(0, 2) : this.id;
        
        // Split payload into words
        String[] words = this.payload.trim().split("\\s+");
        String combinedWords = "";

        if (words.length > 0 && !words[0].isEmpty()) {
            if (words.length == 1) {
                combinedWords = words[0]; // Only one word
            } else {
                combinedWords = words[0] + words[words.length - 1]; // First and last word
            }
        }
        
        return (firstTwoOfId + ":" + this.index + ":" + combinedWords).toUpperCase();
    }

    /**
     * Processes a message to be sent. Updates its state and generates the hash.
     *
     * @param messageIndex The next available index for messages.
     * @return A status message.
     */
    public String sendMessage(int messageIndex) {
        if (!checkMessageID()) return "Failed: Invalid message ID.";
        if (checkCellNumber(this.recipient) != 1) return "Failed: Invalid recipient number.";
        if (checkCellNumber(this.sender) != 1) return "Failed: Invalid sender number.";
        if (this.payload == null || this.payload.trim().isEmpty()) return "Failed: Message content cannot be empty.";
        if (this.payload.length() > 250) return "Failed: Message is too long (max 250 chars).";

        this.index = messageIndex;
        this.hash = createMessageHash();
        this.sent = true;
        this.stored = false;
        this.disregarded = false;

        return "Message sent successfully!";
    }
    
    /**
     * Processes a message to be stored. Updates its state and generates the hash.
     *
     * @param messageIndex The next available index for messages.
     */
    public void storeMessage(int messageIndex) {
        this.index = messageIndex;
        this.hash = createMessageHash(); // Create hash even for stored messages
        this.sent = false;
        this.stored = true;
        this.disregarded = false;
    }
    
    /**
     * Marks a message as disregarded.
     */
    public void disregardMessage() {
        this.sent = false;
        this.stored = false;
        this.disregarded = true;
    }


    // --- Getters ---
    public String getId() { return id; }
    public String getSender() { return sender; }
    public String getRecipient() { return recipient; }
    public String getPayload() { return payload; }
    public int getIndex() { return index; }
    public String getHash() { return hash; }
    public boolean isSent() { return sent; }
    public boolean isStored() { return stored; }
    public boolean isDisregarded() { return disregarded; }
}

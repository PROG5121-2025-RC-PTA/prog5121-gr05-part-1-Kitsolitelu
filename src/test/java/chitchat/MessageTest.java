package chitchat;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests for the updated Message class.
 * These tests are aligned with the new constructor and methods for Part 3.
 *
 * @author Kitso Litelu (Corrected by me for Finale)
 * @version 2025-06-13
 */
public class MessageTest {

    // Test the main constructor
    @Test
    void testMessageCreation() {
        Message msg = new Message("+27111111111", "+27222222222", "Test Payload");
        assertNotNull(msg.getId(), "Message ID should not be null.");
        assertEquals(10, msg.getId().length(), "Message ID should be 10 characters long.");
        assertEquals("+27111111111", msg.getSender());
        assertEquals("+27222222222", msg.getRecipient());
        assertEquals("Test Payload", msg.getPayload());
        assertEquals(0, msg.getIndex(), "Initial index should be 0.");
        assertEquals("", msg.getHash(), "Initial hash should be empty.");
        assertFalse(msg.isSent() || msg.isStored() || msg.isDisregarded(), "Flags should be false on creation.");
    }

    // Test the checkMessageID method
    @Test
    void testCheckMessageID() {
        Message msg = new Message("+27111111111", "+27222222222", "Test");
        assertTrue(msg.checkMessageID(), "A valid, auto-generated ID should pass the check.");
    }

    // Test the checkCellNumber method
    @Test
    void testCheckCellNumber() {
        Message msg = new Message("+27111111111", "+27222222222", "Test");
        assertEquals(1, msg.checkCellNumber("+27123456789"), "A valid SA cell number should return 1.");
        assertEquals(403, msg.checkCellNumber("27123456789"), "Number missing '+' should return 403.");
        assertEquals(403, msg.checkCellNumber("+2712345678"), "Number that is too short should return 403.");
        assertEquals(403, msg.checkCellNumber("+271234567890"), "Number that is too long should return 403.");
        assertEquals(403, msg.checkCellNumber("+2712345678a"), "Number with letters should return 403.");
        assertEquals(403, msg.checkCellNumber(null), "A null number should return 403.");
    }

    // Test the createMessageHash method
    @Test
    void testCreateMessageHash() {
        // We need a predictable ID for this test, so we use the full constructor.
        Message msg = new Message("1234567890", "+27111111111", "+27222222222", "Hi Tonight", 1, "", false, false, false);
        // Set the index before creating the hash
        msg.sendMessage(1); 
        String hash = msg.getHash();
        assertTrue(hash.startsWith("12:1:"), "Hash should start with first two of ID and index.");
        assertTrue(hash.endsWith("HITONIGHT"), "Hash should end with combined first and last words.");
    }

    // Test the sendMessage method with valid inputs
    @Test
    void testSendMessage_Success() {
        Message msg = new Message("+27111111111", "+27222222222", "This is a valid message");
        String result = msg.sendMessage(5); // Use an arbitrary index
        assertEquals("Message sent successfully!", result);
        assertTrue(msg.isSent());
        assertFalse(msg.isStored());
        assertEquals(5, msg.getIndex());
        assertFalse(msg.getHash().isEmpty(), "Hash should be generated when a message is sent.");
    }

    // Test sendMessage with an invalid recipient
    @Test
    void testSendMessage_InvalidRecipient() {
        Message msg = new Message("+27111111111", "invalid-number", "Hello");
        String result = msg.sendMessage(1);
        assertEquals("Failed: Invalid recipient number.", result);
        assertFalse(msg.isSent(), "Message should not be marked as sent on failure.");
    }

    // Test sendMessage with an oversized payload
    @Test
    void testSendMessage_PayloadTooLong() {
        String longPayload = "a".repeat(251);
        Message msg = new Message("+27111111111", "+27222222222", longPayload);
        String result = msg.sendMessage(1);
        assertEquals("Failed: Message is too long (max 250 chars).", result);
        assertFalse(msg.isSent());
    }

    // Test the storeMessage method
    @Test
    void testStoreMessage() {
        Message msg = new Message("+27111111111", "+27222222222", "Store this for me");
        msg.storeMessage(10); // Use an arbitrary index
        assertTrue(msg.isStored());
        assertFalse(msg.isSent());
        assertEquals(10, msg.getIndex());
        assertFalse(msg.getHash().isEmpty(), "Hash should still be generated for stored messages.");
    }

    // Test the disregardMessage method
    @Test
    void testDisregardMessage() {
        Message msg = new Message("+27111111111", "+27222222222", "Nevermind");
        msg.disregardMessage();
        assertTrue(msg.isDisregarded());
        assertFalse(msg.isSent());
        assertFalse(msg.isStored());
        assertEquals(0, msg.getIndex(), "Index should not be set for disregarded messages.");
        assertTrue(msg.getHash().isEmpty(), "Hash should not be set for disregarded messages.");
    }
}

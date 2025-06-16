package chitchat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests for the ReportManager class, covering POE Part 3 requirements.
 * Uses the exact test data specified in the POE document.
 *
 * @author Kitso Litelu (Created by me for Finale)
 * @version 2025-06-13
 */
public class ReportsTest {

    private ReportManager reportManager;
    private ArrayList<Message> testMessages;
    
    // Test data from POE Part 3
    private final String userCell = "+27000000000"; // Generic sender for testing

    @BeforeEach
    void setUp() {
        testMessages = new ArrayList<>();

        // Message 1: Sent
        Message msg1 = new Message(userCell, "+27834557896", "Did you get the cake?");
        msg1.sendMessage(1);
        testMessages.add(msg1);

        // Message 2: Stored
        Message msg2 = new Message(userCell, "+27838884567", "Where are you? You are late! I have asked you to be on time.");
        msg2.storeMessage(2);
        testMessages.add(msg2);

        // Message 3: Disregarded
        Message msg3 = new Message(userCell, "+27834484567", "Yohoooo, I am at your gate.");
        msg3.disregardMessage(); // This message won't have an index or hash
        testMessages.add(msg3);

        // Message 4: Sent (Note: POE data says developer "083..." which is invalid, correcting to valid format for test)
        Message msg4 = new Message(userCell, "+27838884567", "It is dinner time!");
        msg4.sendMessage(3);
        testMessages.add(msg4);
        
        // Message 5: Stored
        Message msg5 = new Message(userCell, "+27838884567", "Ok, I am leaving without you.");
        msg5.storeMessage(4);
        testMessages.add(msg5);

        reportManager = new ReportManager(testMessages);
    }
    
    @Test
    void testSentMessagesArrayIsCorrectlyPopulated() {
        String report = reportManager.getSentMessagesDetails();
        assertTrue(report.contains("Did you get the cake?"), "Report should contain the first sent message.");
        assertTrue(report.contains("It is dinner time!"), "Report should contain the second sent message.");
        assertFalse(report.contains("Where are you?"), "Report should NOT contain stored messages.");
        assertFalse(report.contains("Yohoooo"), "Report should NOT contain disregarded messages.");
    }

    @Test
    void testDisplayLongestMessage() {
        // The POE expects the longest *sent* message.
        // POE Test Data Message 1: "Did you get the cake?" -> length 23
        // POE Test Data Message 4: "It is dinner time!" -> length 18
        // The expected longest SENT message is "Did you get the cake?".
        String actual = reportManager.findLongestMessage();
        assertTrue(actual.contains("Did you get the cake?"), "The longest sent message should be correctly identified.");
    }
    
    @Test
    void testSearchForMessageID() {
        // We need the ID of message 4 for this test.
        String idOfMessage4 = testMessages.get(3).getId();
        String report = reportManager.searchMessageById(idOfMessage4);
        
        assertTrue(report.contains("Recipient: +27838884567"), "Search by ID should show the correct recipient.");
        assertTrue(report.contains("Message: \"It is dinner time!\""), "Search by ID should show the correct message payload.");
    }
    
    @Test
    void testSearchAllMessagesForRecipient() {
        // Search for all sent or stored messages for "+27838884567"
        String report = reportManager.searchMessagesByRecipient("+27838884567");

        assertTrue(report.contains("Where are you? You are late! I have asked you to be on time."), "Report should find the first stored message for the recipient.");
        assertTrue(report.contains("It is dinner time!"), "Report should find the sent message for the recipient.");
        assertTrue(report.contains("Ok, I am leaving without you."), "Report should find the second stored message for the recipient.");
    }
    
    @Test
    void testDeleteMessageUsingHash() {
        // We need the hash of message 2 for this test
        String hashOfMessage2 = testMessages.get(1).getHash();
        String expectedPayload = testMessages.get(1).getPayload();
        
        String result = reportManager.deleteMessageByHash(hashOfMessage2);
        String expectedResult = "Message \"" + expectedPayload + "\" successfully deleted.";
        
        assertEquals(expectedResult, result, "The confirmation message for deletion should be correct.");
        
        // Verify it was actually removed from the list
        assertEquals(4, testMessages.size(), "The message should be removed from the main list.");
        
        // Try to delete it again, should fail
        String secondAttempt = reportManager.deleteMessageByHash(hashOfMessage2);
        assertEquals("Message with hash '" + hashOfMessage2.toUpperCase() + "' not found for deletion.", secondAttempt);
    }
    
    @Test
    void testDisplayFullReport() {
        String report = reportManager.generateFullReport();
        
        // The report should contain details for the two SENT messages (msg1 and msg4)
        assertTrue(report.contains(testMessages.get(0).getHash()), "Full report should contain hash of message 1.");
        assertTrue(report.contains(testMessages.get(0).getRecipient()), "Full report should contain recipient of message 1.");
        assertTrue(report.contains(testMessages.get(0).getPayload()), "Full report should contain payload of message 1.");
        
        assertTrue(report.contains(testMessages.get(3).getHash()), "Full report should contain hash of message 4.");
        assertTrue(report.contains(testMessages.get(3).getRecipient()), "Full report should contain recipient of message 4.");
        assertTrue(report.contains(testMessages.get(3).getPayload()), "Full report should contain payload of message 4.");
        
        assertFalse(report.contains("Where are you?"), "Full report should not contain stored messages.");
    }
}

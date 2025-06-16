package chitchat;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Manages loading and saving messages to a single JSON file.
 * This approach is more efficient than storing each message in a separate file.
 *
 * @author Kitso Litelu (Updated by me for Finale)
 * @version 2025-06-13
 */
public class MessageManager {

    private static final String MESSAGES_FILE = "messages.json";

    /**
     * Loads all messages from the messages.json file.
     *
     * @return An ArrayList of all Message objects.
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<Message> loadAllMessages() {
        ArrayList<Message> allMessages = new ArrayList<>();
        JSONParser parser = new JSONParser();

        // Try to read the existing file
        try (FileReader reader = new FileReader(MESSAGES_FILE)) {
            JSONArray messagesArray = (JSONArray) parser.parse(reader);

            for (Object obj : messagesArray) {
                JSONObject jsonMessage = (JSONObject) obj;

                String id = (String) jsonMessage.get("id");
                String sender = (String) jsonMessage.get("sender");
                String recipient = (String) jsonMessage.get("recipient");
                String payload = (String) jsonMessage.get("payload");
                long indexLong = (Long) jsonMessage.getOrDefault("index", 0L);
                int index = (int) indexLong;
                String hash = (String) jsonMessage.get("hash");
                boolean sent = (Boolean) jsonMessage.getOrDefault("sent", false);
                boolean stored = (Boolean) jsonMessage.getOrDefault("stored", false);
                boolean disregarded = (Boolean) jsonMessage.getOrDefault("disregarded", false);


                // Create Message object using the constructor that takes all fields
                Message message = new Message(id, sender, recipient, payload, index, hash, sent, stored, disregarded);
                allMessages.add(message);
            }
        } catch (IOException | ParseException e) {
            // If the file doesn't exist or is empty, it's not an error.
            // We'll just start with an empty list.
            System.out.println("Info: messages.json not found or is empty. Starting fresh.");
        } catch (Exception e) {
            System.err.println("An unexpected error occurred while loading messages: " + e.getMessage());
        }

        return allMessages;
    }

    /**
     * Saves a list of messages to the messages.json file.
     * This will overwrite the existing file with the new list.
     *
     * @param messages The ArrayList of Message objects to save.
     */
    @SuppressWarnings("unchecked")
    public static void saveAllMessages(ArrayList<Message> messages) {
        JSONArray messagesArray = new JSONArray();
        for (Message msg : messages) {
            JSONObject jsonMessage = new JSONObject();
            jsonMessage.put("id", msg.getId());
            jsonMessage.put("sender", msg.getSender());
            jsonMessage.put("recipient", msg.getRecipient());
            jsonMessage.put("payload", msg.getPayload());
            jsonMessage.put("index", msg.getIndex());
            jsonMessage.put("hash", msg.getHash());
            jsonMessage.put("sent", msg.isSent());
            jsonMessage.put("stored", msg.isStored());
            jsonMessage.put("disregarded", msg.isDisregarded());
            messagesArray.add(jsonMessage);
        }

        try (FileWriter file = new FileWriter(MESSAGES_FILE)) {
            file.write(messagesArray.toJSONString());
            file.flush();
        } catch (IOException e) {
            System.err.println("Error saving messages: " + e.getMessage());
        }
    }
}

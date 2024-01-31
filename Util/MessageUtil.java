package Util;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;

public class MessageUtil {

    //map of all the messages for each user
    //for each user id, there is a LinkedList of messages,
    //saved as JSON objects
    private static final HashMap<Long, LinkedList<JSONObject>> messagesMap = new HashMap<>();

    public static void saveMessage(long userId, String role, String message) {
        //creating an empty list for users messages if it's not created yet
        messagesMap.computeIfAbsent(userId, k -> new LinkedList<>());

        //we are saving our message as JSON object
        JSONObject userRequestJSON = new JSONObject();
        userRequestJSON.put("role", role);
        userRequestJSON.put("content", message);

        //adding message to messages list for user with userId
        messagesMap.get(userId).add(userRequestJSON);

        //logging
        Logger.log(role + " : " + message);
    }

    public static LinkedList<JSONObject> getMessageStory(long chatId) {
        //creating an empty list for users messages if it's not created yet
        messagesMap.computeIfAbsent(chatId, k -> new LinkedList<>());

        return messagesMap.get(chatId);
    }
}

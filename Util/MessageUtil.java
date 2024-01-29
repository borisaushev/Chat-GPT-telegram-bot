package Util;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;

public class MessageUtil {

    //map of all the messages for each user
    private static final HashMap<Long, LinkedList<JSONObject>> messagesMap = new HashMap<>();

    public static void addMessage(long userId,String role, String message) {
        //creating an empty list for users messages if it's not created yet
        messagesMap.computeIfAbsent(userId, k -> new LinkedList<>());

        //adding message to messages map for user with userId
        JSONObject userRequestJSON = new JSONObject();
        userRequestJSON.put("role", role);
        userRequestJSON.put("content", message);

        messagesMap.get(userId).add(userRequestJSON);

        //logging
        Logger.log(role + " : " + message);
    }

    public static LinkedList<JSONObject> getAllMessages(long chatId) {
        //creating an empty list for users messages if it's not created yet
        messagesMap.computeIfAbsent(chatId, k -> new LinkedList<>());

        return messagesMap.get(chatId);
    }
}

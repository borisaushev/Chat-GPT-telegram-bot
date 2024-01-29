import org.glassfish.grizzly.utils.Pair;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class MessageUtil {

    private static HashMap<Long, List<Pair<String, String>>> map = new HashMap<>();

    public static void addMessage(long chatId, String request, String response) {
        request = request.replace("\"", "`").replace("{", "").replace("}", "");
        response = response.replace("\"", "`").replace("{", "").replace("}", "");
        map.computeIfAbsent(chatId, k -> new LinkedList<>());
        map.get(chatId).add(new Pair<>(request, response));
    }

    public static String getAllMessages(long chatId) {

        if(map.get(chatId) == null || map.get(chatId).isEmpty())
            return " ";


        StringBuilder build = new StringBuilder();
        for(Pair<String, String> pair : map.get(chatId)) {
            build.append("{\"role\": \"user\", \"content\": \"" + pair.getFirst() + "\"},");
            build.append("{\"role\": \"assistant\", \"content\": \"" + pair.getSecond() + "\"},");
        }

        return build.toString();
    }
}

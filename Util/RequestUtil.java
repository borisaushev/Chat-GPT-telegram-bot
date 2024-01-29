package Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RequestUtil {
    public static HttpRequest buildRequest(long userId) {
        //making request json object
        JSONObject request = new JSONObject();
        request.put("model", Properties.model);
        request.put("temperature", 0.7);

        //adding all messages into JSON array, including our just created one
        JSONArray messagesJSONArray = new JSONArray(MessageUtil.getAllMessages(userId));

        //adding JSON array into our json request
        request.put("messages", messagesJSONArray);

        //creating http request on our json request
        String HttpRequestBody = request.toString();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(Properties.URL))
                .POST(HttpRequest.BodyPublishers.ofString(HttpRequestBody))
                .header("Authorization", Properties.chatGPTAuthToken)
                .header("Content-Type", "application/json")
                .build();

        return httpRequest;
    }

    public static HttpResponse<String> sendRequest(HttpRequest request) {
        //sending request
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return response;
    }

    public static String parseResponse(HttpResponse<String> httpResponse, long userId) {
        //parsing our response as JSON object
        String responseBody = httpResponse.body();
        JSONObject jsonObject = new JSONObject(responseBody);

        //getting Open AI response
        String response = (String) jsonObject.getJSONArray("choices")
                                    .getJSONObject(0)
                                    .getJSONObject("message")
                                    .get("content");

        //adding message to message
        MessageUtil.addMessage(userId, "assistant", response);

        return response;
    }

}

package Util;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RequestUtil {
    public static HttpRequest buildRequest(String userRequest, long userId) {
        //building the request
        String HttpRequestBody =
                "{" +
                    "\"model\": \"" + Properties.model + "\"," +
                    "\"messages\": [" +
                    MessageUtil.getAllMessages(userId).replace("\n", "").replace("\\", "")  +

                    "{\"role\": \"user\", \"content\": \"" +
                    userRequest +
                    "\"}" +

                    "]," +
                    "\"temperature\": 0.7" +
                "}";

        //adding headers
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(Properties.URL))
                .POST(HttpRequest.BodyPublishers.ofString(HttpRequestBody))
                .header("Authorization", Properties.chatGPTAuthToken)
                .header("Content-Type", "application/json")
                .build();

        return request;
    }

    public static HttpResponse<String> sendRequest(HttpRequest request) {
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response;
        try {
            //sending request
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return response;
    }

    public static String parseResponse(HttpResponse<String> httpResponse) {
        String responseBody = httpResponse.body().replace("\\n", "\n").replace("\\\"", "`");

        //parsing response
        int firstIndex = responseBody.indexOf("\"content\": \"") + "\"content\": \"".length();

        StringBuilder builder = new StringBuilder();
        int i = firstIndex;
        while(responseBody.charAt(i) != '"')
            builder.append(responseBody.charAt(i++));

        String result = builder.toString();
        result = result.replace("`", "\"");

        return result;
    }

}

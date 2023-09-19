import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Bot extends TelegramLongPollingBot {


    @Override
    public void onUpdateReceived(Update update) {

        System.out.println("request!");

        Message message = update.getMessage();
        String text = message.getText().replace("\"", "`").replace("\n", "").replace("\\", "");
        Long id = message.getChatId();

        if(text.equals("/start"))
            text = "представься";

        SendMessage mail = new SendMessage();
        mail.setChatId(id.toString());

        String HttpRequestBody =
                          "{" +
                             "\"model\": \"" + Properties.model + "\"," +
                             "\"messages\": [" +
                                MessageUtil.getAllMessages(id).replace("\n", "").replace("\\", "")  +

                                "{\"role\": \"user\", \"content\": \"" +
                                    text +
                                "\"}" +

                             "]," +
                             "\"temperature\": 0.7" +
                           "}";

        System.out.println("HttpRequestBody : " + HttpRequestBody);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(Properties.URL))
                .POST(HttpRequest.BodyPublishers.ofString(HttpRequestBody))
                .header("Authorization", Properties.chatGPTAuthToken)
                .header("Content-Type", "application/json")
                .build();

        try {

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body().replace("\\n", "\n").replace("\\\"", "`");

            System.out.println("response: " + responseBody);

            int firstIndex = responseBody.indexOf("\"content\": \"") + "\"content\": \"".length();

            StringBuilder builder = new StringBuilder();
            int i = firstIndex;
            while(responseBody.charAt(i) != '"')
                builder.append(responseBody.charAt(i++));

            String result = builder.toString();
            MessageUtil.addMessage(id, text.replace("\"", "`"), result);
            mail.setText(result.replace("`", "\""));

        } catch (IOException | InterruptedException e) {
            System.out.println("exception " + e);
            mail.setText("Error occured");
        }

        try {
            execute(mail);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String getBotUsername() {
        return "ChatGPT-4";
    }

    @Override
    public String getBotToken() {
        return Properties.botToken;
    }
}
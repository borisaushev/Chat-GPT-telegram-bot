import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Bot extends TelegramLongPollingBot {

    private final Logger logger;

    public Bot() throws IOException {
        logger = new Logger();
    }

    @Override
    public void onUpdateReceived(Update update) {

        //getting the request
        Message message = update.getMessage();
        String text = message.getText().replace("\"", "`").replace("\n", "").replace("\\", "");
        Long id = message.getChatId();

        if(text.equals("/start"))
            text = "представься";

        SendMessage mail = new SendMessage();
        mail.setChatId(id.toString());

        System.out.println("request: " + text);

        //building the request
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


        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(Properties.URL))
                .POST(HttpRequest.BodyPublishers.ofString(HttpRequestBody))
                .header("Authorization", Properties.chatGPTAuthToken)
                .header("Content-Type", "application/json")
                .build();

        try {
            //sending request
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body().replace("\\n", "\n").replace("\\\"", "`");

            //parsing response
            int firstIndex = responseBody.indexOf("\"content\": \"") + "\"content\": \"".length();

            StringBuilder builder = new StringBuilder();
            int i = firstIndex;
            while(responseBody.charAt(i) != '"')
                builder.append(responseBody.charAt(i++));

            String result = builder.toString();

            System.out.println("response: " + result);

            //sending the answer back to client
            mail.setText(result.replace("`", "\""));
            execute(mail);

            //adding the message to message story
            MessageUtil.addMessage(id, text.replace("\"", "`"), result);

            //logging
            logger.log("request: " + HttpRequestBody);
            logger.log("responce: " + responseBody);


        } catch (IOException | InterruptedException | TelegramApiException e) {
            System.out.println("exception " + e);
            mail.setText("Error occured");
        }


    }

    @Override
    public String getBotUsername() {
        return "ChatGPT";
    }

    @Override
    public String getBotToken() {
        return Properties.botToken;
    }
}
import Util.Logger;
import Util.MessageUtil;
import Util.Properties;
import Util.RequestUtil;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;

public class Bot extends TelegramLongPollingBot {

    private final Logger logger;
    public Bot() throws IOException {
        logger = new Logger();
    }

    @Override
    public void onUpdateReceived(Update update) {

        //getting the request
        Message message = update.getMessage();
        String userRequest = message.getText().replace("\"", "`").replace("\n", "").replace("\\", "");

        if(userRequest.equals("/start"))
            userRequest = "представься";

        //building request
        Long userId = message.getChatId();
        var request = RequestUtil.buildRequest(userRequest, userId);

        //getting response
        var response = RequestUtil.sendRequest(request);

        //parsing response
        String result = RequestUtil.parseResponse(response);

        //sending message back to user
        sendMessage(result, userId);

        //adding the message to message story
        MessageUtil.addMessage(userId, userRequest, result);

        //logging
        logger.log("request: " + userRequest);
        logger.log("responce: " + result + "\n");

    }

    private void sendMessage(String result, Long userId) {
        SendMessage mail = new SendMessage();
        mail.setChatId(userId.toString());
        try {
            //sending the answer back to client
            mail.setText(result);
            execute(mail);

        } catch (TelegramApiException e) {
            logger.log("------- Exception -------\n" + e);
            mail.setText("Oops, something went wrong");
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
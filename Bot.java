import Util.*;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


public class Bot extends TelegramLongPollingBot {
    @Override
    public void onUpdateReceived(Update update) {

        //getting the request
        Message message = update.getMessage();
        Long userId = message.getChatId();
        String userRequest = message.getText();

        if(userRequest.equals("/start"))
            userRequest = "представься";

        //saving JSON object from our user's request
        MessageUtil.addMessage(userId,"user", userRequest);

        //building request, from all our previous messages, and our just saved request
        var request = RequestUtil.buildRequest(userId);

        //getting and parsing response
        var httpResponse = RequestUtil.sendRequest(request);
        String response = RequestUtil.parseResponse(httpResponse, userId);

        //sending message back to user
        sendMessage(response, userId);

    }

    private void sendMessage(String result, Long userId) {
        //sending the answer back to client
        SendMessage mail = new SendMessage();
        mail.setChatId(userId.toString());

        try {
            mail.setText(result);
            execute(mail);
        } catch (TelegramApiException e) {
            Logger.log("------- Exception -------\n" + e);
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
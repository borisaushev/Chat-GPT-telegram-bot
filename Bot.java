import Util.*;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


public class Bot extends TelegramLongPollingBot {
    @Override
    public void onUpdateReceived(Update update) {

        //getting the request and user id
        Message message = update.getMessage();
        Long userId = message.getChatId();
        String userRequest = message.getText();

        //if user is texting bot for the first time, it introduces itself
        if(userRequest.equals("/start"))
            userRequest = "представься";

        //saving our user's request in message story
        MessageUtil.saveMessage(userId,"user", userRequest);

        //building HttpRequest, from all our previous messages,
        //including our just saved request
        var httpRequest = RequestUtil.buildHttpRequest(userId);

        //getting and parsing HttpResponse
        var httpResponse = RequestUtil.sendHttpRequest(httpRequest);
        String response = RequestUtil.parseHttpResponse(httpResponse);

        //saving message to message story
        MessageUtil.saveMessage(userId, "assistant", response);

        //sending message back to user
        sendMessage(response, userId);

    }

    private void sendMessage(String result, Long userId) {
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
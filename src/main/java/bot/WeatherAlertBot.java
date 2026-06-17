package bot;

import config.BotConfig;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class WeatherAlertBot extends TelegramLongPollingBot {

    //Токен передаем в конструктор
    public WeatherAlertBot() {
        super(BotConfig.getBotToken());
    }
    //Переопредеяем метод и передаем имя бота
    @Override
    public String getBotUsername() {
        return BotConfig.getBotName();
    }
    @Override
    public void onUpdateReceived(Update update){
        if(update.hasMessage() && update.getMessage().hasText()){
            String chatId = update.getMessage().getChatId().toString();
            String text = update.getMessage().getText();

            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            if(text.equalsIgnoreCase("привет")){
                message.setText("И тебе привет");
            }else {
                message.setText("Ты написал: " + text);
            }

            try {
                execute(message);
            } catch (TelegramApiException e) {
               e.printStackTrace();
            }
        }
    }


}

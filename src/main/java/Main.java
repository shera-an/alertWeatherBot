import bot.WeatherAlertBot;
import db.DatabaseMigrator;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) {
        DatabaseMigrator.migrate();
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new WeatherAlertBot());
            System.out.println("Бот запущен!");

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}

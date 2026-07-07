package ru.sheraAn;

import ru.sheraAn.bot.WeatherAlertBot;
import ru.sheraAn.config.db.DatabaseMigrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.sheraAn.service.MessageFormatter;
import ru.sheraAn.service.OpenMeteoService;
import ru.sheraAn.service.UserService;
import ru.sheraAn.service.YandexGeocoderService;
import ru.sheraAn.service.interfaces.GeocoderService;
import ru.sheraAn.service.interfaces.MessageService;
import ru.sheraAn.service.interfaces.UserStorage;
import ru.sheraAn.service.interfaces.WeatherService;

public class Main {
    public static void main(String[] args) {

        System.out.println("Запуск приложения...");

        DatabaseMigrator.migrate();
        WeatherService weatherService = new OpenMeteoService();
        UserStorage userStorage = new UserService();
        GeocoderService geocoder = new YandexGeocoderService();
        MessageService messageService = new MessageFormatter();
        Logger logger = LoggerFactory.getLogger(Main.class);

        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new WeatherAlertBot(weatherService, userStorage,geocoder,messageService));
            logger.info("Бот запущен!");
        } catch (TelegramApiException e) {
            logger.error("Ошибка запуска бота:", e);
        }


    }
}
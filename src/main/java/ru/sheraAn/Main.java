package ru.sheraAn;

import bot.WeatherAlertBot;
import db.DatabaseMigrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import service.MessageFormatter;
import service.OpenMeteoService;
import service.UserService;
import service.YandexGeocoderService;
import service.interfaces.GeocoderService;
import service.interfaces.MessageService;
import service.interfaces.UserStorage;
import service.interfaces.WeatherService;

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
package ru.sheraAn.bot;

import ru.sheraAn.model.DailyForecast;
import ru.sheraAn.model.HourlyForecast;
import ru.sheraAn.model.User;
import ru.sheraAn.model.WeatherResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import ru.sheraAn.config.BotConfig;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.sheraAn.service.interfaces.GeocoderService;
import ru.sheraAn.service.interfaces.MessageService;
import ru.sheraAn.service.interfaces.UserStorage;
import ru.sheraAn.service.interfaces.WeatherService;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeatherAlertBot extends TelegramLongPollingBot {

    private final WeatherService weatherService;
    private final UserStorage userStorage;
    private final GeocoderService geocoder;
    private final MessageService messageService;
    private final Map<String, String> userStates = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(WeatherAlertBot.class);

    private static final String AWAITING_NAME = "AWAITING_NAME";
    private static final String AWAITING_CITY = "AWAITING_CITY";
    private static final String AWAITING_NEW_NAME = "AWAITING_NEW_NAME";
    private static final String AWAITING_NEW_CITY = "AWAITING_NEW_CITY";

    private static final String CMD_START = "/start";
    private static final String CMD_WEATHER = "/weather";
    private static final String CMD_HELP = "/help";
    private static final String CMD_SETTINGS = "/settings";
    private static final String CMD_CHANGE_NAME = "/change name";
    private static final String CMD_CHANGE_CITY = "/change city";
    private static final String CMD_BACK = "/back";

    private static final String BTN_WEATHER_NOW = "Погода сейчас";
    private static final String BTN_FORECAST = "Прогноз";
    private static final String BTN_TODAY = "На сегодня";
    private static final String BTN_3_DAYS = "На 3 дня";
    private static final String BTN_WEEK = "На неделю";
    private static final String BTN_GEOLOCATION = "Погода по геолокации";
    private static final String BTN_SETTINGS = "Настройки";
    private static final String BTN_CHANGE_NAME = "Изменить имя";
    private static final String BTN_CHANGE_CITY = "Изменить город";
    private static final String BTN_BACK = "Назад";
    private static final String BTN_HELP = "Помощь";

    public WeatherAlertBot(WeatherService weatherService,
                           UserStorage userStorage,
                           GeocoderService geocoder,
                           MessageService messageService) {
        super(BotConfig.getBotToken());
        this.weatherService = weatherService;
        this.userStorage = userStorage;
        this.geocoder = geocoder;
        this.messageService = messageService;
    }

    @Override
    public String getBotUsername() {
        return BotConfig.getBotName();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage()) {
            return;
        }

        String chatIdStr = update.getMessage().getChatId().toString();

        if (update.getMessage().hasLocation()) {
            handleLocation(update, chatIdStr);
            return;
        }

        if (!update.getMessage().hasText()) {
            return;
        }

        String text = update.getMessage().getText();
        logger.info("Получено сообщение: {}",text);

        try {
            User user = userStorage.getUser(chatIdStr);
            String state = userStates.get(chatIdStr);

            if (text.equals(CMD_START)) {
                handleStartCommand(chatIdStr, user);
                return;
            }

            if (handleDialogState(chatIdStr, state, text, user, update)) {
                return;
            }

            if (user == null) {
                sendMessage(chatIdStr, "❌ Сначала отправь /start.", null);
                return;
            }

            if (handleWeatherCommands(chatIdStr, user, text)) {
                return;
            }

            if (text.equals(BTN_SETTINGS) || text.equals(CMD_SETTINGS)) {
                sendMessage(chatIdStr, "⚙️ Что хочешь изменить?", KeyboardUtils.getSettingsMenu());
                return;
            }

            if (text.equals(BTN_CHANGE_NAME) || text.equals(CMD_CHANGE_NAME)) {
                userStates.put(chatIdStr, AWAITING_NEW_NAME);
                sendMessage(chatIdStr, "✏️ Введи новое имя:", null);
                return;
            }

            if (text.equals(BTN_CHANGE_CITY) || text.equals(CMD_CHANGE_CITY)) {
                userStates.put(chatIdStr, AWAITING_NEW_CITY);
                sendMessage(chatIdStr, "✏️ Введи новый город:", null);
                return;
            }

            if (text.equals(BTN_BACK) || text.equals(CMD_BACK)) {
                sendMessage(chatIdStr, "📋 Главное меню", KeyboardUtils.getMainMenu());
                return;
            }

            if (text.equals(BTN_HELP) || text.equals(CMD_HELP)) {
                sendMessage(chatIdStr,
                        "📋 Доступные команды:\n" +
                                "🔹 /start - регистрация\n" +
                                "🔹 /weather - погода сейчас\n" +
                                "🔹 /help - помощь",
                        KeyboardUtils.getMainMenu());
                return;
            }

            sendMessage(chatIdStr, "❓ Неизвестная команда. Напиши /help.", null);

        } catch (SQLException e) {
            logger.error("Ошибка базы данных",e);
            sendMessage(chatIdStr, "❌ Ошибка базы данных.", null);
        } catch (Exception e) {
            logger.error("Ошибка базы данных",e);
            sendMessage(chatIdStr, "❌ Ошибка: " + e.getMessage(), null);
        }
    }

    private boolean handleDialogState(String chatIdStr, String state, String text, User user, Update update) throws SQLException {
        if (AWAITING_NAME.equals(state)) {
            userStates.put(chatIdStr, AWAITING_CITY);
            userStates.put(chatIdStr + "_name", text);
            sendMessage(chatIdStr, "Отлично, " + text + "! 🌍 Из какого ты города?", null);
            return true;
        }

        if (AWAITING_NEW_NAME.equals(state)) {
            user.setFirstName(text);
            userStorage.saveUser(user);
            userStates.remove(chatIdStr);
            sendMessage(chatIdStr, "✅ Имя изменено на: " + text, KeyboardUtils.getMainMenu());
            return true;
        }

        if (AWAITING_CITY.equals(state)) {
            String name = userStates.get(chatIdStr + "_name");
            User newUser = new User(
                    chatIdStr,
                    update.getMessage().getFrom().getUserName(),
                    name,
                    text,
                    LocalDateTime.now()
            );
            userStorage.saveUser(newUser);
            userStates.remove(chatIdStr);
            userStates.remove(chatIdStr + "_name");
            sendMessage(chatIdStr,
                    "✅ Регистрация завершена, " + name + "! Твой город: " + text +
                            "\nТеперь я буду присылать тебе погоду.",
                    KeyboardUtils.getMainMenu());
            return true;
        }

        if (AWAITING_NEW_CITY.equals(state)) {
            user.setCity(text);
            userStorage.saveUser(user);
            userStates.remove(chatIdStr);
            sendMessage(chatIdStr, "✅ Город изменен на: " + text, KeyboardUtils.getMainMenu());
            return true;
        }

        return false;
    }

    private void handleStartCommand(String chatIdStr, User user) {
        if (user != null) {
            sendMessage(chatIdStr,
                    "🌤️ С возвращением, " + user.getFirstName() + "! Твой город: " + user.getCity() +
                            "\nИспользуй меню ниже для получения погоды.",
                    KeyboardUtils.getMainMenu());
        } else {
            userStates.put(chatIdStr, AWAITING_NAME);
            String welcomeMessage = "🌤️ Добро пожаловать в Метеор!\n\n" +
                    "Я твой персональный гид по погоде. Давай познакомимся!\n\n" +
                    "Как тебя зовут?";
            sendMessage(chatIdStr, welcomeMessage, null);
        }
    }

    private boolean handleWeatherCommands(String chatIdStr, User user, String text) {
        if (text.equals(BTN_WEATHER_NOW) || text.equals(CMD_WEATHER)) {
            handleCurrentWeather(chatIdStr, user);
            return true;
        }

        if (text.equals(BTN_FORECAST)) {
            sendMessage(chatIdStr, "📊 Выберите срок прогноза:", KeyboardUtils.getForecastMenu());
            return true;
        }

        if (text.equals(BTN_TODAY)) {
            handleHourlyForecast(chatIdStr, user);
            return true;
        }

        if (text.equals(BTN_3_DAYS)) {
            handleDailyForecast(chatIdStr, user, 3);
            return true;
        }

        if (text.equals(BTN_WEEK)) {
            handleDailyForecast(chatIdStr, user, 7);
            return true;
        }

        if (text.equals(BTN_GEOLOCATION)) {
            sendMessage(chatIdStr,
                    "📍 Нажмите кнопку ниже, чтобы отправить свою геолокацию:",
                    KeyboardUtils.getLocationRequestKeyboard());
            return true;
        }

        return false;
    }

    private void sendMessage(String chatId, String text, ReplyKeyboardMarkup keyboard) {
        logger.info("Отправка сообщения в чат {}: {}", chatId, text.substring(0, Math.min(text.length(), 100)));
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        if (keyboard != null) {
            message.setReplyMarkup(keyboard);
        }
        try {
            execute(message);
            logger.info("Сообщение успешно отправлено в чат {}", chatId);
        } catch (TelegramApiException e) {
            logger.error("Ошибка отправки сообщения в чат {}", chatId, e);
        }
    }

    private void handleLocation(Update update, String chatIdStr) {
        double lat = update.getMessage().getLocation().getLatitude();
        double lon = update.getMessage().getLocation().getLongitude();

        logger.info("Получена геолокация: lat={}, lon={}", lat, lon);

        try {
            logger.info("Запрашиваем погоду...");
            WeatherResponse response = weatherService.getWeather(lat, lon, 1);
            logger.info("Погода получена, response: {}", response != null ? "не null" : "null");

            logger.info("Определяем город...");
            String cityName = geocoder.getCityNameByCoords(lat, lon);
            logger.info("Город определён: {}", cityName);

            logger.info("Форматируем сообщение...");
            String weatherMessage = messageService.formatCurrentWeather(cityName, response);
            logger.info("Сообщение сформировано");

            String message = weatherMessage +
                    "\n📍 Определение местоположения: Яндекс.Карты";
            logger.info("Отправляем сообщение...");
            sendMessage(chatIdStr, message, KeyboardUtils.getMainMenu());
            logger.info("Сообщение отправлено");

        } catch (Exception e) {
            logger.error("Ошибка получения погоды по геолокации", e);
            sendMessage(chatIdStr, "Ошибка получения погоды по геолокации: " + e.getMessage(), null);
        }
    }

    private void handleCurrentWeather(String chatIdStr, User user) {
        try {
            String city = user.getCity();
            if (city == null || city.trim().isEmpty()) {
                sendMessage(chatIdStr, "❌ Город не указан. Используй Настройки -> Изменить город.", null);
                return;
            }

            double[] coords = geocoder.getCoordinates(city.trim());
            WeatherResponse response = weatherService.getWeather(coords[0], coords[1], 1);

            String message = messageService.formatCurrentWeather(city, response);
            sendMessage(chatIdStr, message, KeyboardUtils.getMainMenu());

        } catch (Exception e) {
            logger.error("Ошибка получения текущей погоды",e);
            sendMessage(chatIdStr, "❌ Ошибка: " + e.getMessage(), null);
        }
    }

    private void handleHourlyForecast(String chatIdStr, User user) {
        try {
            String city = user.getCity();
            if (city == null || city.trim().isEmpty()) {
                sendMessage(chatIdStr, "❌ Город не указан.", null);
                return;
            }

            double[] coords = geocoder.getCoordinates(city.trim());
            WeatherResponse response = weatherService.getWeather(coords[0], coords[1], 1);

            List<HourlyForecast> hourly = response.getHourlyForecasts();
            if (hourly == null || hourly.isEmpty()) {
                sendMessage(chatIdStr, "❌ Нет почасовых данных.", null);
                return;
            }

            StringBuilder message = new StringBuilder("⏰ ПОЧАСОВОЙ ПРОГНОЗ В " + city.toUpperCase() + " (24 ЧАСА):\n");
            message.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

            int count = Math.min(hourly.size(), 24);
            for (int i = 0; i < count; i++) {
                HourlyForecast hour = hourly.get(i);
                String time = weatherService.formatTime(hour.getTime());
                String temp = String.format("%.1f", hour.getTemperature());
                String precip = hour.getPrecipitationProbability() + "%";

                String timeEmoji = messageService.getTimeEmoji(time);
                String weatherEmoji = hour.getPrecipitationProbability() > 50 ? "🌧️" : "☀️";

                message.append(timeEmoji)
                        .append(" ")
                        .append(time)
                        .append("  🌡️ ")
                        .append(temp)
                        .append("°C  ")
                        .append(weatherEmoji)
                        .append("  💧")
                        .append(precip)
                        .append("\n");
            }

            message.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            message.append("📌 Данные о погоде: Open-Meteo.com");
            sendMessage(chatIdStr, message.toString(), KeyboardUtils.getMainMenu());

        } catch (Exception e) {
            logger.error("Ошибка получения почасового прогноза",e);
            sendMessage(chatIdStr, "❌ Ошибка: " + e.getMessage(), null);
        }
    }

    private void handleDailyForecast(String chatIdStr, User user, int days) {
        try {
            String city = user.getCity();
            if (city == null || city.trim().isEmpty()) {
                sendMessage(chatIdStr, "❌ Город не указан.", null);
                return;
            }

            double[] coords = geocoder.getCoordinates(city.trim());
            WeatherResponse response = weatherService.getWeather(coords[0], coords[1], days);

            List<DailyForecast> forecast = response.getDailyForecasts();
            if (forecast == null || forecast.isEmpty()) {
                sendMessage(chatIdStr, "❌ Нет данных прогноза.", null);
                return;
            }

            int count = Math.min(forecast.size(), days);
            String period = days == 1 ? "1 ДЕНЬ" : days + " ДНЯ";

            StringBuilder message = new StringBuilder("📅 ПРОГНОЗ ПОГОДЫ В " + city.toUpperCase() + " НА " + period + ":\n");
            message.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

            for (int i = 0; i < count; i++) {
                DailyForecast day = forecast.get(i);
                String emoji = messageService.getWeatherEmoji(day.getWeatherCode());
                String dayName = messageService.getDayName(day.getDate());

                message.append("📆 ")
                        .append(dayName)
                        .append(" ")
                        .append(weatherService.formatDate(day.getDate()))
                        .append(":\n")
                        .append("   ")
                        .append(emoji)
                        .append(" ")
                        .append(day.getDescription())
                        .append("\n")
                        .append("   🌡️ ")
                        .append(day.getTempMin())
                        .append("...")
                        .append(day.getTempMax())
                        .append("°C\n")
                        .append("   🌅 Рассвет: ")
                        .append(weatherService.formatTime(day.getSunrise()))
                        .append("  🌇 Закат: ")
                        .append(weatherService.formatTime(day.getSunset()))
                        .append("\n\n");
            }

            message.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            message.append("📌 Данные о погоде: Open-Meteo.com");
            sendMessage(chatIdStr, message.toString(), KeyboardUtils.getMainMenu());

        } catch (Exception e) {
            logger.error("Ошибка получения погоды на заданные день", e);
            sendMessage(chatIdStr, "❌ Ошибка: " + e.getMessage(), null);
        }
    }
}
package ru.sheraAn.service;

import ru.sheraAn.model.WeatherResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sheraAn.service.interfaces.MessageService;

public class MessageFormatter implements MessageService {
    private final static Logger logger = LoggerFactory.getLogger(MessageFormatter.class);

    public String formatCurrentWeather(String city, WeatherResponse response) {
        try {
            if (response == null) {
                logger.error("WeatherResponse is null");
                return "⚠️ Данные о погоде недоступны";
            }
            if (response.getWeather() == null || response.getWeather().isEmpty()) {
                logger.error("Weather list is null or empty");
                return "⚠️ Данные о погоде недоступны";
            }
            if (response.getMain() == null) {
                logger.error("WeatherMain is null");
                return "⚠️ Данные о погоде недоступны";
            }
            if (response.getSys() == null) {
                logger.error("Sys is null");
                return "⚠️ Данные о погоде недоступны";
            }

            String description = response.getWeather().get(0).getDescription();
            double temp = response.getMain().getTemp();
            double feelsLike = response.getMain().getFeelsLike();
            int humidity = response.getMain().getHumidity();
            double pressure = response.getSys().getPressure();
            double windSpeed = response.getSys().getWindSpeed();

            String emoji = getWeatherEmojiByDescription(description);

            return "🌤️ Погода в " + city + ":\n" +
                    "━━━━━━━━━━━━━━━━━\n" +
                    emoji + " Состояние: " + description + "\n" +
                    "🌡️ Температура: " + temp + "°C\n" +
                    "🤔 Ощущается как: " + feelsLike + "°C\n" +
                    "💧 Влажность: " + humidity + "%\n" +
                    "💨 Ветер: " + windSpeed + " км/ч\n" +
                    "📊 Давление: " + pressure + " мм рт. ст.\n" +
                    "━━━━━━━━━━━━━━━━━\n" +
                    "📌 Данные о погоде: Open-Meteo.com";
        } catch (Exception e) {
            logger.error("Ошибка форматирования погоды", e);
            return "⚠️ Ошибка форматирования данных";
        }
    }
    public String getTimeEmoji(String time) {
        try {
            int hour = Integer.parseInt(time.split(":")[0]);
            if (hour >= 6 && hour < 12) return "🌅";
            if (hour >= 12 && hour < 18) return "☀️";
            if (hour >= 18 && hour < 22) return "🌇";
            return "🌙";
        } catch (Exception e) {
            logger.error("Ошибка получения эмоди времени суток",e);
            return "🕐";
        }
    }

    public String getDayName(String dateStr) {
        try {
            java.time.LocalDate date = java.time.LocalDate.parse(dateStr);
            java.time.DayOfWeek day = date.getDayOfWeek();
            return switch (day) {
                case MONDAY -> "ПН";
                case TUESDAY -> "ВТ";
                case WEDNESDAY -> "СР";
                case THURSDAY -> "ЧТ";
                case FRIDAY -> "ПТ";
                case SATURDAY -> "СБ";
                case SUNDAY -> "ВС";
            };
        } catch (Exception e) {
            logger.error("Ошибка получения дня недели",e);
            return "";
        }
    }

    public String getWeatherEmoji(int code) {
        return switch (code) {
            case 0 -> "☀️";
            case 1, 2 -> "⛅";
            case 3 -> "☁️";
            case 45, 48 -> "🌫️";
            case 51, 53, 55 -> "🌦️";
            case 61, 63, 65 -> "🌧️";
            case 71, 73, 75 -> "❄️";
            case 80, 81, 82 -> "🌧️";
            case 95, 96, 99 -> "⛈️";
            default -> "❓";
        };
    }

    public String getWeatherEmojiByDescription(String description) {
        if (description.contains("Ясно")) return "☀️";
        if (description.contains("Малооблачно")) return "⛅";
        if (description.contains("Облачно")) return "☁️";
        if (description.contains("Туман")) return "🌫️";
        if (description.contains("Морось")) return "🌦️";
        if (description.contains("Дождь")) return "🌧️";
        if (description.contains("Снег")) return "❄️";
        if (description.contains("Ливень")) return "🌧️";
        if (description.contains("Гроза")) return "⛈️";
        return "❓";
    }
}

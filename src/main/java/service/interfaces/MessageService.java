package service.interfaces;

import model.WeatherResponse;

public interface MessageService {


        String formatCurrentWeather(String city, WeatherResponse response);

        String getTimeEmoji(String time);

        String getDayName(String dateStr);

        String getWeatherEmoji(int code);

        String getWeatherEmojiByDescription(String description);

}

package ru.sheraAn.service.interfaces;

import ru.sheraAn.model.WeatherResponse;

import java.io.IOException;

public interface WeatherService {
    WeatherResponse getWeather(double lat, double lon, int days) throws IOException, InterruptedException;
        String formatTime(String timeStr);
        String formatDate(String dateStr);

}

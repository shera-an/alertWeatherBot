package service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import config.OpenMeteoConfig;
import model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.interfaces.WeatherService;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class OpenMeteoService implements WeatherService {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final static Logger logger = LoggerFactory.getLogger(OpenMeteoService.class);
    public WeatherResponse getWeather(double lat, double lon, int days) throws IOException, InterruptedException {
        String url = String.format(
                OpenMeteoConfig.getUrl() +
                        "?latitude=%s&longitude=%s" +
                        "&current=temperature_2m,relative_humidity_2m,apparent_temperature,pressure_msl,wind_speed_10m,weather_code" +
                        "&daily=temperature_2m_max,temperature_2m_min,weather_code,sunrise,sunset" +
                        "&hourly=temperature_2m,precipitation_probability" +
                        "&timezone=auto" +
                        "&forecast_days=%d",
                lat, lon, days
        );
        logger.info("Запрос к Open-Meteo: {}", url);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        logger.info( "Статус ответа: {}",response.statusCode());
        if (response.statusCode() != 200) {
            throw new IOException("Ошибка API: " + response.statusCode() + " - " + response.body());
        }

        return parseResponse(response.body());
    }

    private WeatherResponse parseResponse(String jsonBody) throws IOException {
        JsonNode root = objectMapper.readTree(jsonBody);
        JsonNode current = root.path("current");
        JsonNode daily = root.path("daily");
        JsonNode hourly = root.path("hourly");

        double temp = current.path("temperature_2m").asDouble();
        int humidity = current.path("relative_humidity_2m").asInt();
        double feelsLike = current.path("apparent_temperature").asDouble();
        double pressure = current.path("pressure_msl").asDouble();
        double windSpeed = current.path("wind_speed_10m").asDouble();
        int weatherCode = current.path("weather_code").asInt();

        List<DailyForecast> dailyForecasts = new ArrayList<>();
        JsonNode timeArray = daily.path("time");
        JsonNode tempMaxArray = daily.path("temperature_2m_max");
        JsonNode tempMinArray = daily.path("temperature_2m_min");
        JsonNode weatherCodeArray = daily.path("weather_code");
        JsonNode sunriseArray = daily.path("sunrise");
        JsonNode sunsetArray = daily.path("sunset");

        for (int i = 0; i < timeArray.size(); i++) {
            DailyForecast forecast = new DailyForecast();
            forecast.setDate(timeArray.get(i).asText());
            forecast.setTempMax(tempMaxArray.get(i).asDouble());
            forecast.setTempMin(tempMinArray.get(i).asDouble());
            forecast.setWeatherCode(weatherCodeArray.get(i).asInt());
            forecast.setSunrise(sunriseArray.get(i).asText());
            forecast.setSunset(sunsetArray.get(i).asText());
            dailyForecasts.add(forecast);
        }

        List<HourlyForecast> hourlyForecasts = new ArrayList<>();
        JsonNode hourlyTimeArray = hourly.path("time");
        JsonNode hourlyTempArray = hourly.path("temperature_2m");
        JsonNode hourlyPrecipArray = hourly.path("precipitation_probability");

        for (int i = 0; i < hourlyTimeArray.size(); i++) {
            HourlyForecast hour = new HourlyForecast();
            hour.setTime(hourlyTimeArray.get(i).asText());
            hour.setTemperature(hourlyTempArray.get(i).asDouble());
            hour.setPrecipitationProbability(hourlyPrecipArray.get(i).asInt());
            hourlyForecasts.add(hour);
        }

        WeatherResponse response = new WeatherResponse();
        response.setName(root.path("timezone").asText());

        WeatherMain main = new WeatherMain();
        main.setTemp(temp);
        main.setHumidity(humidity);
        main.setFeelsLike(feelsLike);
        response.setMain(main);

        WeatherDescription description = new WeatherDescription();
        description.setDescription(translateWeatherCode(weatherCode));
        response.setWeather(List.of(description));

        Sys sys = new Sys();
        sys.setPressure(pressure);
        sys.setWindSpeed(windSpeed);
        response.setSys(sys);

        response.setDailyForecasts(dailyForecasts);
        response.setHourlyForecasts(hourlyForecasts);

        logger.info("Погода получена. Часов в прогнозе: {}", hourlyForecasts.size());


        return response;
    }

    public String formatTime(String timeStr) {
        try {
            java.time.LocalDateTime dateTime = java.time.LocalDateTime.parse(timeStr);
            return dateTime.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
        } catch (Exception e) {
            logger.error("Ошибка formatTime", e);
            return timeStr;
        }
    }

    public String formatDate(String dateStr) {
        try {
            java.time.LocalDate date = java.time.LocalDate.parse(dateStr);
            return date.format(java.time.format.DateTimeFormatter.ofPattern("dd.MM"));
        } catch (Exception e) {
            logger.error("Ошибка formatDate:", e);
            return dateStr;
        }
    }

    private String translateWeatherCode(int code) {
        return switch (code) {
            case 0 -> "Ясно";
            case 1, 2 -> "Малооблачно";
            case 3 -> "Облачно";
            case 45, 48 -> "Туман";
            case 51, 53, 55 -> "Морось";
            case 61, 63, 65 -> "Дождь";
            case 71, 73, 75 -> "Снег";
            case 80, 81, 82 -> "Ливень";
            case 95, 96, 99 -> "Гроза";
            default -> "Неизвестно";
        };
    }
}
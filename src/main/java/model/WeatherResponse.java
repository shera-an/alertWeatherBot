package model;

import java.util.List;

public class WeatherResponse {
    private String name;
    private WeatherMain main;
    private List<WeatherDescription> weather;
    private Sys sys;
    private List<DailyForecast> dailyForecasts;
    private List<HourlyForecast> hourlyForecasts;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public WeatherMain getMain() { return main; }
    public void setMain(WeatherMain main) { this.main = main; }
    public List<WeatherDescription> getWeather() { return weather; }
    public void setWeather(List<WeatherDescription> weather) { this.weather = weather; }
    public Sys getSys() { return sys; }
    public void setSys(Sys sys) { this.sys = sys; }
    public List<DailyForecast> getDailyForecasts() { return dailyForecasts; }
    public void setDailyForecasts(List<DailyForecast> dailyForecasts) { this.dailyForecasts = dailyForecasts; }
    public List<HourlyForecast> getHourlyForecasts() { return hourlyForecasts; }
    public void setHourlyForecasts(List<HourlyForecast> hourlyForecasts) { this.hourlyForecasts = hourlyForecasts; }
}
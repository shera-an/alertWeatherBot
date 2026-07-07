package ru.sheraAn.model;

public class DailyForecast {
    private String date;
    private double tempMax;
    private double tempMin;
    private int weatherCode;
    private String sunrise;
    private String sunset;

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public double getTempMax() { return tempMax; }
    public void setTempMax(double tempMax) { this.tempMax = tempMax; }
    public double getTempMin() { return tempMin; }
    public void setTempMin(double tempMin) { this.tempMin = tempMin; }
    public int getWeatherCode() { return weatherCode; }
    public void setWeatherCode(int weatherCode) { this.weatherCode = weatherCode; }
    public String getSunrise() { return sunrise; }
    public void setSunrise(String sunrise) { this.sunrise = sunrise; }
    public String getSunset() { return sunset; }
    public void setSunset(String sunset) { this.sunset = sunset; }

    public String getDescription() {
        return switch (weatherCode) {
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
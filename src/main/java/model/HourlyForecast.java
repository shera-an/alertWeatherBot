package model;

public class HourlyForecast {
    private String time;
    private double temperature;
    private int precipitationProbability;

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }
    public int getPrecipitationProbability() { return precipitationProbability; }
    public void setPrecipitationProbability(int precipitationProbability) { this.precipitationProbability = precipitationProbability; }
}
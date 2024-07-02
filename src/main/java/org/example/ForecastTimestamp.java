package org.example;

public class ForecastTimestamp {
    private String forecastTimeUtc;
    private double airTemperature;
    private double feelsLikeTemperature;
    private double windSpeed;
    private String conditionCode;

    public String getForecastTimeUtc() {
        return forecastTimeUtc;
    }

    public void setForecastTimeUtc(String forecastTimeUtc) {
        this.forecastTimeUtc = forecastTimeUtc;
    }

    public double getAirTemperature() {
        return airTemperature;
    }

    public void setAirTemperature(double airTemperature) {
        this.airTemperature = airTemperature;
    }

    public double getFeelsLikeTemperature() {
        return feelsLikeTemperature;
    }

    public void setFeelsLikeTemperature(double feelsLikeTemperature) {
        this.feelsLikeTemperature = feelsLikeTemperature;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getConditionCode() {
        return conditionCode;
    }

    public void setConditionCode(String conditionCode) {
        this.conditionCode = conditionCode;
    }

    @Override
    public String toString() {
        return "ForecastTimestamp{" +
                "forecastTimeUtc='" + forecastTimeUtc + '\'' +
                ", airTemperature=" + airTemperature +
                ", feelsLikeTemperature=" + feelsLikeTemperature +
                ", windSpeed=" + windSpeed +
                ", conditionCode='" + conditionCode + '\'' +
                '}';
    }
}
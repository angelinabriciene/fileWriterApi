package org.example;

public class Observation {

    private String observationTimeUtc;
    private double airTemperature;
    private double feelsLikeTemperature;
    private double windSpeed;
    private String conditionCode;

    public Observation() {
    }

    public Observation(String observationTimeUtc, double airTemperature, double feelsLikeTemperature, double windSpeed, String conditionCode) {
        this.observationTimeUtc = observationTimeUtc;
        this.airTemperature = airTemperature;
        this.feelsLikeTemperature = feelsLikeTemperature;
        this.windSpeed = windSpeed;
        this.conditionCode = conditionCode;
    }

    public String getObservationTimeUtc() {
        return observationTimeUtc;
    }

    public void setObservationTimeUtc(String observationTimeUtc) {
        this.observationTimeUtc = observationTimeUtc;
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
}
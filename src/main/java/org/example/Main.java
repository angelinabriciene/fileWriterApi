package org.example;

import com.google.gson.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static Gson gson;
    public static void main(String[] args) {
        gson = new Gson();

        API();


    }

    private static void API() {
        System.out.println("Įveskite miestą, kurio orus norite pamatyti");
        Scanner sc = new Scanner(System.in);;
        String place = sc.nextLine();

        try {
            URL url = new URL("https://api.meteo.lt/v1/places/" + place + "/forecasts/long-term");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
            String response = "";
            String line;
            while ((line = reader.readLine()) != null) {
                response += line;
            }
            JsonElement jsonElement = JsonParser.parseString(response);
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            JsonObject placeObject = jsonObject.getAsJsonObject("place");

            Place placeInfo = new Place();
            placeInfo.setCode(placeObject.get("code").getAsString());
            placeInfo.setName(placeObject.get("name").getAsString());
            placeInfo.setAdministrativeDivision(placeObject.get("administrativeDivision").getAsString());
            placeInfo.setCountryCode(placeObject.get("countryCode").getAsString());
            placeInfo.setCoordinates(String.valueOf(new Coordinates(
                    placeObject.getAsJsonObject("coordinates").get("latitude").getAsString(),
                    placeObject.getAsJsonObject("coordinates").get("longitude").getAsString()
            )));

            System.out.println("Miestas: " + placeInfo.getName());
            System.out.println("Savivaldybė: " + placeInfo.getAdministrativeDivision());

            JsonArray forecastArray = jsonObject.getAsJsonArray("forecastTimestamps");
            List<ForecastTimestamp> forecastList = new ArrayList<>();

            for (JsonElement element : forecastArray) {
                JsonObject forecastObject = element.getAsJsonObject();
                ForecastTimestamp forecast = new ForecastTimestamp();
                forecast.setForecastTimeUtc(forecastObject.get("forecastTimeUtc").getAsString());
                forecast.setAirTemperature(forecastObject.get("airTemperature").getAsDouble());
                forecast.setFeelsLikeTemperature(forecastObject.get("feelsLikeTemperature").getAsDouble());
                forecast.setWindSpeed(forecastObject.get("windSpeed").getAsDouble());
                forecast.setConditionCode(forecastObject.get("conditionCode").getAsString());
                forecastList.add(forecast);
            }

            System.out.println("\nOrų prognozė:");
            System.out.println("-------------------------------");
            for (ForecastTimestamp forecast : forecastList) {
                System.out.println("Prognozės laikas: " + forecast.getForecastTimeUtc());
                System.out.println("Oro temperatūra: " + forecast.getAirTemperature());
                System.out.println("Jutinimė temperatūra: " + forecast.getFeelsLikeTemperature());
                System.out.println("Vėjo greitis: " + forecast.getWindSpeed());
                System.out.println("Būsena: " + forecast.getConditionCode());
                System.out.println();
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void addPlace(Place place){
        List<Place> places = getPlaces();
        places.add(place);
        updateJson(places);
    }

    public static void updateJson(List<Place> places){
        try(FileWriter writer = new FileWriter("places.json")) {
            gson.toJson(places,writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Place getPlace(String code) {
        try(FileReader reader = new FileReader("places.json")){
            JsonElement jsonElement = JsonParser.parseReader(reader);
            JsonArray jsonArray = jsonElement.getAsJsonArray();

            for (JsonElement element : jsonArray) {
                JsonObject jsonObject = element.getAsJsonObject();

                String placeCode = jsonObject.get("code").getAsString();
                if (code.equals(placeCode)) {
                    String name = jsonObject.get("name").getAsString();
                    String administrativeDivision = jsonObject.get("administrativeDivision").getAsString();
                    String countryCode = jsonObject.get("countryCode").getAsString();
                    String coordinates = jsonObject.get("coordinates").getAsString();

                    Place place = new Place();
                    place.setCode(placeCode);
                    place.setName(name);
                    place.setAdministrativeDivision(administrativeDivision);
                    place.setCountryCode(countryCode);
                    place.setCoordinates(coordinates);
                    return place;
                }
            }
        }catch (Exception e){
            System.out.println(e);
            System.out.println("boooooom");
        }
        return new Place();
    }

    public static List<Place> getPlaces() {
        List<Place> places = new ArrayList<>();
        try(FileReader reader = new FileReader("places.json")){
            JsonElement jsonElement = JsonParser.parseReader(reader);
            JsonArray jsonArray = jsonElement.getAsJsonArray();

            for (JsonElement element : jsonArray) {
                JsonObject jsonObject = element.getAsJsonObject();
                String placeCode = jsonObject.get("code").getAsString();
                String name = jsonObject.get("name").getAsString();
                String administrativeDivision = jsonObject.get("administrativeDivision").getAsString();
                String countryCode = jsonObject.get("countryCode").getAsString();
                String coordinates = jsonObject.get("coordinates").getAsString();

                Place place = new Place();
                place.setCode(placeCode);
                place.setName(name);
                place.setAdministrativeDivision(administrativeDivision);
                place.setCountryCode(countryCode);
                Coordinates Coordinates = new Coordinates();
                place.setCoordinates(String.valueOf(Coordinates));
                places.add(place);
            }
        }catch (Exception e){
            System.out.println(e);
            System.out.println("boooooom");
        }
        return places;
    }

    public static void updatePlace (Place place) {
        List<Place> places = getPlaces();
        places.stream()
                .filter(p -> p.equals(place))
                .findFirst()
                .map(p -> places.set(places.indexOf(p), place))
                .orElseThrow(() -> new IllegalArgumentException("Place with code " + place.getCode() + " not found."));
        updateJson(places);
    }

    public static void deletePlace(Place place) {
        List<Place> places = getPlaces();
        places.stream()
                .filter(p -> p.equals(place))
                .findFirst()
                .map(p -> places.remove(place))
                .orElseThrow(() -> new IllegalArgumentException("Place with code " + place.getCode() + " not found."));
        updateJson(places);
    }


}
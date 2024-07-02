package org.example;

import com.google.gson.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
    public static Gson gson;

    public static void main(String[] args) {

        int input;

        while (true) {
            System.out.println("Pasirinkime norimą paslaugą įvesdami skaičių");
            System.out.println("1. Orai šiandien pagal miestą");
            System.out.println("2. Orų istorija pagl datą");
            System.out.println("3. Rinktis iš miestų sąrašo ir pamatyti orų prognozę");
            System.out.println("4. Išeiti iš meniu");
            Scanner sc = new Scanner(System.in);
            input = sc.nextInt();

            switch (input) {
                case 1:
                    getUserInputAndCallAPI();
                    break;
                case 2:
                    getStationHistoryFromAPI();
                    break;
                case 3:
                    getCitiesFromApi();
                    break;
                case 4:
                    System.out.println("Ačiū, kad apsilankėte!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Netinkamas pasirinkimas, bandykite dar kartą. Įveskite skaičių");
            }
        }

    }

    private static void getStationHistoryFromAPI() {
        System.out.println("Įveskite orų stotelės miestą (formatas: Vilniaus) :D ");
        Scanner sc = new Scanner(System.in);
        String stationName = sc.nextLine();

        System.out.println("Įveskite datą (formatas: yyyy-MM-dd)");
        String date = sc.nextLine();

        try {
            URL url = new URL("https://api.meteo.lt/v1/stations/" + stationName + "-ams/observations/" + date);

            System.out.println("https://api.meteo.lt/v1/stations/" + stationName + "-ams/observations/" + date);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
            String response = "";
            String line;
            while ((line = reader.readLine()) != null) {
                response += line;
            }
            reader.close();
            con.disconnect();

            JsonElement jsonElement = JsonParser.parseString(response);
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            System.out.println(stationName + " orų stotelės istorija " + date + " dienai");
            System.out.println("-------------------------------");

            JsonArray forecastArray = jsonObject.getAsJsonArray("observations");
            List<Observation> forecastList = new ArrayList<>();

            for (JsonElement element : forecastArray) {
                JsonObject forecastObject = element.getAsJsonObject();
                Observation observation = new Observation();
                observation.setObservationTimeUtc(forecastObject.get("observationTimeUtc").getAsString());
                observation.setAirTemperature(forecastObject.get("airTemperature").getAsDouble());
                observation.setFeelsLikeTemperature(forecastObject.get("feelsLikeTemperature").getAsDouble());
                observation.setWindSpeed(forecastObject.get("windSpeed").getAsDouble());
                observation.setConditionCode(forecastObject.get("conditionCode").getAsString());
                forecastList.add(observation);
            }
            for (Observation forecast : forecastList) {
                System.out.println("Prognozės laikas: " + forecast.getObservationTimeUtc());
                System.out.println("Oro temperatūra: " + forecast.getAirTemperature());
                System.out.println("Jutinimė temperatūra: " + forecast.getFeelsLikeTemperature());
                System.out.println("Vėjo greitis: " + forecast.getWindSpeed());
                System.out.println("Būsena: " + forecast.getConditionCode());
                System.out.println();
            }
        } catch (Exception e) {
            System.out.println("Klaida " + e.getMessage());
        }
    }

    private static void getUserInputAndCallAPI() {
        System.out.println("Įveskite miestą, kurio orų prognozę norite pamatyti");
        Scanner sc = new Scanner(System.in);
        String place = sc.nextLine();
        API(place);
    }

    private static String getUserInput() {
        Scanner sc = new Scanner(System.in);
        return sc.nextLine();
    }

    private static void API(String place) {
        Scanner sc = new Scanner(System.in);

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
            reader.close();
            con.disconnect();

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

            ZoneId zoneId = ZoneId.of("Europe/Vilnius");
            LocalDateTime now = LocalDateTime.now(zoneId);

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

            forecastList = forecastList.stream()
                    .filter(f -> LocalDateTime.parse(f.getForecastTimeUtc(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).atZone(zoneId).toLocalDateTime().isAfter(now.minusMinutes(now.getMinute()).minusSeconds(now.getSecond()))
                            && LocalDateTime.parse(f.getForecastTimeUtc(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).atZone(zoneId).toLocalDateTime().toLocalDate().equals(now.toLocalDate()))
                    .collect(Collectors.toList());

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
            System.out.println("Įvesta vietovė neturi meteorologinio stebėjimo stotelės, įveskite kitą jums artimiausią vietovę.Ar norite rinktis iš sąrašo? Įveskite TAIP arba NE");
            String answer = sc.nextLine();
            if (answer.equalsIgnoreCase("taip")) {
                getCitiesFromApi();
            } else {
                System.out.println("Įveskite kitą miestą:");
                String newPlace = getUserInput();
                API(newPlace);
            }
        }
    }

    private static void getCitiesFromApi() {
        Scanner sc = new Scanner(System.in);
        List<String> citieNames = getCities();
        for (int i = 0; i < citieNames.size(); i++) {
            System.out.println((i + 1) + ". " + citieNames.get(i));
        }
        System.out.println("-------------------------------");
        System.out.println("Pasirinkite miestą įvesdami jo skaičių");
        int cityChoice = sc.nextInt();
        sc.nextLine();
        List<String> cities = citieNames;
        String selectedCity = cities.get(cityChoice - 1);
        API(selectedCity);
    }

    private static List<String> getCities() {
        List<String> cities = new ArrayList<>();
        try {
            URL url = new URL("https://api.meteo.lt/v1/places");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
            String response = "";
            String line;
            while ((line = reader.readLine()) != null) {
                response += line;
            }
            reader.close();
            con.disconnect();

            JsonElement jsonElement = JsonParser.parseString(response);
            JsonArray jsonArray = jsonElement.getAsJsonArray();

            for (JsonElement element : jsonArray) {
                JsonObject jsonObject = element.getAsJsonObject();
                String city = jsonObject.get("name").getAsString();
                if (!cities.contains(city)) {
                    cities.add(city);
                }
            }
        } catch (Exception e) {
            System.out.println("Error getting cities: " + e.getMessage());
        }
        return cities;
    }

    public static void addPlace(Place place) {
        List<Place> places = getPlaces();
        places.add(place);
        updateJson(places);
    }

    public static void updateJson(List<Place> places) {
        try (FileWriter writer = new FileWriter("places.json")) {
            gson.toJson(places, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Place getPlace(String code) {
        try (FileReader reader = new FileReader("places.json")) {
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
        } catch (Exception e) {
            System.out.println(e);
            System.out.println("boooooom");
        }
        return new Place();
    }

    public static List<Place> getPlaces() {
        List<Place> places = new ArrayList<>();
        try (FileReader reader = new FileReader("places.json")) {
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
        } catch (Exception e) {
            System.out.println(e);
            System.out.println("boooooom");
        }
        return places;
    }

    public static void updatePlace(Place place) {
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
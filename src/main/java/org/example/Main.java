package org.example;

import com.google.gson.*;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static Gson gson;
    public static void main(String[] args) {
        gson = new Gson();

        Place p = new Place("abromiskes", "Abromiškės", "Elektrėnų savivaldybė", "LT", "latitude: 54.7825, longitude: 24.71032");
        Place p2 = new Place("acokavai", "Acokavai", "Radviliškio rajono savivaldybė", "LT", "latitude: 55.72656, longitude: 23.34748");
        
//        addPlace(p);
//        addPlace(p2);
//        updatePlace(p2);
//        deletePlace(p2);
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
                place.setCoordinates(coordinates);
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
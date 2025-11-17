package data_access;

import entity.Restaurant;
import entity.RestaurantFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.nio.file.Files;
import java.nio.file.Path;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * DAO for restaurant data implemented with json file to persist data
 */
public class JsonRestaurantDataAccessObject {
    private final Map<String, Restaurant> restaurantById = new HashMap<>();

    /**
     * Construct DAO for saving to and reading from a local json file
     * @param jsonPath the json file path to extract data from
     * @param restaurantFactory the factory to create Restaurant objects
     * @throws IOException throws IOException
     */
    public JsonRestaurantDataAccessObject(String jsonPath, RestaurantFactory restaurantFactory) throws IOException {

        try{
            JSONArray restaurantData = new JSONArray(Files.readString(Path.of("src/main/java/data/restaurant.json")));

            for(int i = 0; i < restaurantData.length(); i++){
                JSONObject curObj = restaurantData.getJSONObject(i);
                String restaurantId = curObj.getString("name");
                Restaurant restaurant = restaurantFactory.create(curObj);
                restaurantById.put(restaurantId, restaurant);
            }
        }catch(IOException e){
            throw new IOException(e);
        }
    }

    /**
     * Gets a restaurant by its ID.
     * @param restaurantId the ID of the restaurant
     * @return the Restaurant object, or null if not found
     */
    public Restaurant getRestaurantById(String restaurantId) {
        return restaurantById.get(restaurantId);
    }

    /**
     * Gets multiple restaurants by their IDs.
     * @param restaurantIds list of restaurant IDs
     * @return list of Restaurant objects
     */
    public List<Restaurant> getRestaurantsByIds(List<String> restaurantIds) {
        final List<Restaurant> restaurants = new ArrayList<>();
        for (String restaurantId : restaurantIds) {
            final Restaurant restaurant = restaurantById.get(restaurantId);
            if (restaurant != null) {
                restaurants.add(restaurant);
            }
        }
        return restaurants;
    }

    /**
     * For debugging
     */
    public void printAllNames(){
        for(Restaurant restaurant : restaurantById.values()){
            System.out.println(restaurant.getName());
        }
    }
}
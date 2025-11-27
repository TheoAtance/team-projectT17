package data_access;

import entity.Restaurant;
import entity.RestaurantFactory;
import use_case.filter.IRestaurantDataAccess;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.nio.file.Files;
import java.nio.file.Path;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * DAO for restaurant data implemented with json file to persist data
 */
public class JsonRestaurantDataAccessObject implements IRestaurantDataAccess {
    private final Map<String, Restaurant> restaurantById = new HashMap<>();

    /**
     * Construct DAO for saving to and reading from a local json file
     * @param jsonPath the json file path to extract data from
     * @param restaurantFactory factory for creating restaurant objects
     * @throws IOException throws IOException
     */
    public JsonRestaurantDataAccessObject(String jsonPath, RestaurantFactory restaurantFactory) throws IOException {
        System.out.println("Loading restaurant data from: " + jsonPath);
        try {
            JSONArray restaurantData = new JSONArray(Files.readString(Path.of(jsonPath)));
            System.out.println("Loaded " + restaurantData.length() + " restaurants from JSON");

            // Map id to their respective restaurant obj
            for (int i = 0; i < restaurantData.length(); i++) {
                JSONObject curObj = restaurantData.getJSONObject(i);
                String restaurantId = curObj.getString("name");
                Restaurant restaurant = restaurantFactory.create(curObj);
                restaurantById.put(restaurantId, restaurant);
            }

            System.out.println("Successfully loaded " + restaurantById.size() + " restaurants into memory");
        } catch (IOException e) {
            System.err.println("ERROR: Failed to load restaurant data!");
            e.printStackTrace();
            throw new IOException(e);
        }
    }

    /**
     * Get all restaurants of a specific type.
     * @param type the restaurant type to filter by
     * @return list of restaurants matching the type
     */
    @Override
    public List<Restaurant> getRestaurantsByType(String type) {
        System.out.println("Filtering restaurants by type: " + type);
        List<Restaurant> filtered = restaurantById.values().stream()
                .filter(restaurant -> restaurant.getType().equals(type))
                .collect(Collectors.toList());
        System.out.println("Found " + filtered.size() + " restaurants of type: " + type);
        return filtered;
    }

    /**
     * Get all unique restaurant types available.
     * Limited to 5 types for now.
     * @return array of unique restaurant types
     */
    @Override
    public String[] getAllRestaurantTypes() {
        System.out.println("Getting all restaurant types...");
        System.out.println("Total restaurants in memory: " + restaurantById.size());

        String[] types = restaurantById.values().stream()
                .map(Restaurant::getType)
                .distinct()
                .limit(5)
                .toArray(String[]::new);

        System.out.println("Found " + types.length + " unique types");
        return types;
    }

    /**
     * For debugging
     */
    public void printAllNames() {
        for (Restaurant restaurant : restaurantById.values()) {
            System.out.println(restaurant.getName());
        }
    }
}
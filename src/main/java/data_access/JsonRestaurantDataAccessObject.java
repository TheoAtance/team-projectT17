package data_access;

import entity.Restaurant;
import entity.RestaurantFactory;
import use_case.filter.IRestaurantDataAccess;
import use_case.random_restaurant.RandomRestaurantDataAccessInterface;
import use_case.view_restaurant.ViewRestaurantDataAccessInterface;
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
public class JsonRestaurantDataAccessObject implements
        IRestaurantDataAccess,
        ViewRestaurantDataAccessInterface,
        RandomRestaurantDataAccessInterface {

    private final Map<String, Restaurant> restaurantById = new HashMap<>();

    /**
     * Construct DAO for saving to and reading from a local json file
     * @param jsonPath the json file path to extract data from
     * @param restaurantFactory factory for creating restaurant objects
     * @throws IOException throws IOException
     */
    public JsonRestaurantDataAccessObject(String jsonPath, RestaurantFactory restaurantFactory) throws IOException {
        try {
            JSONArray restaurantData = new JSONArray(Files.readString(Path.of(jsonPath)));
            // Map CID to their respective restaurant obj
            for (int i = 0; i < restaurantData.length(); i++) {
                JSONObject curObj = restaurantData.getJSONObject(i);

                // Extract CID from placeUri instead of using name
                String placeUri = curObj.getJSONObject("googleMapsLinks").getString("placeUri");
                String restaurantId = extractCidFromPlaceUri(placeUri);

                Restaurant restaurant = restaurantFactory.create(curObj);
                restaurantById.put(restaurantId, restaurant);
            }
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    /**
     * Extracts the CID parameter from a Google Maps placeUri.
     * Example: "https://maps.google.com/?cid=5193387586656989408&..." -> "5193387586656989408"
     */
    private String extractCidFromPlaceUri(String placeUri) {
        int cidStart = placeUri.indexOf("cid=");
        if (cidStart == -1) {
            throw new IllegalArgumentException("No CID found in placeUri: " + placeUri);
        }

        cidStart += 4; // Move past "cid="
        int cidEnd = placeUri.indexOf('&', cidStart);

        if (cidEnd == -1) {
            // CID is at the end of the URL
            return placeUri.substring(cidStart);
        } else {
            return placeUri.substring(cidStart, cidEnd);
        }
    }

    /**
     * Get all restaurants of a specific type.
     * @param type the restaurant type to filter by
     * @return list of restaurants matching the type
     */
    @Override
    public List<Restaurant> getRestaurantsByType(String type) {
        return restaurantById.values().stream()
                .filter(restaurant -> restaurant.getType().equals(type))
                .collect(Collectors.toList());
    }

    /**
     * Get all unique restaurant types available.
     * Limited to 5 types for now.
     * @return array of unique restaurant types
     */
    @Override
    public String[] getAllRestaurantTypes() {
        return restaurantById.values().stream()
                .map(Restaurant::getType)
                .distinct()
                .limit(5)
                .toArray(String[]::new);
    }


    /**
     * Get restaurant with given id (CID)
     * @param id CID of the restaurant to look up
     * @return restaurant that corresponds to given CID
     */
    @Override
    public Restaurant get(String id) {
        return restaurantById.get(id);
    }

    @Override
    public boolean existById(String id){
        return restaurantById.containsKey(id);
    }

    @Override
    public Restaurant getRandom(){
        List<Restaurant> restaurants = new ArrayList<>(restaurantById.values());
        Random rand = new Random();
        int randomIndex = rand.nextInt(restaurants.size());
        return restaurants.get(randomIndex);
    }
}
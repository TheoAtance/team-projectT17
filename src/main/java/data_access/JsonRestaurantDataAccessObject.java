package data_access;

import entity.Restaurant;
import entity.RestaurantFactory;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import java.nio.file.Files;
import java.nio.file.Path;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * DAO for restaurant data implemented with json file to persist data
 */
public class JsonRestaurantDataAccessObject {
    private final JSONArray restaurantData;
    private final Map<String, Restaurant> restaurantById = new HashMap<>();

    /**
     * Construct DAO for saving to and reading from a local json file
     * @param jsonPath the json file path to extra data from
     * @throws IOException throws IOException
     */
    public JsonRestaurantDataAccessObject(String jsonPath, RestaurantFactory restaurantFactory) throws IOException {

        try{
            restaurantData = new JSONArray(Files.readString(Path.of("src/main/java/data/restaurant.json")));

            for(int i = 0;i < restaurantData.length();i++){
                JSONObject curObj = restaurantData.getJSONObject(i);
                String restaurantId = curObj.getString("name");
                Restaurant restaurant = restaurantFactory.create(curObj);
                restaurantById.put(restaurantId, restaurant);
            }
        }catch(IOException e){
            throw new IOException(e);
        }
    }

    public void printAllNames(){
        for(Restaurant restaurant : restaurantById.values()){
            System.out.println(restaurant.getName());
        }
    }

}

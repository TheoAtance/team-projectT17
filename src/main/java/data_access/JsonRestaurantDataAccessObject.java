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
    private final Map<String, String> placesIdToCid = new HashMap<>();

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

                // Extract CID from placeUri
                String placeUri = curObj.getJSONObject("googleMapsLinks").getString("placeUri");
                String cid = extractCidFromPlaceUri(placeUri);

                // Extract Google Places ID from the first photo's name
                if (curObj.has("photos") && curObj.getJSONArray("photos").length() > 0) {
                    JSONObject firstPhoto = curObj.getJSONArray("photos").getJSONObject(0);
                    if (firstPhoto.has("name")) {
                        String photoName = firstPhoto.getString("name");
                        // Extract "places/ChIJ..." from "places/ChIJ.../photos/..."
                        String placesId = extractPlacesIdFromPhotoName(photoName);
                        if (placesId != null) {
                            placesIdToCid.put(placesId, cid);
                            System.out.println("DEBUG JsonRestaurantDAO: Mapped " + placesId + " -> " + cid);
                        }
                    }
                }

                Restaurant restaurant = restaurantFactory.create(curObj);
                restaurantById.put(cid, restaurant);
            }

            System.out.println("DEBUG JsonRestaurantDAO: Loaded " + restaurantById.size() + " restaurants");
            System.out.println("DEBUG JsonRestaurantDAO: Created " + placesIdToCid.size() + " Places ID mappings");
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
     * Extracts the Google Places ID from a photo name.
     * Example: "places/ChIJs_Gr4rE0K4gR4PCci36eEkg/photos/AWn5..." -> "places/ChIJs_Gr4rE0K4gR4PCci36eEkg"
     */
    private String extractPlacesIdFromPhotoName(String photoName) {
        if (photoName != null && photoName.startsWith("places/")) {
            int photosIndex = photoName.indexOf("/photos/");
            if (photosIndex != -1) {
                return photoName.substring(0, photosIndex);
            }
        }
        return null;
    }

    /**
     * Converts a Google Places ID to CID if needed.
     * If the ID is already a CID, returns it as-is.
     * @param id Either a CID or a Google Places ID (places/ChIJ...)
     * @return The corresponding CID
     */
    private String normalizeId(String id) {
        // If it's a Google Places ID format, convert to CID
        if (id != null && id.startsWith("places/")) {
            String cid = placesIdToCid.get(id);
            if (cid != null) {
                System.out.println("DEBUG JsonRestaurantDAO: Converted " + id + " -> " + cid);
                return cid;
            }
            System.err.println("WARNING JsonRestaurantDAO: No CID mapping found for " + id);
            return id; // Return as-is if no mapping found
        }
        // Already a CID
        return id;
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
     * Get all restaurants in data JSON.
     * @return list of restaurant objects.
     */
    @Override
    public List<Restaurant> getAllRestaurants() {
        return new ArrayList<>(restaurantById.values());
    }

    /**
     * Get restaurant with given id (supports both CID and Google Places ID)
     * @param id CID or Google Places ID of the restaurant to look up
     * @return restaurant that corresponds to given ID
     */
    @Override
    public Restaurant get(String id) {
        String cid = normalizeId(id);
        return restaurantById.get(cid);
    }

    @Override
    public boolean existById(String id){
        String cid = normalizeId(id);
        return restaurantById.containsKey(cid);
    }

    @Override
    public Restaurant getRandom(){
        List<Restaurant> restaurants = new ArrayList<>(restaurantById.values());
        Random rand = new Random();
        int randomIndex = rand.nextInt(restaurants.size());
        return restaurants.get(randomIndex);
    }
}
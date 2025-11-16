package entity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.*;


/**
 * Factory for creating restaurant objects
 */
public class RestaurantFactory {

    public Restaurant create(JSONObject restaurant){
        String id = restaurant.getString("name");
        String name = restaurant.getJSONObject("displayName").getString("text");
        String address = restaurant.getString("formattedAddress");
        String mapUri = restaurant.getJSONObject("googleMapsLinks").getString("placeUri");
        double latitude = restaurant.getJSONObject("location").getDouble("latitude");
        double longitude = restaurant.getJSONObject("location").getDouble("longitude");
        String type = restaurant.getJSONObject("primaryTypeDisplayName").getString("text");
        double rating = restaurant.getDouble("rating");
        int ratingCount = restaurant.getInt("userRatingCount");
        String phoneNumber = restaurant.optString("nationalPhoneNumber");
        String websiteUri = restaurant.optString("websiteUri");


        JSONArray jsonOpeningHours = restaurant.getJSONObject("regularOpeningHours")
                .getJSONArray("weekdayDescriptions");

        List<String> openingHours = new ArrayList<>();

        for(int i = 0;i < jsonOpeningHours.length();i++){
            openingHours.add(jsonOpeningHours.getString(i));
        }


        JSONArray jsonPhotos = restaurant.getJSONArray("photos");
        List<String> photoIds = new ArrayList<>();

        for(int i = 0;i < jsonPhotos.length();i++){
                photoIds.add(jsonPhotos.getJSONObject(i).getString("name"));
        }

        return new Restaurant.Builder()
                .id(id)
                .name(name)
                .location(address, mapUri, latitude, longitude)
                .type(type)
                .rating(rating, ratingCount)
                .contact(phoneNumber, websiteUri)
                .openingHours(openingHours)
                .studentDiscount(false, 0)
                .photoIds(photoIds)
                .build();
    }

}

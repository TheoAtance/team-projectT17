package interface_adapter.view_restaurant;

import java.util.List;

/**
 * The state for view restaurant view model
 */
public class ViewRestaurantState {
    private String name = "";
    private String address = "";
    private String type = "";
    private double rating = 0;
    private int ratingCount = 0;
    private String phoneNumber = "";
    private List<String> openingHours;
    private List<String> photoIds;
    private String restaurantDndError;


    public ViewRestaurantState(ViewRestaurantState copy) {
        name = copy.name;
        address = copy.address;
        type = copy.type;
        rating = copy.rating;
        ratingCount = copy.ratingCount;
        phoneNumber = copy.phoneNumber;
        openingHours = copy.openingHours;
        photoIds = copy.photoIds;
        restaurantDndError = copy.restaurantDndError;
    }

    public ViewRestaurantState(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<String> getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(List<String> openingHours) {
        this.openingHours = openingHours;
    }

    public List<String> getPhotoIds() {
        return photoIds;
    }

    public void setPhotoIds(List<String> photoIds) {
        this.photoIds = photoIds;
    }

    public String getRestaurantDndError() {
        return restaurantDndError;
    }

    public void setRestaurantDndError(String restaurantDndError) {
        this.restaurantDndError = restaurantDndError;
    }
}

package interface_adapter.favorites;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class FavoritesState {
    private String userId = "";  // NEW
    private String username = "";
    private List<RestaurantDisplayData> restaurants = new ArrayList<>();
    private String errorMessage = "";
    private String successMessage = "";

    public FavoritesState(FavoritesState copy) {
        this.userId = copy.userId;  // NEW
        this.username = copy.username;
        this.restaurants = new ArrayList<>(copy.restaurants);
        this.errorMessage = copy.errorMessage;
        this.successMessage = copy.successMessage;
    }

    public FavoritesState() {
    }

    public String getUserId() {  // NEW
        return userId;
    }

    public void setUserId(String userId) {  // NEW
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<RestaurantDisplayData> getRestaurants() {
        return restaurants;
    }

    public void setRestaurants(List<RestaurantDisplayData> restaurants) {
        this.restaurants = restaurants;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }

    public static class RestaurantDisplayData {
        private final String id;
        private final String name;
        private final String type;
        private final String rating;
        private final boolean hasDiscount;
        private final String discount;
        private final BufferedImage photo;

        public RestaurantDisplayData(String id, String name, String type, String rating,
                                     boolean hasDiscount, String discount, BufferedImage photo) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.rating = rating;
            this.hasDiscount = hasDiscount;
            this.discount = discount;
            this.photo = photo;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public String getType() { return type; }
        public String getRating() { return rating; }
        public boolean hasDiscount() { return hasDiscount; }
        public String getDiscount() { return discount; }
        public BufferedImage getPhoto() { return photo; }
    }
}
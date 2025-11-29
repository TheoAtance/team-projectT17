package interface_adapter.favorites;

import java.util.ArrayList;
import java.util.List;

public class FavoritesState {
    private String username = "";
    private List<RestaurantDisplayData> restaurants = new ArrayList<>();
    private String errorMessage = "";
    private String successMessage = "";

    public FavoritesState(FavoritesState copy) {
        this.username = copy.username;
        this.restaurants = new ArrayList<>(copy.restaurants);
        this.errorMessage = copy.errorMessage;
        this.successMessage = copy.successMessage;
    }

    public FavoritesState() {
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

        public RestaurantDisplayData(String id, String name, String type, String rating,
                                     boolean hasDiscount, String discount) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.rating = rating;
            this.hasDiscount = hasDiscount;
            this.discount = discount;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public String getType() { return type; }
        public String getRating() { return rating; }
        public boolean hasDiscount() { return hasDiscount; }
        public String getDiscount() { return discount; }
    }
}
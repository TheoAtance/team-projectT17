package use_case.favorites.add_favorite;

public class AddFavoriteInputData {
    private final String userId;
    private final String restaurantId;

    public AddFavoriteInputData(String userId, String restaurantId) {
        this.userId = userId;
        this.restaurantId = restaurantId;
    }

    public String getUserId() {
        return userId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }
}
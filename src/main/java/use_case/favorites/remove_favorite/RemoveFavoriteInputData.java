package use_case.favorites.remove_favorite;

/**
 * Input Data for the Remove Favorite use case. Contains the user ID and restaurant ID for the
 * removal operation.
 */
public class RemoveFavoriteInputData {

  private final String userId;
  private final String restaurantId;

  public RemoveFavoriteInputData(String userId, String restaurantId) {
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
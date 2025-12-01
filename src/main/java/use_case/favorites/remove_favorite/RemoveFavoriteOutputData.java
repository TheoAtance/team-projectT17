package use_case.favorites.remove_favorite;

/**
 * Output Data for the Remove Favorite use case. Contains the result of the removal operation.
 */
public class RemoveFavoriteOutputData {

  private final String restaurantName;
  private final boolean success;
  private final String message;

  public RemoveFavoriteOutputData(String restaurantName, boolean success, String message) {
    this.restaurantName = restaurantName;
    this.success = success;
    this.message = message;
  }

  public String getRestaurantName() {
    return restaurantName;
  }

  public boolean isSuccess() {
    return success;
  }

  public String getMessage() {
    return message;
  }
}
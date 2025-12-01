package use_case.view_restaurant;

public class ViewRestaurantInputData {

  private final String restaurantId;

  public ViewRestaurantInputData(String restaurantId) {
    this.restaurantId = restaurantId;
  }

  String getRestaurantId() {
    return restaurantId;
  }
}

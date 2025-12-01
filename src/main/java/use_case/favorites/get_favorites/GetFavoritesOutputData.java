package use_case.favorites.get_favorites;

import java.util.List;

/**
 * Output Data for the Get Favorites use case. Contains the user's favorite restaurants data.
 */
public class GetFavoritesOutputData {

  private final String username;
  private final List<FavoriteRestaurantData> restaurants;

  public GetFavoritesOutputData(String username, List<FavoriteRestaurantData> restaurants) {
    this.username = username;
    this.restaurants = restaurants;
  }

  public String getUsername() {
    return username;
  }

  public List<FavoriteRestaurantData> getRestaurants() {
    return restaurants;
  }

  /**
   * Data class representing a favorite restaurant. This is a simplified version of Restaurant
   * entity for output.
   */
  public static class FavoriteRestaurantData {

    private final String id;
    private final String name;
    private final String type;
    private final double rating;
    private final boolean hasDiscount;
    private final double discountValue;

    public FavoriteRestaurantData(String id, String name, String type, double rating,
        boolean hasDiscount, double discountValue) {
      this.id = id;
      this.name = name;
      this.type = type;
      this.rating = rating;
      this.hasDiscount = hasDiscount;
      this.discountValue = discountValue;
    }

    public String getId() {
      return id;
    }

    public String getName() {
      return name;
    }

    public String getType() {
      return type;
    }

    public double getRating() {
      return rating;
    }

    public boolean hasDiscount() {
      return hasDiscount;
    }

    public double getDiscountValue() {
      return discountValue;
    }
  }
}
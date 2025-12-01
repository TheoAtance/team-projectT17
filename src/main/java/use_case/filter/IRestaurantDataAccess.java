package use_case.filter;

import entity.Restaurant;
import java.util.List;

/**
 * Data access interface for restaurant filtering operations.
 */
public interface IRestaurantDataAccess {

  /**
   * Get all restaurants of a specific type.
   *
   * @param type the restaurant type to filter by
   * @return list of restaurants matching the type
   */
  List<Restaurant> getRestaurantsByType(String type);

  /**
   * Get all unique restaurant types available.
   *
   * @return array of unique restaurant types
   */
  String[] getAllRestaurantTypes();

  /**
   * Get all restaurants in the data source.
   *
   * @return list of all restaurant objects
   */
  List<Restaurant> getAllRestaurants();
}
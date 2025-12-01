package use_case.view_restaurant;

import entity.Restaurant;

public interface ViewRestaurantDataAccessInterface {

  /**
   * Returns whether a restaurant of given id exists in the database.
   *
   * @param id id of restaurant to search for
   * @return true if restaurant exists, false otherwise.
   */
  boolean existById(String id);

  /**
   * Returns the restaurant obj with given restaurant id
   *
   * @param id id of the restaurant to look up
   * @return the restaurant obj with given id
   */
  Restaurant get(String id);


}

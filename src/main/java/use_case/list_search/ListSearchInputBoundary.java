package use_case.list_search;

import entity.Restaurant;
import java.util.List;

/**
 * Input boundary for the ListSearch use case.
 */
public interface ListSearchInputBoundary {
    /**
     * Execute the search for restaurants based on a query string.
     * @param query the search text entered by the user
     */
    void search(String query);

    /**
     * Optional: get all restaurants (for initial display or reset)
     * @return list of all restaurants
     */
    List<Restaurant> getAllRestaurants();
}

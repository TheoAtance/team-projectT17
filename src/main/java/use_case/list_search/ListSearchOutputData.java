package use_case.list_search;

import entity.Restaurant;
import java.util.List;

/**
 * Output data for the ListSearch use case.
 */
public class ListSearchOutputData {
    private final List<Restaurant> filteredRestaurants;

    public ListSearchOutputData(List<Restaurant> filteredRestaurants) {
        this.filteredRestaurants = filteredRestaurants;
    }

    public List<Restaurant> getFilteredRestaurants() {
        return filteredRestaurants;
    }
}

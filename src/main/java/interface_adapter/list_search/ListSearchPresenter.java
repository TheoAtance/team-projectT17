package interface_adapter.list_search;

import entity.Restaurant;
import ui.components.RestaurantListView;
import use_case.list_search.ListSearchOutputBoundary;
import use_case.list_search.ListSearchOutputData;

import java.util.List;

/**
 * Presenter for the ListSearch use case.
 * Updates the RestaurantListView inside LoggedInView.
 */
public class ListSearchPresenter implements ListSearchOutputBoundary {

    private final RestaurantListView restaurantListView;
    private final RestaurantListView.HeartClickListener heartClickListener;

    /**
     * @param restaurantListView the UI restaurant list component from LoggedInView
     * @param heartClickListener the callback for favoriting
     */
    public ListSearchPresenter(RestaurantListView restaurantListView,
                               RestaurantListView.HeartClickListener heartClickListener) {

        this.restaurantListView = restaurantListView;
        this.heartClickListener = heartClickListener;
    }

    /**
     * Called when interactor delivers successful results.
     */
    @Override
    public void presentResults(ListSearchOutputData outputData) {
        List<Restaurant> restaurants = outputData.getFilteredRestaurants();
        restaurantListView.updateRestaurants(restaurants, heartClickListener);
    }

    /**
     * Called on search error.
     */
    @Override
    public void presentError(String error) {
        System.err.println("ListSearch error: " + error);
    }
}
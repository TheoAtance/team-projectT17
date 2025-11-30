package interface_adapter.list_search;

// NEW: Import RestaurantPanel for its inner class
import view.RestaurantPanel;

import java.util.ArrayList;
import java.util.List;

public class ListSearchState {
    // CHANGED: Type to List<RestaurantPanel.RestaurantDisplayData>
    private List<RestaurantPanel.RestaurantDisplayData> filteredRestaurants = new ArrayList<>();
    private String searchQuery = "";
    private String errorMessage = null;

    // Default constructor (important for view models)
    public ListSearchState() {
    }

    // Copy constructor (for immutability pattern if needed, or defensive copying)
    public ListSearchState(ListSearchState copy) {
        this.filteredRestaurants = new ArrayList<>(copy.filteredRestaurants);
        this.searchQuery = copy.searchQuery;
        this.errorMessage = copy.errorMessage;
    }

    // Getters
    // CHANGED: Return type
    public List<RestaurantPanel.RestaurantDisplayData> getFilteredRestaurants() {
        return filteredRestaurants;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    // Setters
    // CHANGED: Parameter type
    public void setFilteredRestaurants(List<RestaurantPanel.RestaurantDisplayData> filteredRestaurants) {
        this.filteredRestaurants = filteredRestaurants;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}

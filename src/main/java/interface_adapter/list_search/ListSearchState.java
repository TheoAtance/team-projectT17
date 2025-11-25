package interface_adapter.list_search;

import entity.Restaurant;
import java.util.ArrayList;
import java.util.List;

public class ListSearchState {
    private List<Restaurant> filteredRestaurants = new ArrayList<>();
    private String errorMessage = null;
    // We can also track the current query if needed for the UI
    private String searchQuery = "";

    // Copy Constructor: Creates a copy of another state
    public ListSearchState(ListSearchState copy) {
        this.filteredRestaurants = copy.filteredRestaurants;
        this.errorMessage = copy.errorMessage;
        this.searchQuery = copy.searchQuery;
    }

    // Default Constructor
    public ListSearchState() {}

    public List<Restaurant> getFilteredRestaurants() {
        return filteredRestaurants;
    }

    public void setFilteredRestaurants(List<Restaurant> filteredRestaurants) {
        this.filteredRestaurants = filteredRestaurants;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }
}

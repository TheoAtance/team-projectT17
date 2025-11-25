package interface_adapter.list_search;

import use_case.list_search.ListSearchOutputBoundary;
import use_case.list_search.ListSearchOutputData;
import view.RestaurantPanel; // NEW: Import RestaurantPanel for its inner class
import entity.Restaurant; // Keep this, as interactor passes entity.Restaurant

import java.util.ArrayList;
import java.util.List;

public class ListSearchPresenter implements ListSearchOutputBoundary {

    private final ListSearchViewModel listSearchViewModel;

    public ListSearchPresenter(ListSearchViewModel listSearchViewModel) {
        this.listSearchViewModel = listSearchViewModel;
    }

    @Override
    public void presentResults(ListSearchOutputData outputData) {
        ListSearchState state = listSearchViewModel.getState();

        // Convert List<entity.Restaurant> from outputData to List<RestaurantPanel.RestaurantDisplayData>
        List<RestaurantPanel.RestaurantDisplayData> displayDataList = new ArrayList<>();
        for (Restaurant restaurant : outputData.getFilteredRestaurants()) { // ADJUSTED: Use getFilteredRestaurants()
            RestaurantPanel.RestaurantDisplayData displayData = new RestaurantPanel.RestaurantDisplayData(
                    restaurant.getId(),
                    restaurant.getName(),
                    restaurant.getType(),
                    restaurant.getRating(),
                    restaurant.hasStudentDiscount(),
                    restaurant.getDiscountValue()
            );
            displayDataList.add(displayData);
        }

        state.setFilteredRestaurants(displayDataList);
        // REMOVED: ListSearchOutputData does not contain searchQuery, so we don't update it from outputData.
        // The searchQuery is typically stored in the ListSearchState as part of the input, not output.
        // state.setSearchQuery(outputData.getSearchQuery()); // THIS LINE IS REMOVED

        listSearchViewModel.setState(state);
        listSearchViewModel.firePropertyChanged();
    }

    @Override
    public void presentError(String errorMessage) {
        ListSearchState state = listSearchViewModel.getState();
        state.setErrorMessage(errorMessage);
        listSearchViewModel.setState(state);
        listSearchViewModel.firePropertyChanged();
    }
}
package interface_adapter.list_search;

import use_case.list_search.ListSearchOutputBoundary;
import use_case.list_search.ListSearchOutputData;

/**
 * Presenter for the ListSearch use case.
 * Updates the ListSearchViewModel.
 */
public class ListSearchPresenter implements ListSearchOutputBoundary {

    private final ListSearchViewModel listSearchViewModel;

    public ListSearchPresenter(ListSearchViewModel listSearchViewModel) {
        this.listSearchViewModel = listSearchViewModel;
    }

    /**
     * Called when interactor delivers successful results.
     */
    @Override
    public void presentResults(ListSearchOutputData outputData) {
        // 1. Get the current state
        ListSearchState state = listSearchViewModel.getState();

        // 2. Update the state with new data
        state.setFilteredRestaurants(outputData.getFilteredRestaurants());
        // Clear any previous error since this was successful
        state.setErrorMessage(null);

        // 3. Update ViewModel and fire change
        listSearchViewModel.setState(state);
        listSearchViewModel.firePropertyChanged();
    }

    /**
     * Called on search error.
     */
    @Override
    public void presentError(String error) {
        ListSearchState state = listSearchViewModel.getState();

        state.setErrorMessage(error);

        listSearchViewModel.setState(state);
        listSearchViewModel.firePropertyChanged();
    }
}
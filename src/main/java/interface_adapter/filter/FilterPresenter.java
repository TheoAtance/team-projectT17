package interface_adapter.filter;

import use_case.filter.FilterOutputBoundary;
import use_case.filter.FilterOutputData;

/**
 * Presenter for the filter use case.
 */
public class FilterPresenter implements FilterOutputBoundary {
    private final FilterViewModel filterViewModel;

    public FilterPresenter(FilterViewModel filterViewModel) {
        this.filterViewModel = filterViewModel;
    }

    @Override
    public void prepareSuccessView(FilterOutputData outputData) {
        FilterState state = filterViewModel.getState();
        state.setRestaurantNames(outputData.getRestaurantNames());
        state.setCurrentFilterType(outputData.getFilterType());
        state.setErrorMessage("");

        filterViewModel.setState(state);
        filterViewModel.firePropertyChange();
    }

    @Override
    public void prepareFailView(String error) {
        FilterState state = filterViewModel.getState();
        state.setErrorMessage(error);

        filterViewModel.setState(state);
        filterViewModel.firePropertyChange();
    }
}

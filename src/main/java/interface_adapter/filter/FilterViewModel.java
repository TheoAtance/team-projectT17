package interface_adapter.filter;

import interface_adapter.ViewModel;

/**
 * View Model for the Filter View.
 */
public class FilterViewModel extends ViewModel<FilterState> {
    public static final String TITLE_LABEL = "Filter View";
    public static final String FILTER_BUTTON_LABEL = "Filter";

    public FilterViewModel() {
        super("filter");
        this.setState(new FilterState());
    }
}
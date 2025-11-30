package interface_adapter.list_search;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Observable view model for ListSearch.
 * Holds the ListSearchState.
 */
public class ListSearchViewModel {
    public static final String STATE_PROPERTY = "state";

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private ListSearchState state = new ListSearchState();

    public void setState(ListSearchState state) {
        this.state = state;
    }

    public ListSearchState getState() {
        return state;
    }

    /**
     * Fires a property change to let the View know the State has changed.
     */
    public void firePropertyChanged() {
        support.firePropertyChange(STATE_PROPERTY, null, this.state);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
}
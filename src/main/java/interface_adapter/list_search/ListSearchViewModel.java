package interface_adapter.list_search;

import entity.Restaurant;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

/**
 * Observable view model for ListSearch.
 */
public class ListSearchViewModel {
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private List<Restaurant> filteredRestaurants;
    private String errorMessage;

    public void setFilteredRestaurants(List<Restaurant> restaurants) {
        this.filteredRestaurants = restaurants;
        support.firePropertyChange("filteredRestaurants", null, restaurants);
    }

    public List<Restaurant> getFilteredRestaurants() {
        return filteredRestaurants;
    }

    public void setErrorMessage(String error) {
        this.errorMessage = error;
        support.firePropertyChange("errorMessage", null, error);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
}

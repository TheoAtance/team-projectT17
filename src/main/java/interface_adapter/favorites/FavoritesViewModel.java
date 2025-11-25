package interface_adapter.favorites;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class FavoritesViewModel {
    public static final String TITLE_LABEL = "Favorites View";

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private FavoritesState state = new FavoritesState();

    public FavoritesViewModel() {
    }

    public void setState(FavoritesState state) {
        FavoritesState oldState = this.state;
        this.state = state;
        support.firePropertyChange("state", oldState, state);
    }

    public FavoritesState getState() {
        return state;
    }

    public void firePropertyChanged() {
        support.firePropertyChange("state", null, this.state);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
}
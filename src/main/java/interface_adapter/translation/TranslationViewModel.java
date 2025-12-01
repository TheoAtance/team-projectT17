package interface_adapter.translation;

import interface_adapter.ViewModel;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class TranslationViewModel extends ViewModel {

  public static final String VIEW_NAME = "translation";

  private final PropertyChangeSupport support = new PropertyChangeSupport(this);
  private TranslationState state = new TranslationState();

  public TranslationViewModel() {
    super(VIEW_NAME);
  }

  public TranslationState getState() {
    return state;
  }

  public void setState(TranslationState state) {
    this.state = state;
  }

  @Override
  public void addPropertyChangeListener(PropertyChangeListener listener) {
    support.addPropertyChangeListener(listener);
  }

  public void firePropertyChanged() {
    support.firePropertyChange("state", null, this.state);
  }
}
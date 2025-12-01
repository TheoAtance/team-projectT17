package view;

import interface_adapter.ViewManagerModel;
import java.awt.CardLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * The View Manager for the program. It listens for property change events in the ViewManagerModel
 * and updates which View should be visible.
 */
public class ViewManager implements PropertyChangeListener {

  private final CardLayout cardLayout;
  private final JPanel views;
  private final ViewManagerModel viewManagerModel;
  private JFrame applicationWindow;

  public ViewManager(JPanel views, CardLayout cardLayout, ViewManagerModel viewManagerModel) {
    this.views = views;
    this.cardLayout = cardLayout;
    this.viewManagerModel = viewManagerModel;
    this.viewManagerModel.addPropertyChangeListener(this);
    this.applicationWindow = new JFrame();
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    if (evt.getPropertyName().equals("state")) {
      final String viewModelName = (String) evt.getNewValue();
      cardLayout.show(views, viewModelName);

      // resize window to fit new view's content
      applicationWindow.pack();

      // center window to screen
      applicationWindow.setLocationRelativeTo(null);
    }
  }

  public void setApp(JFrame applicationWindow) {
    this.applicationWindow = applicationWindow;
  }
}
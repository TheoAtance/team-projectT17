package view;
import interface_adapter.translation.TranslationController;
import interface_adapter.view_restaurant.ViewRestaurantController;
import interface_adapter.view_restaurant.ViewRestaurantState;
import interface_adapter.view_restaurant.ViewRestaurantViewModel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class RestaurantView extends JPanel implements ActionListener, PropertyChangeListener {
    private final String viewName = "restaurant info";
    private ViewRestaurantViewModel viewRestaurantViewModel;
    private final RestaurantTitlePanel titlePanel;
    private TranslationController translationController;

    public RestaurantView(ViewRestaurantViewModel viewRestaurantViewModel) {
        this.viewRestaurantViewModel = viewRestaurantViewModel;
        ViewRestaurantState state = viewRestaurantViewModel.getState();

        viewRestaurantViewModel.addPropertyChangeListener(this);

        titlePanel = new RestaurantTitlePanel(state.getName(), state.getRating(), state.getRatingCount());
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(titlePanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }

    public String getViewName() {
        return viewName;
    }

    public void setTranslationController(TranslationController translationController) {
        this.translationController = translationController;
    }
}
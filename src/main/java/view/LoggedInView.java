package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.display_reviews.DisplayReviewsController;
import interface_adapter.logged_in.LoggedInState;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.logout.LogoutController;
import interface_adapter.random_restauarant.RandomRestaurantController;
import interface_adapter.view_restaurant.ViewRestaurantController;
import interface_adapter.view_restaurant.ViewRestaurantState;
import interface_adapter.view_restaurant.ViewRestaurantViewModel;

import javax.swing.*;
import javax.swing.text.View;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

/**
 * The View displayed after successful login.
 * Shows user information and provides logout functionality.
 */
public class LoggedInView extends JPanel implements PropertyChangeListener {
    public static final String VIEW_NAME = "logged in";
    private final LoggedInViewModel loggedInViewModel;
    private final JLabel welcomeLabel;
    private final JLabel uidLabel;
    private final JButton logoutButton;
    private final JButton filterViewButton;
    private final JButton randomRestaurantButton;

    private LogoutController logoutController;
    private ViewRestaurantController viewRestaurantController;
    private RandomRestaurantController randomRestaurantController;
    private ViewManagerModel viewManagerModel;
    private ViewRestaurantViewModel viewRestaurantViewModel;

    public LoggedInView(LoggedInViewModel loggedInViewModel) {
        this.loggedInViewModel = loggedInViewModel;
        this.loggedInViewModel.addPropertyChangeListener(this);

        final JLabel title = new JLabel(LoggedInViewModel.TITLE_LABEL);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Arial", Font.BOLD, 24));

        welcomeLabel = new JLabel();
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 18));

        uidLabel = new JLabel();
        uidLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        uidLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        uidLabel.setForeground(Color.GRAY);

        logoutButton = new JButton("Logout");
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.addActionListener(evt -> {
            if (logoutController != null) {
                logoutController.execute();
            } else {
                JOptionPane.showMessageDialog(this, "Logout Controller not initialized.");
            }
        });

        filterViewButton = new JButton("Filter Restaurants");
        filterViewButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        filterViewButton.addActionListener(evt -> {
            if (viewManagerModel != null) {
                viewManagerModel.setState("filter");
                viewManagerModel.firePropertyChange();
            } else {
                JOptionPane.showMessageDialog(this, "View Manager not initialized.");
            }
        });

        randomRestaurantButton = new JButton("Random Restaurant");
        randomRestaurantButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        randomRestaurantButton.addActionListener(ect ->{
            if(viewRestaurantController != null){
                try {
                    randomRestaurantController.execute();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if(viewManagerModel != null){
                System.out.println("Changing viewManager state to: " + viewRestaurantViewModel.getViewName());
                viewManagerModel.setState(viewRestaurantViewModel.getViewName());
                viewManagerModel.firePropertyChange();
            }
            else{
                JOptionPane.showMessageDialog(this, "ViewManager not initialized");
            }
        });

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(Box.createVerticalStrut(50));
        this.add(title);
        this.add(Box.createVerticalStrut(20));
        this.add(welcomeLabel);
        this.add(Box.createVerticalStrut(10));
        this.add(uidLabel);
        this.add(Box.createVerticalStrut(30));
        this.add(filterViewButton);
        this.add(Box.createVerticalStrut(10));
        this.add(randomRestaurantButton);
        this.add(Box.createVerticalStrut(10));
        this.add(logoutButton);

        // Initialize with current state
        updateView(loggedInViewModel.getState());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        final LoggedInState state = (LoggedInState) evt.getNewValue();
        updateView(state);
    }

    private void updateView(LoggedInState state) {
        if (state.getNickname() != null && !state.getNickname().isEmpty()) {
            welcomeLabel.setText("Welcome, " + state.getNickname() + "!");
        } else {
            welcomeLabel.setText("Welcome!");
        }

        if (state.getUid() != null && !state.getUid().isEmpty()) {
            uidLabel.setText("User ID: " + state.getUid());
        } else {
            uidLabel.setText("");
        }
    }

    public String getViewName() {
        return VIEW_NAME;
    }

    public void setLogoutController(LogoutController logoutController) {
        this.logoutController = logoutController;
    }

    public void setViewRestaurantController(ViewRestaurantController viewRestaurantController){
        this.viewRestaurantController = viewRestaurantController;
    }

    public void setRandomRestaurantController(RandomRestaurantController randomRestaurantController) {
        this.randomRestaurantController = randomRestaurantController;
    }


    public ViewRestaurantController getViewRestaurantController(){
        return viewRestaurantController;
    }

    public void setViewManagerModel(ViewManagerModel viewManagerModel) {
        this.viewManagerModel = viewManagerModel;
    }

    public void setViewRestaurantViewModel(ViewRestaurantViewModel viewRestaurantViewModel){
        this.viewRestaurantViewModel = viewRestaurantViewModel;
    }




}
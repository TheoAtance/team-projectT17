package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.logged_in.LoggedInState;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.logout.LogoutController;
import interface_adapter.list_search.ListSearchController;
import ui.components.RestaurantListView;
import entity.Restaurant;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

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

    private LogoutController logoutController;
    private ViewManagerModel viewManagerModel;

    private JTextField searchField; // search bar
    private RestaurantListView restaurantListView; // scrollable restaurant list
    private List<Restaurant> allRestaurants; // all restaurants from interactor
    private RestaurantListView.HeartClickListener heartListener; // heart click callback

    private ListSearchController searchController;

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
        this.add(logoutButton);

        JPanel restaurantSection = new JPanel();
        restaurantSection.setLayout(new BoxLayout(restaurantSection, BoxLayout.Y_AXIS));
        restaurantSection.setAlignmentX(Component.CENTER_ALIGNMENT);
        restaurantSection.setMaximumSize(new Dimension(Integer.MAX_VALUE, 500)); // adjust height // adjust height

        // search bar
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0)); // horizontal gap
        searchPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        searchPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        searchPanel.add(searchLabel);

        searchField = new JTextField();
        int maxFieldWidth = 400; // can be proportional to scrollable area
        searchField.setPreferredSize(new Dimension(maxFieldWidth, 24));
        searchField.setMaximumSize(new Dimension(maxFieldWidth, 24));
        searchPanel.add(searchField);

        restaurantSection.add(searchPanel);
        restaurantSection.add(Box.createVerticalStrut(10));

        // placeholder restaurant list view
        restaurantListView = new RestaurantListView(List.of(), null);
        restaurantSection.add(restaurantListView.getScrollPane(), BorderLayout.CENTER);

        this.add(Box.createVerticalStrut(30));
        this.add(restaurantSection);
//
//        // setup search listener
//        searchField.getDocument().addDocumentListener(new DocumentListener() {
//            @Override
//            public void insertUpdate(DocumentEvent e) { filterRestaurants(); }
//            @Override
//            public void removeUpdate(DocumentEvent e) { filterRestaurants(); }
//            @Override
//            public void changedUpdate(DocumentEvent e) { filterRestaurants(); }
//
//            private void filterRestaurants() {
//                if (allRestaurants == null) return;
//
//                String query = searchField.getText().trim().toLowerCase();
//                List<Restaurant> filtered = allRestaurants.stream()
//                        .filter(r -> r.getName().toLowerCase().contains(query))
//                        .toList();
//
//                restaurantListView.updateRestaurants(filtered, heartListener);
//            }
//        });

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

    public RestaurantListView getRestaurantListView() {
        return restaurantListView;
    }

    public RestaurantListView.HeartClickListener getHeartClickListener() {
        return heartListener;
    }

    public void setLogoutController(LogoutController logoutController) {
        this.logoutController = logoutController;
    }

    public void setViewManagerModel(ViewManagerModel viewManagerModel) {
        this.viewManagerModel = viewManagerModel;
    }

    /**
     * Sets all restaurants from interactor. Also initializes heart click listener.
     */
    public void setAllRestaurants(List<Restaurant> restaurants,
                                  RestaurantListView.HeartClickListener heartListener) {
        this.allRestaurants = restaurants;
        this.heartListener = heartListener;
        restaurantListView.updateRestaurants(allRestaurants, heartListener);
    }
    public void setSearchController(ListSearchController searchController) {
        this.searchController = searchController;

        // Update the search listener to use the controller
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { triggerSearch(); }
            @Override
            public void removeUpdate(DocumentEvent e) { triggerSearch(); }
            @Override
            public void changedUpdate(DocumentEvent e) { triggerSearch(); }

            private void triggerSearch() {
                if (searchController == null) return;
                String query = searchField.getText().trim();
                searchController.search(query);
            }
        });
    }
}
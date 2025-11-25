package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.logged_in.LoggedInState;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.logout.LogoutController;
import interface_adapter.list_search.ListSearchController;
import interface_adapter.list_search.ListSearchState;
import interface_adapter.list_search.ListSearchViewModel;
import ui.components.RestaurantListView;
// No direct import of RestaurantPanel needed if HeartClickListener is from RestaurantListView
import entity.Restaurant;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.ArrayList;

/**
 * The View displayed after successful login.
 * Shows user information, provides logout functionality, and displays searchable restaurant list.
 */
public class LoggedInView extends JPanel implements PropertyChangeListener {
    public static final String VIEW_NAME = "logged in";

    // ViewModels for observing changes
    private final LoggedInViewModel loggedInViewModel;
    private final ListSearchViewModel listSearchViewModel;

    // UI Components for LoggedInView specific elements
    private final JLabel welcomeLabel;
    private final JLabel uidLabel;
    private final JButton logoutButton;
    private final JButton filterViewButton; // Declared

    // Controllers for actions
    private LogoutController logoutController;
    private ViewManagerModel viewManagerModel;
    private ListSearchController searchController;

    // UI Components for the search/restaurant list
    private JTextField searchField;
    private RestaurantListView restaurantListView;

    // This listener can be passed down, or a specific method can be called on the controller
    // For now, let's keep it here, but ideally, this would trigger a use case.
    private RestaurantListView.HeartClickListener heartListener;

    // NEW: Field to store the name of the filter view
    private final String filterViewName;


    public LoggedInView(LoggedInViewModel loggedInViewModel,
                        ListSearchViewModel listSearchViewModel,
                        RestaurantListView.HeartClickListener heartListener,
                        String filterViewName) { // RE-ADDED: filterViewName parameter

        this.loggedInViewModel = loggedInViewModel;
        this.listSearchViewModel = listSearchViewModel;
        this.heartListener = heartListener;
        this.filterViewName = filterViewName; // Initialize the new field

        // Add self as listener to both view models
        this.loggedInViewModel.addPropertyChangeListener(this);
        this.listSearchViewModel.addPropertyChangeListener(this);

        // --- LoggedInView specific UI setup ---
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

        // FIXED: Initialize filterViewButton
        filterViewButton = new JButton("Filter Restaurants"); // NEW
        filterViewButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        filterViewButton.addActionListener(evt -> {
            if (viewManagerModel != null) {
                // Use the stored filterViewName
                viewManagerModel.setState(this.filterViewName);
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

        // --- Restaurant Search Section UI setup ---
        JPanel restaurantSection = new JPanel();
        restaurantSection.setLayout(new BoxLayout(restaurantSection, BoxLayout.Y_AXIS));
        restaurantSection.setAlignmentX(Component.CENTER_ALIGNMENT);
        restaurantSection.setMaximumSize(new Dimension(Integer.MAX_VALUE, 500));

        // Search bar
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        searchPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        searchPanel.add(searchLabel);

        searchField = new JTextField();
        int maxFieldWidth = 400;
        searchField.setPreferredSize(new Dimension(maxFieldWidth, 24));
        searchField.setMaximumSize(new Dimension(maxFieldWidth, 24));
        searchPanel.add(searchField);

        // Add DocumentListener here, directly linking to the searchController
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { triggerSearch(); }
            @Override
            public void removeUpdate(DocumentEvent e) { triggerSearch(); }
            @Override
            public void changedUpdate(DocumentEvent e) { triggerSearch(); }

            private void triggerSearch() {
                if (LoggedInView.this.searchController == null) return;
                String query = searchField.getText().trim();
                LoggedInView.this.searchController.search(query);
            }
        });

        restaurantSection.add(searchPanel);
        restaurantSection.add(Box.createVerticalStrut(10));

        // Initialize RestaurantListView with an empty list
        // It will be updated via propertyChange from ListSearchViewModel
        restaurantListView = new RestaurantListView(new ArrayList<>(), this.heartListener);
        JScrollPane restaurantScrollPane = restaurantListView.getScrollPane();
        restaurantScrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        restaurantSection.add(restaurantScrollPane, BorderLayout.CENTER);


        this.add(Box.createVerticalStrut(30));
        this.add(restaurantSection);

        // Initialize views with current states
        updateLoggedInView(loggedInViewModel.getState());
        updateListSearchView(listSearchViewModel.getState());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() == loggedInViewModel) {
            LoggedInState state = (LoggedInState) evt.getNewValue();
            updateLoggedInView(state);
        } else if (evt.getSource() == listSearchViewModel) {
            ListSearchState state = (ListSearchState) evt.getNewValue();
            updateListSearchView(state);
        }
    }

    private void updateLoggedInView(LoggedInState state) {
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

    private void updateListSearchView(ListSearchState state) {
        if (state.getErrorMessage() != null && !state.getErrorMessage().isEmpty()) {
            JOptionPane.showMessageDialog(this, state.getErrorMessage());
            state.setErrorMessage(null);
        }

        restaurantListView.updateRestaurants(state.getFilteredRestaurants(), this.heartListener);
    }

    public String getViewName() {
        return VIEW_NAME;
    }

    public RestaurantListView getRestaurantListView() {
        return restaurantListView;
    }

    public void setLogoutController(LogoutController logoutController) {
        this.logoutController = logoutController;
    }

    public void setViewManagerModel(ViewManagerModel viewManagerModel) {
        this.viewManagerModel = viewManagerModel;
    }

    public void setSearchController(ListSearchController searchController) {
        this.searchController = searchController;
    }
}
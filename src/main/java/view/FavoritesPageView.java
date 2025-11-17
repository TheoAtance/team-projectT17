package view;

import interface_adapter.favorites.FavoritesState;
import interface_adapter.favorites.FavoritesViewModel;
import interface_adapter.favorites.GetFavoritesController;
import interface_adapter.favorites.RemoveFavoriteController;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * The View for the Favorites Use Case.
 * Uses RestaurantPanel components to display favorite restaurants.
 */
public class FavoritesPageView extends JFrame implements PropertyChangeListener {

    public static final String VIEW_NAME = "favorites";

    private final FavoritesViewModel favoritesViewModel;
    private final String userId;

    private JPanel restaurantsContainer;
    private JLabel titleLabel;
    private JLabel userLabel;

    private GetFavoritesController getFavoritesController;
    private RemoveFavoriteController removeFavoriteController;

    public FavoritesPageView(FavoritesViewModel favoritesViewModel, String userId) {
        this.favoritesViewModel = favoritesViewModel;
        this.userId = userId;

        favoritesViewModel.addPropertyChangeListener(this);

        initializeUI();
    }

    private void initializeUI() {
        setTitle("Favorite Restaurants");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        final JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        titleLabel = new JLabel("My Favorites");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        userLabel = new JLabel("User: ");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        userLabel.setForeground(Color.GRAY);
        headerPanel.add(userLabel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        restaurantsContainer = new JPanel();
        restaurantsContainer.setLayout(new GridLayout(0, 3, 20, 20));
        restaurantsContainer.setBackground(new Color(249, 250, 251));
        restaurantsContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        final JScrollPane scrollPane = new JScrollPane(restaurantsContainer);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Loads favorites by calling the controller.
     */
    public void loadFavorites() {
        if (getFavoritesController != null) {
            getFavoritesController.execute(userId);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Get Favorites Controller not initialized.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("state".equals(evt.getPropertyName())) {
            final FavoritesState state = favoritesViewModel.getState();

            updateUsername(state);
            updateRestaurantsDisplay(state);
            showErrorIfPresent(state);
            showSuccessIfPresent(state);
        }
    }

    private void updateUsername(FavoritesState state) {
        final String username = state.getUsername();
        if (username != null && !username.isEmpty()) {
            userLabel.setText("User: " + username);
        }
    }

    private void updateRestaurantsDisplay(FavoritesState state) {
        restaurantsContainer.removeAll();

        if (state.getRestaurants().isEmpty()) {
            showEmptyState();
        } else {
            setTitle("Favorite Restaurants (" + state.getRestaurants().size() + ") - " + state.getUsername());
            restaurantsContainer.setLayout(new GridLayout(0, 3, 20, 20));
            restaurantsContainer.setBackground(new Color(249, 250, 251));

            for (FavoritesState.RestaurantDisplayData restaurantData : state.getRestaurants()) {
                final RestaurantPanel panel = createRestaurantPanel(restaurantData);
                restaurantsContainer.add(panel);
            }
        }

        restaurantsContainer.revalidate();
        restaurantsContainer.repaint();
    }

    private RestaurantPanel createRestaurantPanel(FavoritesState.RestaurantDisplayData restaurantData) {
        // Convert FavoritesState data to RestaurantPanel data
        RestaurantPanel.RestaurantDisplayData panelData = convertToRestaurantPanelData(restaurantData);

        // Create the RestaurantPanel
        RestaurantPanel restaurantPanel = new RestaurantPanel(panelData);

        // Set as favorite since we're in favorites page
        restaurantPanel.setFavorite(true);

        // Set up heart click listener to handle removal
        restaurantPanel.setHeartClickListener((restaurantId, newFavoriteState) -> {
            if (!newFavoriteState) {
                handleRemoveFavorite(restaurantData);
            } else {
                // If somehow they favorite it again, just keep it as favorite
                restaurantPanel.setFavorite(true);
            }
        });

        return restaurantPanel;
    }

    private RestaurantPanel.RestaurantDisplayData convertToRestaurantPanelData(FavoritesState.RestaurantDisplayData data) {
        // Parse rating from String to double
        double rating;
        try {
            rating = Double.parseDouble(data.getRating());
        } catch (NumberFormatException e) {
            rating = 0.0;
        }

        // Parse discount value from percentage string to double
        double discountValue = 0.0;
        if (data.hasDiscount() && data.getDiscount() != null && !data.getDiscount().isEmpty()) {
            try {
                // Extract number from discount string (e.g., "15% off" -> 0.15)
                String discountStr = data.getDiscount().replaceAll("[^0-9.]", "");
                if (!discountStr.isEmpty()) {
                    discountValue = Double.parseDouble(discountStr) / 100.0;
                }
            } catch (NumberFormatException e) {
                discountValue = 0.0;
            }
        }

        return new RestaurantPanel.RestaurantDisplayData(
                data.getId(),
                data.getName(),
                data.getType(),
                rating,
                data.hasDiscount(),
                discountValue
        );
    }

    private void handleRemoveFavorite(FavoritesState.RestaurantDisplayData restaurantData) {
        final int result = JOptionPane.showConfirmDialog(
                this,
                "Remove " + restaurantData.getName() + " from favorites?",
                "Remove Favorite",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            if (removeFavoriteController != null) {
                // Call the controller to remove from backend
                removeFavoriteController.execute(userId, restaurantData.getId());
                // IMMEDIATELY reload favorites to refresh the display - just like your old code
                loadFavorites();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Remove Favorite Controller not initialized.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showEmptyState() {
        setTitle("Favorite Restaurants (0) - " + favoritesViewModel.getState().getUsername());
        restaurantsContainer.setLayout(new BorderLayout());
        restaurantsContainer.setBackground(new Color(249, 250, 251));

        final JPanel emptyPanel = new JPanel();
        emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));
        emptyPanel.setBackground(new Color(249, 250, 251));
        emptyPanel.setBorder(BorderFactory.createEmptyBorder(100, 50, 100, 50));

        final JLabel emptyIcon = new JLabel("♡");
        emptyIcon.setFont(new Font("Arial", Font.PLAIN, 72));
        emptyIcon.setForeground(new Color(209, 213, 219));
        emptyIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        final JLabel emptyText = new JLabel("No favorite restaurants yet");
        emptyText.setFont(new Font("Arial", Font.BOLD, 20));
        emptyText.setForeground(new Color(107, 114, 128));
        emptyText.setAlignmentX(Component.CENTER_ALIGNMENT);
        emptyText.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        final JLabel emptySubtext = new JLabel("Start adding restaurants to your favorites!");
        emptySubtext.setFont(new Font("Arial", Font.PLAIN, 14));
        emptySubtext.setForeground(new Color(156, 163, 175));
        emptySubtext.setAlignmentX(Component.CENTER_ALIGNMENT);

        emptyPanel.add(emptyIcon);
        emptyPanel.add(emptyText);
        emptyPanel.add(emptySubtext);

        restaurantsContainer.add(emptyPanel, BorderLayout.CENTER);
    }

    private void showErrorIfPresent(FavoritesState state) {
        final String error = state.getErrorMessage();
        if (error != null && !error.isEmpty()) {
            JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
            state.setErrorMessage("");
        }
    }

    private void showSuccessIfPresent(FavoritesState state) {
        final String success = state.getSuccessMessage();
        if (success != null && !success.isEmpty()) {
            JOptionPane.showMessageDialog(this, success, "Success", JOptionPane.INFORMATION_MESSAGE);
            state.setSuccessMessage("");
        }
    }

    public String getViewName() {
        return VIEW_NAME;
    }

    public void setGetFavoritesController(GetFavoritesController controller) {
        this.getFavoritesController = controller;
    }

    public void setRemoveFavoriteController(RemoveFavoriteController controller) {
        this.removeFavoriteController = controller;
    }
}
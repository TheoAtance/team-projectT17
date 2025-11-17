package ui;

import entity.Restaurant;
import entity.User;
import ui.components.RestaurantPanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FavoritesPage extends JFrame {
    private JPanel restaurantsContainer; // REMOVED 'final' keyword
    private User currentUser;
    private List<Restaurant> allRestaurants;

    // Callback interface to notify when user data changes
    public interface UserUpdateListener {
        void onUserUpdated(User updatedUser);
    }
    private UserUpdateListener userUpdateListener;

    public FavoritesPage(User currentUser, List<Restaurant> allRestaurants) {
        this.currentUser = currentUser;
        this.allRestaurants = allRestaurants;

        initializeUI();
        loadFavorites();
    }

    public FavoritesPage(User currentUser, List<Restaurant> allRestaurants, UserUpdateListener listener) {
        this.currentUser = currentUser;
        this.allRestaurants = allRestaurants;
        this.userUpdateListener = listener;

        initializeUI();
        loadFavorites();
    }

    private void initializeUI() {
        setTitle("Favorite Restaurants - " + currentUser.getNickname());
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null); // Center the window

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("My Favorites");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Add user info
        JLabel userLabel = new JLabel("User: " + currentUser.getNickname());
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        userLabel.setForeground(Color.GRAY);
        headerPanel.add(userLabel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Main content area with scroll - INITIALIZE restaurantsContainer here
        restaurantsContainer = new JPanel(); // This is where it gets initialized
        restaurantsContainer.setLayout(new GridLayout(0, 3, 20, 20));
        restaurantsContainer.setBackground(new Color(249, 250, 251));
        restaurantsContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JScrollPane scrollPane = new JScrollPane(restaurantsContainer);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Load favorite restaurants from user's favoriteRestaurantIds
     */
    private void loadFavorites() {
        restaurantsContainer.removeAll();
        restaurantsContainer.setLayout(new GridLayout(0, 3, 20, 20));
        restaurantsContainer.setBackground(new Color(249, 250, 251));

        List<Restaurant> favoriteRestaurants = getFavoriteRestaurantsFromUser();

        if (favoriteRestaurants.isEmpty()) {
            showEmptyState();
        } else {
            // Update window title with count
            setTitle("Favorite Restaurants (" + favoriteRestaurants.size() + ") - " + currentUser.getNickname());

            for (Restaurant restaurant : favoriteRestaurants) {
                RestaurantPanel panel = new RestaurantPanel(restaurant);
                panel.setFavorite(true);

                // Add heart click listener to remove from user's favorites
                panel.setHeartClickListener((clickedRestaurant, newFavoriteState) -> {
                    if (!newFavoriteState) {
                        int result = JOptionPane.showConfirmDialog(
                                FavoritesPage.this,
                                "Remove " + clickedRestaurant.getName() + " from favorites?",
                                "Remove Favorite",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE
                        );
                        if (result == JOptionPane.YES_OPTION) {
                            removeFavoriteFromUser(clickedRestaurant);
                        } else {
                            // Revert the heart state if user cancels
                            panel.setFavorite(true);
                        }
                    }
                });

                restaurantsContainer.add(panel);
            }
        }

        restaurantsContainer.revalidate();
        restaurantsContainer.repaint();
    }

    /**
     * Convert user's favorite restaurant IDs to actual Restaurant objects
     */
    private List<Restaurant> getFavoriteRestaurantsFromUser() {
        List<Restaurant> favorites = new ArrayList<>();
        for (String restaurantId : currentUser.getFavoriteRestaurantIds()) {
            // Find the restaurant in allRestaurants by ID
            for (Restaurant restaurant : allRestaurants) {
                if (restaurant.getId().equals(restaurantId)) {
                    favorites.add(restaurant);
                    break;
                }
            }
        }
        return favorites;
    }

    /**
     * Remove restaurant from user's favorites and notify listener
     */
    private void removeFavoriteFromUser(Restaurant restaurant) {
        // Remove from user's favorite list
        currentUser.removeFavoriteRestaurantId(restaurant.getId());

        // Notify listener about user update
        if (userUpdateListener != null) {
            userUpdateListener.onUserUpdated(currentUser);
        }

        // Reload the display
        loadFavorites();

        // Show confirmation
        JOptionPane.showMessageDialog(this,
                restaurant.getName() + " removed from favorites!",
                "Favorite Updated",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Show empty state when no favorites exist
     */
    private void showEmptyState() {
        setTitle("Favorite Restaurants (0) - " + currentUser.getNickname());

        restaurantsContainer.setLayout(new BorderLayout());
        restaurantsContainer.setBackground(new Color(249, 250, 251));

        JPanel emptyPanel = new JPanel();
        emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));
        emptyPanel.setBackground(new Color(249, 250, 251));
        emptyPanel.setBorder(BorderFactory.createEmptyBorder(100, 50, 100, 50));

        JLabel emptyIcon = new JLabel("♡");
        emptyIcon.setFont(new Font("Arial", Font.PLAIN, 72));
        emptyIcon.setForeground(new Color(209, 213, 219));
        emptyIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel emptyText = new JLabel("No favorite restaurants yet");
        emptyText.setFont(new Font("Arial", Font.BOLD, 20));
        emptyText.setForeground(new Color(107, 114, 128));
        emptyText.setAlignmentX(Component.CENTER_ALIGNMENT);
        emptyText.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        JLabel emptySubtext = new JLabel("Start adding restaurants to your favorites!");
        emptySubtext.setFont(new Font("Arial", Font.PLAIN, 14));
        emptySubtext.setForeground(new Color(156, 163, 175));
        emptySubtext.setAlignmentX(Component.CENTER_ALIGNMENT);

        emptyPanel.add(emptyIcon);
        emptyPanel.add(emptyText);
        emptyPanel.add(emptySubtext);

        restaurantsContainer.add(emptyPanel, BorderLayout.CENTER);
    }

    /**
     * Update the page with a new user object
     */
    public void updateUser(User updatedUser) {
        this.currentUser = updatedUser;
        setTitle("Favorite Restaurants - " + currentUser.getNickname());
        loadFavorites();
    }

    /**
     * Set the user update listener
     */
    public void setUserUpdateListener(UserUpdateListener listener) {
        this.userUpdateListener = listener;
    }

    /**
     * Get the current user (in case you need to access updated user data)
     */
    public User getCurrentUser() {
        return currentUser;
    }

    // Demo main method to test the functionality
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create a sample user with some favorites
            User demoUser = new User("user123", "john@example.com", "John Doe");
            demoUser.addFavoriteRestaurantId("1");
            demoUser.addFavoriteRestaurantId("3");
            demoUser.addFavoriteRestaurantId("5");

            // Create sample restaurants
            List<Restaurant> allRestaurants = new ArrayList<>();
            allRestaurants.add(new Restaurant("1", "Sweet Tooth Café", "123 Main St",
                    "Desserts", 3.6, true, 0.15));
            allRestaurants.add(new Restaurant("2", "Pizza Paradise", "456 Oak Ave",
                    "Italian", 4.2, true, 0.10));
            allRestaurants.add(new Restaurant("3", "Sushi Master", "789 Pine Rd",
                    "Japanese", 4.8, false, 0));
            allRestaurants.add(new Restaurant("4", "Burger Joint", "321 Elm St",
                    "American", 4.5, true, 0.20));
            allRestaurants.add(new Restaurant("5", "Taco Fiesta", "654 Maple Dr",
                    "Mexican", 4.3, true, 0.12));

            // Create the favorites page with user and restaurants
            FavoritesPage favoritesPage = new FavoritesPage(demoUser, allRestaurants,
                    new FavoritesPage.UserUpdateListener() {
                        @Override
                        public void onUserUpdated(User updatedUser) {
                            System.out.println("User favorites updated!");
                            System.out.println("Current favorites: " + updatedUser.getFavoriteRestaurantIds());
                            // Here you would typically save to your database
                        }
                    });

            favoritesPage.setVisible(true);
        });
    }
}
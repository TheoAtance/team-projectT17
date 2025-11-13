package ui;

import entity.Restaurant;
import ui.components.RestaurantPanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FavoritesPage extends JFrame {
    private final JPanel restaurantsContainer;
    private List<Restaurant> favoriteRestaurants;

    public FavoritesPage() {
        setTitle("Favorite Restaurants");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        favoriteRestaurants = new ArrayList<>();

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("My Favorites");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        add(headerPanel, BorderLayout.NORTH);

        // Main content area with scroll
        restaurantsContainer = new JPanel();
        restaurantsContainer.setLayout(new GridLayout(0, 3, 20, 20)); // 3 columns
        restaurantsContainer.setBackground(new Color(249, 250, 251));
        restaurantsContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JScrollPane scrollPane = new JScrollPane(restaurantsContainer);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);

        // Load favorites (this would come from your data layer)
        loadFavorites();
    }

    /**
     * Load favorite restaurants and display them.
     * In a real app, this would fetch from your data access layer.
     */
    private void loadFavorites() {
        // Clear existing panels
        restaurantsContainer.removeAll();

        // CRITICAL FIX: Always reset to GridLayout when showing restaurants
        restaurantsContainer.setLayout(new GridLayout(0, 3, 20, 20));
        restaurantsContainer.setBackground(new Color(249, 250, 251));

        if (favoriteRestaurants.isEmpty()) {
            showEmptyState();
        } else {
            System.out.println("Loading " + favoriteRestaurants.size() + " favorites"); // Debug line

            for (Restaurant restaurant : favoriteRestaurants) {
                RestaurantPanel panel = new RestaurantPanel(restaurant);
                panel.setFavorite(true);

                // Add click handler to remove from favorites
                panel.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        // Check if heart icon was clicked (top-right area)
                        if (e.getX() > panel.getWidth() - 60 && e.getY() < 60) {
                            int result = JOptionPane.showConfirmDialog(
                                    FavoritesPage.this,
                                    "Remove " + restaurant.getName() + " from favorites?",
                                    "Remove Favorite",
                                    JOptionPane.YES_NO_OPTION
                            );
                            if (result == JOptionPane.YES_OPTION) {
                                removeFavorite(restaurant);
                            }
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
     * Show empty state when no favorites exist.
     */
    private void showEmptyState() {
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
     * Add a restaurant to favorites.
     */
    public void addFavorite(Restaurant restaurant) {
        favoriteRestaurants.add(restaurant);
        loadFavorites();
    }

    /**
     * Remove a restaurant from favorites.
     */
    public void removeFavorite(Restaurant restaurant) {
        favoriteRestaurants.remove(restaurant);
        loadFavorites();
    }

    /**
     * Set the list of favorite restaurants (used when loading from database).
     */
    public void setFavorites(List<Restaurant> restaurants) {
        this.favoriteRestaurants = new ArrayList<>(restaurants);
        loadFavorites();
    }

    /**
     * Get the current list of favorite restaurants.
     */
    public List<Restaurant> getFavorites() {
        return new ArrayList<>(favoriteRestaurants);
    }

    // Temporary launcher for development
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FavoritesPage page = new FavoritesPage();

            // Add some sample favorites for testing
            Restaurant r1 = new Restaurant("1", "Sweet Tooth Café", "123 Main St",
                    "Desserts", 3.6, true, 0.15);
            Restaurant r2 = new Restaurant("2", "Pizza Paradise", "456 Oak Ave",
                    "Italian", 4.2, true, 0.10);
            Restaurant r3 = new Restaurant("3", "Sushi Master", "789 Pine Rd",
                    "Japanese", 4.8, false, 0);
            Restaurant r4 = new Restaurant("4", "Burger Joint", "321 Elm St",
                    "American", 4.5, true, 0.20);
            Restaurant r5 = new Restaurant("5", "Taco Fiesta", "654 Maple Dr",
                    "Mexican", 4.3, true, 0.12);

            // Use setFavorites instead of multiple addFavorite calls
            List<Restaurant> favorites = new ArrayList<>();
            favorites.add(r1);
            favorites.add(r2);
            favorites.add(r3);
            favorites.add(r4);
            favorites.add(r5);

            page.setFavorites(favorites);

            page.setVisible(true);
        });
    }
}
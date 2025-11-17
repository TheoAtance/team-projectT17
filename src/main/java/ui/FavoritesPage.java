package ui;

import entity.Restaurant;
import entity.User;
import ui.components.RestaurantPanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FavoritesPage extends JFrame {
    private JPanel restaurantsContainer;
    private User currentUser;
    private List<Restaurant> allRestaurants;

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
        setLocationRelativeTo(null);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("My Favorites");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JLabel userLabel = new JLabel("User: " + currentUser.getNickname());
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        userLabel.setForeground(Color.GRAY);
        headerPanel.add(userLabel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        restaurantsContainer = new JPanel();
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

    private void loadFavorites() {
        restaurantsContainer.removeAll();
        restaurantsContainer.setLayout(new GridLayout(0, 3, 20, 20));
        restaurantsContainer.setBackground(new Color(249, 250, 251));

        List<Restaurant> favoriteRestaurants = getFavoriteRestaurantsFromUser();

        if (favoriteRestaurants.isEmpty()) {
            showEmptyState();
        } else {
            setTitle("Favorite Restaurants (" + favoriteRestaurants.size() + ") - " + currentUser.getNickname());

            for (Restaurant restaurant : favoriteRestaurants) {
                RestaurantPanel panel = new RestaurantPanel(restaurant);
                panel.setFavorite(true);

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

    private List<Restaurant> getFavoriteRestaurantsFromUser() {
        List<Restaurant> favorites = new ArrayList<>();
        for (String restaurantId : currentUser.getFavoriteRestaurantIds()) {
            for (Restaurant restaurant : allRestaurants) {
                if (restaurant.getId().equals(restaurantId)) {
                    favorites.add(restaurant);
                    break;
                }
            }
        }
        return favorites;
    }

    private void removeFavoriteFromUser(Restaurant restaurant) {
        currentUser.removeFavoriteRestaurantId(restaurant.getId());

        if (userUpdateListener != null) {
            userUpdateListener.onUserUpdated(currentUser);
        }

        loadFavorites();

        JOptionPane.showMessageDialog(this,
                restaurant.getName() + " removed from favorites!",
                "Favorite Updated",
                JOptionPane.INFORMATION_MESSAGE);
    }

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

    public void updateUser(User updatedUser) {
        this.currentUser = updatedUser;
        setTitle("Favorite Restaurants - " + currentUser.getNickname());
        loadFavorites();
    }

    public void setUserUpdateListener(UserUpdateListener listener) {
        this.userUpdateListener = listener;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            User demoUser = new User("user123", "john@example.com", "John Doe");
            demoUser.addFavoriteRestaurantId("1");
            demoUser.addFavoriteRestaurantId("3");
            demoUser.addFavoriteRestaurantId("5");

            List<Restaurant> allRestaurants = new ArrayList<>();

            allRestaurants.add(new Restaurant.Builder()
                    .id("1")
                    .name("Sweet Tooth Café")
                    .location("123 Main St", "https://maps.example.com/1", 43.6532, -79.3832)
                    .type("Desserts")
                    .rating(3.6, 150)
                    .contact("+1-416-555-0101", "https://sweettooth.example.com")
                    .studentDiscount(true, 0.15)
                    .openingHours(List.of("Mon-Fri: 8am-8pm", "Sat-Sun: 9am-10pm"))
                    .photoIds(List.of("photo1a", "photo1b"))
                    .build());

            allRestaurants.add(new Restaurant.Builder()
                    .id("2")
                    .name("Pizza Paradise")
                    .location("456 Oak Ave", "https://maps.example.com/2", 43.6612, -79.3952)
                    .type("Italian")
                    .rating(4.2, 320)
                    .contact("+1-416-555-0102", "https://pizzaparadise.example.com")
                    .studentDiscount(true, 0.10)
                    .openingHours(List.of("Mon-Sun: 11am-11pm"))
                    .photoIds(List.of("photo2a"))
                    .build());

            allRestaurants.add(new Restaurant.Builder()
                    .id("3")
                    .name("Sushi Master")
                    .location("789 Pine Rd", "https://maps.example.com/3", 43.6482, -79.4012)
                    .type("Japanese")
                    .rating(4.8, 520)
                    .contact("+1-416-555-0103", "https://sushimaster.example.com")
                    .studentDiscount(false, 0)
                    .openingHours(List.of("Tue-Sun: 12pm-10pm", "Closed Monday"))
                    .photoIds(List.of("photo3a", "photo3b", "photo3c"))
                    .build());

            allRestaurants.add(new Restaurant.Builder()
                    .id("4")
                    .name("Burger Joint")
                    .location("321 Elm St", "https://maps.example.com/4", 43.6702, -79.3872)
                    .type("American")
                    .rating(4.5, 410)
                    .contact("+1-416-555-0104", "https://burgerjoint.example.com")
                    .studentDiscount(true, 0.20)
                    .openingHours(List.of("Mon-Sun: 10am-12am"))
                    .photoIds(List.of("photo4a", "photo4b"))
                    .build());

            allRestaurants.add(new Restaurant.Builder()
                    .id("5")
                    .name("Taco Fiesta")
                    .location("654 Maple Dr", "https://maps.example.com/5", 43.6552, -79.4102)
                    .type("Mexican")
                    .rating(4.3, 280)
                    .contact("+1-416-555-0105", "https://tacofiesta.example.com")
                    .studentDiscount(true, 0.12)
                    .openingHours(List.of("Mon-Thu: 11am-9pm", "Fri-Sun: 11am-11pm"))
                    .photoIds(List.of("photo5a"))
                    .build());

            FavoritesPage favoritesPage = new FavoritesPage(demoUser, allRestaurants,
                    new FavoritesPage.UserUpdateListener() {
                        @Override
                        public void onUserUpdated(User updatedUser) {
                            System.out.println("User favorites updated!");
                            System.out.println("Current favorites: " + updatedUser.getFavoriteRestaurantIds());
                        }
                    });

            favoritesPage.setVisible(true);
        });
    }
}
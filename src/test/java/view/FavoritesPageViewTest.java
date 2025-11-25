package test.view;

import interface_adapter.favorites.FavoritesState;
import interface_adapter.favorites.FavoritesViewModel;
import interface_adapter.favorites.GetFavoritesController;
import interface_adapter.favorites.RemoveFavoriteController;
import use_case.favorites.get_favorites.GetFavoritesInputBoundary;
import use_case.favorites.get_favorites.GetFavoritesInputData;
import use_case.favorites.remove_favorite.RemoveFavoriteInputBoundary;
import use_case.favorites.remove_favorite.RemoveFavoriteInputData;
import view.FavoritesPageView;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Test to display FavoritesPageView with RestaurantPanel components
 */
class FavoritesPageViewTest {

    // This simulates the user's actual favorites list
    private static List<FavoritesState.RestaurantDisplayData> userFavorites = new ArrayList<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Create the view model
                FavoritesViewModel viewModel = new FavoritesViewModel();
                FavoritesState state = viewModel.getState();

                // Set username
                state.setUsername("Test User");

                // Initialize user's favorites
                userFavorites.clear();
                userFavorites.add(new FavoritesState.RestaurantDisplayData("1", "Pizza Palace", "Italian", "4.5", true, "15% off"));
                userFavorites.add(new FavoritesState.RestaurantDisplayData("2", "Burger Barn", "American", "4.2", false, ""));
                userFavorites.add(new FavoritesState.RestaurantDisplayData("3", "Sushi Zen", "Japanese", "4.8", true, "20% off"));
                userFavorites.add(new FavoritesState.RestaurantDisplayData("4", "Taco Fiesta", "Mexican", "4.3", true, "10% off"));
                userFavorites.add(new FavoritesState.RestaurantDisplayData("5", "Green Leaf Cafe", "Vegetarian", "4.6", false, ""));
                userFavorites.add(new FavoritesState.RestaurantDisplayData("6", "Dragon Garden", "Chinese", "4.4", true, "25% off"));

                // Create the favorites page
                FavoritesPageView favoritesView = new FavoritesPageView(viewModel, "testuser123");

                // Create mock GetFavoritesInputBoundary that returns the user's current favorites
                GetFavoritesInputBoundary mockGetInteractor = new GetFavoritesInputBoundary() {
                    @Override
                    public void execute(GetFavoritesInputData inputData) {
                        System.out.println("Getting favorites for user: " + inputData.getUserId());
                        // Return the current user favorites
                        FavoritesState currentState = viewModel.getState();
                        currentState.setRestaurants(new ArrayList<>(userFavorites));
                        viewModel.firePropertyChanged(); // Use firePropertyChanged to ensure update
                    }
                };

                // Create mock RemoveFavoriteInputBoundary that actually removes from user's favorites
                RemoveFavoriteInputBoundary mockRemoveInteractor = new RemoveFavoriteInputBoundary() {
                    @Override
                    public void execute(RemoveFavoriteInputData inputData) {
                        System.out.println("Removing restaurant " + inputData.getRestaurantId() +
                                " from user's favorites: " + inputData.getUserId());

                        // Remove from the user's favorites list
                        userFavorites.removeIf(restaurant -> restaurant.getId().equals(inputData.getRestaurantId()));

                        // Update state to reflect the change
                        FavoritesState currentState = viewModel.getState();
                        currentState.setRestaurants(new ArrayList<>(userFavorites));
                        currentState.setSuccessMessage("Restaurant removed from favorites successfully!");
                        viewModel.firePropertyChanged(); // Use firePropertyChanged to ensure update
                    }
                };

                // Create controllers
                GetFavoritesController getController = new GetFavoritesController(mockGetInteractor);
                RemoveFavoriteController removeController = new RemoveFavoriteController(mockRemoveInteractor);

                // Set controllers
                favoritesView.setGetFavoritesController(getController);
                favoritesView.setRemoveFavoriteController(removeController);

                // Make the window visible
                favoritesView.setVisible(true);

                // Set initial restaurants in state and trigger display
                state.setRestaurants(new ArrayList<>(userFavorites));
                viewModel.firePropertyChanged(); // This triggers the property change to display restaurants

                System.out.println("Displaying " + userFavorites.size() + " restaurant panels");
                System.out.println("Click the heart icons to remove from favorites");

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Error: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
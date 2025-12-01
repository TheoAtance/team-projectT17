package view;

import data_access.UserDataAccessInterface;
import entity.User;
import interface_adapter.favorites.AddFavoriteController;
import interface_adapter.favorites.RemoveFavoriteController;
import interface_adapter.logged_in.LoggedInViewModel;

/**
 * Helper class to set up RestaurantPanel with favorites functionality.
 * Use this in LoggedInView to enable add/remove favorites on panels.
 */
public class RestaurantPanelHelper {

    /**
     * Sets up a RestaurantPanel with proper favorites handling.
     * Call this after creating each RestaurantPanel.
     *
     * @param panel The RestaurantPanel to configure
     * @param addFavoriteController Controller for adding favorites
     * @param removeFavoriteController Controller for removing favorites
     * @param userDataAccess Data access for checking current favorites
     * @param loggedInViewModel View model to get current user ID
     */
    public static void setupFavoritesForPanel(
            RestaurantPanel panel,
            AddFavoriteController addFavoriteController,
            RemoveFavoriteController removeFavoriteController,
            UserDataAccessInterface userDataAccess,
            LoggedInViewModel loggedInViewModel) {

        // Check if this restaurant is already favorited
        String restaurantId = panel.getDisplayData().getId();
        boolean isCurrentlyFavorited = checkIfFavorited(restaurantId, userDataAccess, loggedInViewModel);
        panel.setFavorite(isCurrentlyFavorited);

        // Set up heart click listener
        panel.setHeartClickListener((id, newFavoriteState) -> {
            String userId = loggedInViewModel.getState().getUid();

            if (userId == null || userId.isEmpty()) {
                // User not logged in - revert the toggle
                panel.setFavorite(!newFavoriteState);
                return;
            }

            if (newFavoriteState) {
                // Adding to favorites
                addFavoriteController.execute(userId, id);
            } else {
                // Removing from favorites
                removeFavoriteController.execute(userId, id);
            }
        });
    }

    private static boolean checkIfFavorited(String restaurantId, UserDataAccessInterface userDataAccess, LoggedInViewModel loggedInViewModel) {
        try {
            String userId = loggedInViewModel.getState().getUid();
            if (userId == null || userId.isEmpty()) {
                return false;
            }

            User user = userDataAccess.getUser(userId);
            if (user != null) {
                return user.getFavoriteRestaurantIds().contains(restaurantId);
            }
        } catch (Exception e) {
            System.err.println("Error checking favorite status: " + e.getMessage());
        }
        return false;
    }
}
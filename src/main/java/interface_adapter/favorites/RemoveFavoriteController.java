package interface_adapter.favorites;

import use_case.favorites.remove_favorite.RemoveFavoriteInputBoundary;
import use_case.favorites.remove_favorite.RemoveFavoriteInputData;

/**
 * Controller for Remove Favorite use case.
 * Handles user request to remove a restaurant from their favorites.
 */
public class RemoveFavoriteController {

    private final RemoveFavoriteInputBoundary removeFavoriteInteractor;

    public RemoveFavoriteController(RemoveFavoriteInputBoundary removeFavoriteInteractor) {
        this.removeFavoriteInteractor = removeFavoriteInteractor;
    }

    /**
     * Executes the remove favorite use case.
     * @param userId the ID of the user removing the favorite
     * @param restaurantId the ID of the restaurant to remove
     */
    public void execute(String userId, String restaurantId) {
        final RemoveFavoriteInputData inputData = new RemoveFavoriteInputData(userId, restaurantId);
        removeFavoriteInteractor.execute(inputData);
    }
}
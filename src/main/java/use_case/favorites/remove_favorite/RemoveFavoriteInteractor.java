package use_case.favorites.remove_favorite;

import data_access.UserDataAccessInterface;
import entity.Restaurant;
import entity.User;

/**
 * Interactor for the Remove Favorite use case.
 * Handles the business logic for removing a restaurant from user's favorites.
 */
public class RemoveFavoriteInteractor implements RemoveFavoriteInputBoundary {

    private final UserDataAccessInterface userDataAccess;
    private final RemoveFavoriteOutputBoundary presenter;

    public RemoveFavoriteInteractor(UserDataAccessInterface userDataAccess,
                                    RemoveFavoriteOutputBoundary presenter) {
        this.userDataAccess = userDataAccess;
        this.presenter = presenter;
    }

    /**
     * Executes the remove favorite use case.
     * Removes the restaurant from user's favorites and saves the change.
     * @param inputData the input data containing user and restaurant IDs
     */
    @Override
    public void execute(RemoveFavoriteInputData inputData) {
        try {
            System.out.println("DEBUG RemoveFavoriteInteractor: Attempting to remove restaurant " + inputData.getRestaurantId());

            final User user = userDataAccess.getUser(inputData.getUserId());
            if (user == null) {
                System.out.println("DEBUG RemoveFavoriteInteractor: User not found");
                presenter.presentError("User not found");
                return;
            }

            System.out.println("DEBUG RemoveFavoriteInteractor: User found: " + user.getNickname());
            System.out.println("DEBUG RemoveFavoriteInteractor: Current favorites: " + user.getFavoriteRestaurantIds());
            System.out.println("DEBUG RemoveFavoriteInteractor: Checking if favorites contains: " + inputData.getRestaurantId());
            System.out.println("DEBUG RemoveFavoriteInteractor: Contains? " + user.getFavoriteRestaurantIds().contains(inputData.getRestaurantId()));

            // Check if the restaurant is actually in favorites
            if (!user.getFavoriteRestaurantIds().contains(inputData.getRestaurantId())) {
                presenter.presentError("Restaurant is not in favorites");
                return;
            }

            // Try to get restaurant name for the success message, but don't fail if not found
            String restaurantName = "Restaurant";
            try {
                final Restaurant restaurant = userDataAccess.getRestaurantById(inputData.getRestaurantId());
                if (restaurant != null) {
                    restaurantName = restaurant.getName();
                }
            } catch (Exception e) {
                // If we can't get the restaurant, just use generic name
                System.err.println("Could not fetch restaurant details: " + e.getMessage());
            }

            // Remove from favorites
            user.removeFavoriteRestaurantId(inputData.getRestaurantId());
            System.out.println("DEBUG RemoveFavoriteInteractor: After removal, favorites: " + user.getFavoriteRestaurantIds());

            userDataAccess.saveUser(user);

            final RemoveFavoriteOutputData outputData = new RemoveFavoriteOutputData(
                    restaurantName,
                    true,
                    restaurantName + " removed from favorites!"
            );

            presenter.presentSuccess(outputData);
        } catch (Exception exception) {
            presenter.presentError("Failed to remove favorite: " + exception.getMessage());
        }
    }
}
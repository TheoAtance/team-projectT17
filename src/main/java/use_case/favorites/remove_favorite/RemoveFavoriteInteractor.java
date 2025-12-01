package use_case.favorites.remove_favorite;

import data_access.UserDataAccessInterface;
import entity.Restaurant;
import entity.User;

/**
 * Interactor for the Remove Favorite use case. Handles the business logic for removing a restaurant
 * from user's favorites.
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
   * Executes the remove favorite use case. Removes the restaurant from user's favorites and saves
   * the change.
   *
   * @param inputData the input data containing user and restaurant IDs
   */
  @Override
  public void execute(RemoveFavoriteInputData inputData) {
    try {
      final User user = userDataAccess.getUser(inputData.getUserId());
      if (user == null) {
        presenter.presentError("User not found");
        return;
      }

      final Restaurant restaurant = userDataAccess.getRestaurantById(inputData.getRestaurantId());
      if (restaurant == null) {
        presenter.presentError("Restaurant not found");
        return;
      }

      user.removeFavoriteRestaurantId(inputData.getRestaurantId());
      userDataAccess.saveUser(user);

      final RemoveFavoriteOutputData outputData = new RemoveFavoriteOutputData(
          restaurant.getName(),
          true,
          restaurant.getName() + " removed from favorites!"
      );

      presenter.presentSuccess(outputData);
    } catch (Exception exception) {
      presenter.presentError("Failed to remove favorite: " + exception.getMessage());
    }
  }
}
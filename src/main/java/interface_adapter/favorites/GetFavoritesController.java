package interface_adapter.favorites;

import use_case.favorites.get_favorites.GetFavoritesInputBoundary;
import use_case.favorites.get_favorites.GetFavoritesInputData;

/**
 * Controller for Get Favorites use case. Handles user request to view their favorite restaurants.
 */
public class GetFavoritesController {

  private final GetFavoritesInputBoundary getFavoritesInteractor;

  public GetFavoritesController(GetFavoritesInputBoundary getFavoritesInteractor) {
    this.getFavoritesInteractor = getFavoritesInteractor;
  }

  /**
   * Executes the get favorites use case.
   *
   * @param userId the ID of the user whose favorites to retrieve
   */
  public void execute(String userId) {
    final GetFavoritesInputData inputData = new GetFavoritesInputData(userId);
    getFavoritesInteractor.execute(inputData);
  }
}
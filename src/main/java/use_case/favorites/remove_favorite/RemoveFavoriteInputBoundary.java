package use_case.favorites.remove_favorite;

/**
 * Input Boundary for the Remove Favorite use case. Defines the contract for executing the remove
 * favorite operation.
 */
public interface RemoveFavoriteInputBoundary {

  /**
   * Executes the remove favorite use case.
   *
   * @param inputData the input data containing user and restaurant information
   */
  void execute(RemoveFavoriteInputData inputData);
}
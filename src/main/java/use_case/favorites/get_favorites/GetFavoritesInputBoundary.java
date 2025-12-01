package use_case.favorites.get_favorites;

/**
 * Input Boundary for the Get Favorites use case. Defines the contract for executing the get
 * favorites operation.
 */
public interface GetFavoritesInputBoundary {

  /**
   * Executes the get favorites use case.
   *
   * @param inputData the input data containing user information
   */
  void execute(GetFavoritesInputData inputData);
}
package use_case.favorites.remove_favorite;

/**
 * Output Boundary for the Remove Favorite use case. Defines the contract for presenting removal
 * results.
 */
public interface RemoveFavoriteOutputBoundary {

  /**
   * Presents a successful removal result.
   *
   * @param outputData the output data containing removal confirmation
   */
  void presentSuccess(RemoveFavoriteOutputData outputData);

  /**
   * Presents an error message.
   *
   * @param error the error message to display
   */
  void presentError(String error);
}
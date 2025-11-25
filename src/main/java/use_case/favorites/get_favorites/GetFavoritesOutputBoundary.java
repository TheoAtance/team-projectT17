package use_case.favorites.get_favorites;

/**
 * Output Boundary for the Get Favorites use case.
 * Defines the contract for presenting favorites results.
 */
public interface GetFavoritesOutputBoundary {

    /**
     * Presents the user's favorite restaurants.
     * @param outputData the output data containing favorites information
     */
    void presentFavorites(GetFavoritesOutputData outputData);

    /**
     * Presents an error message.
     * @param error the error message to display
     */
    void presentError(String error);
}
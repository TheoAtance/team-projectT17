package use_case.favorites.get_favorites;

/**
 * Input Data for the Get Favorites use case. Contains the user ID whose favorites should be
 * retrieved.
 */
public class GetFavoritesInputData {

  private final String userId;

  public GetFavoritesInputData(String userId) {
    this.userId = userId;
  }

  public String getUserId() {
    return userId;
  }
}
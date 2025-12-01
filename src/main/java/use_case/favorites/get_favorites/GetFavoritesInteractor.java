package use_case.favorites.get_favorites;

import data_access.UserDataAccessInterface;
import entity.Restaurant;
import entity.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Interactor for the Get Favorites use case.
 * Handles the business logic for retrieving a user's favorite restaurants.
 */
public class GetFavoritesInteractor implements GetFavoritesInputBoundary {

    private final UserDataAccessInterface userDataAccess;
    private final GetFavoritesOutputBoundary presenter;

    public GetFavoritesInteractor(UserDataAccessInterface userDataAccess,
                                  GetFavoritesOutputBoundary presenter) {
        this.userDataAccess = userDataAccess;
        this.presenter = presenter;
    }

    /**
     * Executes the get favorites use case.
     * Retrieves the user and their favorite restaurants, then presents the results.
     * @param inputData the input data containing user ID
     */
    @Override
    public void execute(GetFavoritesInputData inputData) {
        try {
            final User user = userDataAccess.getUser(inputData.getUserId());
            if (user == null) {
                presenter.presentError("User not found");
                return;
            }

            final List<String> favoriteIds = user.getFavoriteRestaurantIds();
            final List<Restaurant> favoriteRestaurants = userDataAccess.getRestaurantsByIds(favoriteIds);

            // Create a map of restaurant CID -> Restaurant for easy lookup
            Map<String, Restaurant> restaurantMap = new HashMap<>();
            for (Restaurant restaurant : favoriteRestaurants) {
                restaurantMap.put(restaurant.getId(), restaurant);
            }

            final List<GetFavoritesOutputData.FavoriteRestaurantData> restaurantDataList = new ArrayList<>();

            // IMPORTANT: Iterate through favoriteIds to preserve the original ID format
            for (String favoriteId : favoriteIds) {
                Restaurant restaurant = userDataAccess.getRestaurantById(favoriteId);

                if (restaurant != null) {
                    // Use the ORIGINAL favoriteId (Google Places format), not restaurant.getId() (CID)
                    restaurantDataList.add(new GetFavoritesOutputData.FavoriteRestaurantData(
                            favoriteId,  // ← CHANGED: Use original ID from favorites list
                            restaurant.getName(),
                            restaurant.getType(),
                            restaurant.getRating(),
                            restaurant.hasStudentDiscount(),
                            restaurant.getDiscountValue(),
                            restaurant.getPhotoIds()
                    ));
                }
            }

            final GetFavoritesOutputData outputData = new GetFavoritesOutputData(
                    user.getUid(),
                    user.getNickname(),
                    restaurantDataList
            );

            presenter.presentFavorites(outputData);
        } catch (Exception exception) {
            presenter.presentError("Failed to load favorites: " + exception.getMessage());
        }
    }
}
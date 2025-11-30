package use_case.favorites.get_favorites;

import data_access.UserDataAccessInterface;
import entity.Restaurant;
import entity.User;

import java.util.ArrayList;
import java.util.List;

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

            final List<GetFavoritesOutputData.FavoriteRestaurantData> restaurantDataList = new ArrayList<>();
            for (Restaurant restaurant : favoriteRestaurants) {
                restaurantDataList.add(new GetFavoritesOutputData.FavoriteRestaurantData(
                        restaurant.getId(),
                        restaurant.getName(),
                        restaurant.getType(),
                        restaurant.getRating(),
                        restaurant.hasStudentDiscount(),
                        restaurant.getDiscountValue(),
                        restaurant.getPhotoIds()
                ));
            }

            final GetFavoritesOutputData outputData = new GetFavoritesOutputData(
                    user.getUid(),  // NEW: Pass user ID
                    user.getNickname(),
                    restaurantDataList
            );

            presenter.presentFavorites(outputData);
        } catch (Exception exception) {
            presenter.presentError("Failed to load favorites: " + exception.getMessage());
        }
    }
}
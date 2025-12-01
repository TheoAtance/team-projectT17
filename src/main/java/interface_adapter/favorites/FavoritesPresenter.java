package interface_adapter.favorites;

import interface_adapter.ImageDataAccessInterface;
import use_case.favorites.get_favorites.GetFavoritesOutputBoundary;
import use_case.favorites.get_favorites.GetFavoritesOutputData;
import use_case.favorites.get_favorites.GetFavoritesInputBoundary;
import use_case.favorites.get_favorites.GetFavoritesInputData;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import use_case.favorites.remove_favorite.RemoveFavoriteOutputBoundary;
import use_case.favorites.remove_favorite.RemoveFavoriteOutputData;

/**
 * Presenter for the Get Favorites use case.
 * Converts output data to view model state and fetches restaurant images.
 */
public class FavoritesPresenter implements GetFavoritesOutputBoundary, RemoveFavoriteOutputBoundary {

    private final FavoritesViewModel favoritesViewModel;
    private final ImageDataAccessInterface imageDataAccess;
    private final GetFavoritesInputBoundary getFavoritesInteractor;

    public FavoritesPresenter(FavoritesViewModel favoritesViewModel,
                              ImageDataAccessInterface imageDataAccess,
                              GetFavoritesInputBoundary getFavoritesInteractor) {
        this.favoritesViewModel = favoritesViewModel;
        this.imageDataAccess = imageDataAccess;
        this.getFavoritesInteractor = getFavoritesInteractor;
    }

    @Override
    public void presentFavorites(GetFavoritesOutputData outputData) {
        final FavoritesState state = new FavoritesState();
        state.setUserId(outputData.getUserId());
        state.setUsername(outputData.getUsername());

        final List<FavoritesState.RestaurantDisplayData> displayDataList = new ArrayList<>();
        final String apiKey = System.getenv("PLACES_API_TOKEN");

        for (GetFavoritesOutputData.FavoriteRestaurantData restaurantData : outputData.getRestaurants()) {
            // Fetch the first photo for each restaurant
            BufferedImage photo = null;

            if (restaurantData.getPhotoIds() != null && !restaurantData.getPhotoIds().isEmpty()) {
                if (apiKey != null) {
                    try {
                        photo = imageDataAccess.fetchRestaurantImage(
                                restaurantData.getPhotoIds().get(0),
                                apiKey
                        );
                    } catch (Exception e) {
                        System.err.println("Failed to fetch image for restaurant: " + restaurantData.getName());
                        e.printStackTrace();
                    }
                }
            }

            // If no photo was fetched, use placeholder
            if (photo == null) {
                try {
                    photo = ImageIO.read(Objects.requireNonNull(
                            getClass().getResource("/images/placeholder.png")
                    ));
                } catch (IOException e) {
                    System.err.println("Failed to load placeholder image");
                    e.printStackTrace();
                }
            }

            final FavoritesState.RestaurantDisplayData displayData =
                    new FavoritesState.RestaurantDisplayData(
                            restaurantData.getId(),
                            restaurantData.getName(),
                            restaurantData.getType(),
                            String.valueOf(restaurantData.getRating()),
                            restaurantData.hasDiscount(),
                            formatDiscount(restaurantData.getDiscountValue()),
                            photo
                    );

            displayDataList.add(displayData);
        }

        state.setRestaurants(displayDataList);
        favoritesViewModel.setState(state);
        favoritesViewModel.firePropertyChanged();
    }

    @Override
    public void presentError(String errorMessage) {
        final FavoritesState state = favoritesViewModel.getState();
        state.setErrorMessage(errorMessage);
        favoritesViewModel.setState(state);
        favoritesViewModel.firePropertyChanged();
    }

    private String formatDiscount(double discountValue) {
        return (int)(discountValue * 100) + "% off";
    }

    // ========= RemoveFavoriteOutputBoundary Implementation =========

    @Override
    public void presentSuccess(RemoveFavoriteOutputData outputData) {
        final FavoritesState state = favoritesViewModel.getState();
        state.setSuccessMessage(outputData.getMessage());
        favoritesViewModel.setState(state);
        favoritesViewModel.firePropertyChanged();

        // Reload favorites to refresh the view with updated list
        String userId = state.getUserId();
        getFavoritesInteractor.execute(new GetFavoritesInputData(userId));
    }
}
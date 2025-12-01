package interface_adapter.favorites;

import use_case.favorites.add_favorite.AddFavoriteOutputBoundary;
import use_case.favorites.add_favorite.AddFavoriteOutputData;

public class AddFavoritePresenter implements AddFavoriteOutputBoundary {
    private final FavoritesViewModel favoritesViewModel;

    public AddFavoritePresenter(FavoritesViewModel favoritesViewModel) {
        this.favoritesViewModel = favoritesViewModel;
    }

    @Override
    public void presentSuccess(AddFavoriteOutputData outputData) {
        // Just show success - the view will handle updating the heart icon
        System.out.println(outputData.getMessage());
    }

    @Override
    public void presentError(String errorMessage) {
        System.err.println("Add favorite error: " + errorMessage);
    }
}
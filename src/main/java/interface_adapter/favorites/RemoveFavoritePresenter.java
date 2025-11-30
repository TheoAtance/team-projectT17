package interface_adapter.favorites;

import use_case.favorites.remove_favorite.RemoveFavoriteOutputBoundary;
import use_case.favorites.remove_favorite.RemoveFavoriteOutputData;

public class RemoveFavoritePresenter implements RemoveFavoriteOutputBoundary {
    private final FavoritesViewModel favoritesViewModel;

    public RemoveFavoritePresenter(FavoritesViewModel favoritesViewModel) {
        this.favoritesViewModel = favoritesViewModel;
    }

    @Override
    public void presentSuccess(RemoveFavoriteOutputData outputData) {
        System.out.println(outputData.getMessage());
    }

    @Override
    public void presentError(String errorMessage) {
        System.err.println("Error removing favorite: " + errorMessage);
    }
}
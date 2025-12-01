package use_case.favorites.add_favorite;

public interface AddFavoriteOutputBoundary {
    void presentSuccess(AddFavoriteOutputData outputData);
    void presentError(String errorMessage);
}
package interface_adapter.favorites;

import use_case.favorites.add_favorite.AddFavoriteInputBoundary;
import use_case.favorites.add_favorite.AddFavoriteInputData;

public class AddFavoriteController {
    private final AddFavoriteInputBoundary addFavoriteInteractor;

    public AddFavoriteController(AddFavoriteInputBoundary addFavoriteInteractor) {
        this.addFavoriteInteractor = addFavoriteInteractor;
    }

    public void execute(String userId, String restaurantId) {
        AddFavoriteInputData inputData = new AddFavoriteInputData(userId, restaurantId);
        addFavoriteInteractor.execute(inputData);
    }
}
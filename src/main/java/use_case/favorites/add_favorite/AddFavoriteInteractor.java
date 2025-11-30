package use_case.favorites.add_favorite;

import data_access.UserDataAccessInterface;
import entity.User;

public class AddFavoriteInteractor implements AddFavoriteInputBoundary {
    private final UserDataAccessInterface userDataAccess;
    private final AddFavoriteOutputBoundary presenter;

    public AddFavoriteInteractor(UserDataAccessInterface userDataAccess,
                                 AddFavoriteOutputBoundary presenter) {
        this.userDataAccess = userDataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(AddFavoriteInputData inputData) {
        try {
            // Get the user
            User user = userDataAccess.getUser(inputData.getUserId());

            if (user == null) {
                presenter.presentError("User not found");
                return;
            }

            // Check if already favorited
            if (user.getFavoriteRestaurantIds().contains(inputData.getRestaurantId())) {
                presenter.presentError("Restaurant is already in favorites");
                return;
            }

            // Add to favorites
            user.addFavoriteRestaurantId(inputData.getRestaurantId());

            // Save to database
            userDataAccess.saveUser(user);

            // Present success
            AddFavoriteOutputData outputData = new AddFavoriteOutputData(
                    "Restaurant added to favorites!",
                    true
            );
            presenter.presentSuccess(outputData);

        } catch (Exception e) {
            presenter.presentError("Failed to add favorite: " + e.getMessage());
        }
    }
}
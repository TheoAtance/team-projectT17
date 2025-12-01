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
        System.out.println("AddFavoriteInteractor.execute called");
        System.out.println("  User ID: " + inputData.getUserId());
        System.out.println("  Restaurant ID: " + inputData.getRestaurantId());

        try {
            // Get the user
            User user = userDataAccess.getUser(inputData.getUserId());

            if (user == null) {
                System.out.println("  ERROR: User not found");
                presenter.presentError("User not found");
                return;
            }

            System.out.println("  User found: " + user.getNickname());
            System.out.println("  Current favorites: " + user.getFavoriteRestaurantIds());

            // Check if already favorited
            if (user.getFavoriteRestaurantIds().contains(inputData.getRestaurantId())) {
                System.out.println("  Already favorited!");
                presenter.presentError("Restaurant is already in favorites");
                return;
            }

            // Add to favorites
            user.addFavoriteRestaurantId(inputData.getRestaurantId());
            System.out.println("  Added to favorites list");
            System.out.println("  New favorites: " + user.getFavoriteRestaurantIds());

            // Save to database
            userDataAccess.saveUser(user);
            System.out.println("  Saved to database");

            // Present success
            AddFavoriteOutputData outputData = new AddFavoriteOutputData(
                    "Restaurant added to favorites!",
                    true
            );
            presenter.presentSuccess(outputData);

        } catch (Exception e) {
            System.out.println("  EXCEPTION: " + e.getMessage());
            e.printStackTrace();
            presenter.presentError("Failed to add favorite: " + e.getMessage());
        }
    }
}
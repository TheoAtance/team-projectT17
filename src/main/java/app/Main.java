package app;

import data_access.FirebaseService;

import javax.swing.*;

/**
 * The Main class for the Restaurant Review Application.
 * This is the entry point that initializes Firebase and launches the UI.
 */
public class Main {
    public static void main(String[] args) {
        // Initialize Firebase
        FirebaseService firebaseService = FirebaseService.getInstance();

        // Build the application
        AppBuilder appBuilder = new AppBuilder();
        JFrame application = appBuilder
                .addRestaurantView()
                .addLoginView()
                .addRegisterView()
                .addLoggedInView()
                .addFilterView()
                .addRestaurantUseCase()
                .addFavoritesUseCase()
                .addAddFavoriteToRestaurantView()
                .addAddReviewUseCase()
                .addDisplayReviewUseCase()
                .build();

        // Display the application
        application.pack();
        application.setLocationRelativeTo(null); // Center on screen
        application.setVisible(true);
    }
}
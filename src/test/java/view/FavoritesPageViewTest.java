package view;

import data_access.FirebaseService;
import data_access.FirestoreUserRepo;
import data_access.GooglePlacesGateway;
import data_access.UserDataAccessInterface;
import interface_adapter.favorites.FavoritesPresenter;
import interface_adapter.favorites.FavoritesViewModel;
import interface_adapter.favorites.GetFavoritesController;
import interface_adapter.favorites.RemoveFavoriteController;
import use_case.favorites.get_favorites.GetFavoritesInputBoundary;
import use_case.favorites.get_favorites.GetFavoritesInteractor;
import use_case.favorites.remove_favorite.RemoveFavoriteInteractor;

import javax.swing.*;

/**
 * Test to display FavoritesView with real data from Firebase and Google Places API
 */
class FavoritesViewTest {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Initialize Firebase
                System.out.println("Initializing Firebase...");
                FirebaseService.getInstance();
                System.out.println("Firebase initialized successfully.");

                // Create data access objects
                UserDataAccessInterface userRepo = new FirestoreUserRepo();
                GooglePlacesGateway imageGateway = new GooglePlacesGateway();

                // Create view model
                FavoritesViewModel favoritesViewModel = new FavoritesViewModel();

                // Create presenter with circular dependency resolution
                GetFavoritesInputBoundary getFavoritesInteractor;
                FavoritesPresenter presenter;

                // Step 1: Create temporary presenter
                presenter = new FavoritesPresenter(
                        favoritesViewModel,
                        imageGateway,
                        null
                );

                // Step 2: Create Get Favorites Interactor
                getFavoritesInteractor = new GetFavoritesInteractor(
                        userRepo,
                        presenter
                );

                // Step 3: Recreate presenter with interactor
                presenter = new FavoritesPresenter(
                        favoritesViewModel,
                        imageGateway,
                        getFavoritesInteractor
                );

                // Step 4: Recreate Get Favorites Interactor with new presenter
                getFavoritesInteractor = new GetFavoritesInteractor(
                        userRepo,
                        presenter
                );

                // Step 5: Create Remove Favorite Interactor
                RemoveFavoriteInteractor removeFavoriteInteractor = new RemoveFavoriteInteractor(
                        userRepo,
                        presenter
                );

                // Create controllers
                GetFavoritesController getFavoritesController = new GetFavoritesController(
                        getFavoritesInteractor
                );

                RemoveFavoriteController removeFavoriteController = new RemoveFavoriteController(
                        removeFavoriteInteractor
                );

                // IMPORTANT: Use a real user ID from your Firebase
                String testUserId = "HQOx2etUGVWQw5lhPqVFibiIzT22";

                // Create the favorites view (now a JPanel)
                FavoritesView favoritesView = new FavoritesView(favoritesViewModel);

                // Set controllers
                favoritesView.setGetFavoritesController(getFavoritesController);
                favoritesView.setRemoveFavoriteController(removeFavoriteController);

                // Create a JFrame to hold the panel
                JFrame frame = new JFrame("Favorites Test");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(900, 600);
                frame.setLocationRelativeTo(null);
                frame.add(favoritesView);
                frame.setVisible(true);

                // Set the userId in the state before loading
                favoritesViewModel.getState().setUserId(testUserId);

                // Load favorites from Firebase
                favoritesView.loadFavorites();

                System.out.println("Favorites page loaded successfully!");
                System.out.println("User ID: " + testUserId);
                System.out.println("Click the heart icons to remove from favorites");
                System.out.println("Images will be fetched from Google Places API");

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Error: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
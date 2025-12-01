package app;

import data_access.*;
import entity.RestaurantFactory;
import interface_adapter.ViewManagerModel;
import interface_adapter.add_review.AddReviewController;
import interface_adapter.add_review.AddReviewPresenter;
import interface_adapter.add_review.AddReviewViewModel;
import interface_adapter.display_reviews.DisplayReviewsController;
import interface_adapter.display_reviews.DisplayReviewsPresenter;
import interface_adapter.display_reviews.DisplayReviewsViewModel;
import interface_adapter.favorites.FavoritesPresenter;
import interface_adapter.favorites.FavoritesViewModel;
import interface_adapter.favorites.GetFavoritesController;
import interface_adapter.favorites.RemoveFavoriteController;
import interface_adapter.favorites.AddFavoriteController;
import interface_adapter.favorites.AddFavoritePresenter;
import interface_adapter.favorites.RemoveFavoritePresenter;
import interface_adapter.filter.FilterController;
import interface_adapter.filter.FilterPresenter;
import interface_adapter.filter.FilterViewModel;
import interface_adapter.google_login.GoogleLoginController;
import interface_adapter.google_login.GoogleLoginPresenter;
import interface_adapter.list_search.ListSearchController;
import interface_adapter.list_search.ListSearchPresenter;
import interface_adapter.list_search.ListSearchViewModel;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.login.LoginController;
import interface_adapter.login.LoginPresenter;
import interface_adapter.login.LoginViewModel;
import interface_adapter.logout.LogoutController;
import interface_adapter.logout.LogoutPresenter;
import interface_adapter.random_restauarant.RandomRestaurantController;
import interface_adapter.register.RegisterController;
import interface_adapter.register.RegisterPresenter;
import interface_adapter.register.RegisterViewModel;
import interface_adapter.view_restaurant.ViewRestaurantController;
import interface_adapter.view_restaurant.ViewRestaurantPresenter;
import interface_adapter.view_restaurant.ViewRestaurantViewModel;
import use_case.IAuthGateway;
import use_case.IUserRepo;
import use_case.add_review.AddReviewInputBoundary;
import use_case.add_review.AddReviewInteractor;
import use_case.add_review.AddReviewOutputBoundary;
import use_case.custom_login.CustomLoginInputBoundary;
import use_case.custom_login.CustomLoginUserInteractor;
import use_case.custom_register.RegisterInputBoundary;
import use_case.custom_register.RegisterUserInteractor;
import use_case.display_reviews.DisplayReviewsInputBoundary;
import use_case.display_reviews.DisplayReviewsInteractor;
import use_case.display_reviews.DisplayReviewsOutputBoundary;
import use_case.favorites.get_favorites.GetFavoritesInputBoundary;
import use_case.favorites.get_favorites.GetFavoritesInteractor;
import use_case.favorites.remove_favorite.RemoveFavoriteInputBoundary;
import use_case.favorites.remove_favorite.RemoveFavoriteInteractor;
import use_case.favorites.add_favorite.AddFavoriteInputBoundary;
import use_case.favorites.add_favorite.AddFavoriteInteractor;
import use_case.filter.FilterInputBoundary;
import use_case.filter.FilterInteractor;
import use_case.google_login.GoogleLoginInputBoundary;
import use_case.google_login.GoogleLoginInteractor;
import use_case.list_search.ListSearchInputBoundary;
import use_case.list_search.ListSearchInteractor;
import use_case.logout.LogoutInputBoundary;
import use_case.logout.LogoutUserInteractor;
import use_case.random_restaurant.RandomRestaurantInputBoundary;
import use_case.random_restaurant.RandomRestaurantInteractor;
import use_case.view_restaurant.ViewRestaurantInputBoundary;
import use_case.view_restaurant.ViewRestaurantInteractor;
import use_case.view_restaurant.ViewRestaurantOutputBoundary;
import view.*;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * The AppBuilder is responsible for constructing and wiring together
 * all the components of the application using dependency injection.
 */
public class AppBuilder {
    final ViewManagerModel viewManagerModel = new ViewManagerModel();
    private final JPanel cardPanel = new JPanel();
    private final CardLayout cardLayout = new CardLayout();
    final ViewManager viewManager = new ViewManager(cardPanel, cardLayout, viewManagerModel);

    // Add review
    private final AddReviewViewModel addReviewViewModel = new AddReviewViewModel();
    // Display Review
    private final DisplayReviewsViewModel displayReviewsViewModel = new DisplayReviewsViewModel();

    // Shared data access objects
    private final IAuthGateway authGateway = new FirebaseUserAuth();
    private final IUserRepo userRepository = new FirestoreUserRepo();
    private final CurrentUser currentUser = new CurrentUser(authGateway, userRepository);
    private final GooglePlacesGateway googlePlacesGateway = new GooglePlacesGateway();

    // ======== View Models ========
    // Home page
    private LoggedInView loggedInView;
    // Account
    private LoginViewModel loginViewModel;
    private RegisterViewModel registerViewModel;
    private LoggedInViewModel loggedInViewModel;

    // Filter
    private FilterViewModel filterViewModel;
    // Restaurant info
    private ViewRestaurantViewModel viewRestaurantViewModel;
    private RestaurantView restaurantView;
    private JsonRestaurantDataAccessObject restaurantDataAccess;
    private JsonReviewDataAccessObject reviewDataAccess;
    // Shared Google Login Controller
    private GoogleLoginController googleLoginController;

    /**
     * Creates the App.
     */
    public AppBuilder() {
        // tell card panel to use cardLayout to manage its layout.
        cardPanel.setLayout(cardLayout);

        // Initialize restaurant data access
        try {
            RestaurantFactory restaurantFactory = new RestaurantFactory();
            this.restaurantDataAccess = new JsonRestaurantDataAccessObject(
                    "src/main/java/data/restaurant.json",
                    restaurantFactory
            );

            this.reviewDataAccess = new JsonReviewDataAccessObject("src/main/java/data/reviews.json");

        } catch (IOException e) {
            System.err.println("Failed to load restaurant data: " + e.getMessage());
        }
    }

    /**
     * Adds the Login View to the application.
     *
     * @return log in view.
     */
    public AppBuilder addLoginView() {
        // Create View Model
        loginViewModel = new LoginViewModel();

        // Create Logged In View Model (needed by presenter)
        if (loggedInViewModel == null) {
            loggedInViewModel = new LoggedInViewModel();
        }

        // Create Presenter
        LoginPresenter loginPresenter = new LoginPresenter(
                viewManagerModel,
                loggedInViewModel,
                loginViewModel
        );

        // Create Interactor
        CustomLoginInputBoundary loginInteractor = new CustomLoginUserInteractor(
                authGateway,
                userRepository,
                loginPresenter
        );

        // Create Controller
        LoginController loginController = new LoginController(
                loginInteractor,
                viewManagerModel,
                "register" // Register view name
        );

        // Create Google Login Controller (shared)
        if (googleLoginController == null) {
            createGoogleLoginController();
        }

        // Create View
        LoginView loginView = new LoginView(loginViewModel);
        loginView.setLoginController(loginController);
        loginView.setGoogleLoginController(googleLoginController);

        // Add to card panel
        cardPanel.add(loginView, loginView.getViewName());

        return this;
    }

    /**
     * Adds the Register View to the application.
     *
     * @return register view.
     */
    public AppBuilder addRegisterView() {
        // Create View Model
        registerViewModel = new RegisterViewModel();

        // Ensure Login View Model exists (for navigation)
        if (loginViewModel == null) {
            loginViewModel = new LoginViewModel();
        }

        // Create Presenter
        RegisterPresenter registerPresenter = new RegisterPresenter(
                registerViewModel,
                loginViewModel,
                viewManagerModel
        );

        // Create Interactor
        RegisterInputBoundary registerInteractor = new RegisterUserInteractor(
                authGateway,
                userRepository,
                registerPresenter
        );

        // Create Controller
        RegisterController registerController = new RegisterController(
                registerInteractor,
                viewManagerModel,
                "login" // Login view name
        );

        // Use shared Google Login Controller
        if (googleLoginController == null) {
            createGoogleLoginController();
        }

        // Create View
        RegisterView registerView = new RegisterView(registerViewModel);
        registerView.setRegisterController(registerController);
        registerView.setGoogleLoginController(googleLoginController);

        // Add to card panel
        cardPanel.add(registerView, registerView.getViewName());

        return this;
    }

    /**
     * Adds the Logged In View to the application.
     *
     * @return the logged in view.
     */
    @SuppressWarnings({"checkstyle:VariableDeclarationUsageDistance", "checkstyle:Indentation"})
    public AppBuilder addLoggedInView() {
        ListSearchViewModel listSearchViewModel;
        // Create View Model (if not already created)
        if (loggedInViewModel == null) {
            loggedInViewModel = new LoggedInViewModel();
        }

        // Create ListSearchViewModel
        listSearchViewModel = new ListSearchViewModel();

        // Create Logout Presenter
        LogoutPresenter logoutPresenter = new LogoutPresenter(
                loginViewModel,
                viewManagerModel,
                loggedInViewModel
        );

        // Create Logout Interactor
        LogoutInputBoundary logoutInteractor = new LogoutUserInteractor(
                authGateway,
                logoutPresenter,
                currentUser
        );

        // Create Logout Controller
        LogoutController logoutController = new LogoutController(logoutInteractor);

        // Create HeartClickListener (placeholder - actual listener created in view)
        RestaurantPanel.HeartClickListener heartListener = (restaurantId, newState) ->
                System.out.println("Heart toggled for: " + restaurantId + " → " + newState);

        // Create View
        loggedInView = new LoggedInView(loggedInViewModel);
        loggedInView.setLogoutController(logoutController);
        loggedInView.setViewManagerModel(viewManagerModel);
        loggedInView.setViewRestaurantViewModel(viewRestaurantViewModel);

        // Set list search dependencies using setters
        loggedInView.setListSearchViewModel(listSearchViewModel);
        loggedInView.setHeartListener(heartListener);
        loggedInView.setFilterViewName(FilterView.VIEW_NAME);

        // Set restaurant data access and image access for loading images
        loggedInView.setRestaurantDataAccess(restaurantDataAccess);
        loggedInView.setImageDataAccess(googlePlacesGateway);

        // Create ListSearch components
        ListSearchPresenter listSearchPresenter = new ListSearchPresenter(listSearchViewModel);
        ListSearchInputBoundary listSearchInteractor = new ListSearchInteractor(restaurantDataAccess,
                listSearchPresenter);
        ListSearchController listSearchController = new ListSearchController(listSearchInteractor);

        loggedInView.setSearchController(listSearchController);
        listSearchController.search("");

        // Add to card panel
        cardPanel.add(loggedInView, loggedInView.getViewName());

        return this;
    }

    /**
     * Adds the Filter View to the application.
     *
     * @return filter view.
     */
    public AppBuilder addFilterView() {
        // Create View Model
        if (filterViewModel == null) {
            filterViewModel = new FilterViewModel();
        }

        // Create Presenter
        FilterPresenter filterPresenter = new FilterPresenter(filterViewModel);

        // Create Interactor
        FilterInputBoundary filterInteractor = new FilterInteractor(
                restaurantDataAccess,
                filterPresenter
        );

        // Create Controller
        FilterController filterController = new FilterController(filterInteractor);

        // Create View
        FilterView filterView = new FilterView(filterViewModel);
        filterView.setFilterController(filterController);
        filterView.setViewManagerModel(viewManagerModel);

        // Add to card panel
        cardPanel.add(filterView, filterView.getViewName());

        return this;
    }

    /**
     * Creates the Restaurant View.
     */
    public AppBuilder addRestaurantView() {
        viewRestaurantViewModel = new ViewRestaurantViewModel();
        restaurantView = new RestaurantView(viewRestaurantViewModel);
        cardPanel.add(restaurantView, restaurantView.getViewName());

        return this;
    }

    /**
     * Creates the Restaurant.
     */
    @SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
    public AppBuilder addRestaurantUseCase() {
        final ViewRestaurantOutputBoundary viewRestaurantOutputBoundary =
                new ViewRestaurantPresenter(viewManagerModel, viewRestaurantViewModel, googlePlacesGateway);

        final ViewRestaurantInputBoundary viewRestaurantInteractor =
                new ViewRestaurantInteractor(restaurantDataAccess, viewRestaurantOutputBoundary);

        final RandomRestaurantInputBoundary randomRestaurantInteractor =
                new RandomRestaurantInteractor(restaurantDataAccess, viewRestaurantOutputBoundary);

        ViewRestaurantController viewRestaurantController = new ViewRestaurantController(
                viewRestaurantInteractor);
        RandomRestaurantController randomRestaurantController = new RandomRestaurantController(
                randomRestaurantInteractor);

        restaurantView.setViewRestaurantController(viewRestaurantController);
        restaurantView.setLoggedInViewModel(loggedInViewModel);
        restaurantView.setViewManagerModel(viewManagerModel);
        restaurantView.setAddReviewViewModel(addReviewViewModel);
        restaurantView.setDisplayReviewViewModel(displayReviewsViewModel);

        loggedInView.setViewRestaurantController(viewRestaurantController);
        loggedInView.setRandomRestaurantController(randomRestaurantController);

        return this;
    }

    /**
     * Creates the Review.
     */
    public AppBuilder addAddReviewUseCase() {
        final AddReviewOutputBoundary addReviewOutputBoundary =
                new AddReviewPresenter(viewManagerModel, addReviewViewModel);

        final AddReviewInputBoundary addReviewInteractor =
                new AddReviewInteractor(addReviewOutputBoundary, reviewDataAccess, currentUser);

        AddReviewController addReviewController = new AddReviewController(addReviewInteractor);
        restaurantView.setAddReviewController(addReviewController);

        return this;
    }

    /**
     * Creates the Display.
     */
    public AppBuilder addDisplayReviewUseCase() {
        final DisplayReviewsOutputBoundary DisplayReviewPresenter =
                new DisplayReviewsPresenter(displayReviewsViewModel);

        final DisplayReviewsInputBoundary displayReviewsInteractor =
                new DisplayReviewsInteractor(reviewDataAccess, DisplayReviewPresenter, userRepository);

        DisplayReviewsController displayReviewsController = new DisplayReviewsController(
                displayReviewsInteractor);
        restaurantView.setDisplayReviewController(displayReviewsController);

        return this;
    }

    /**
     * Adds the Favorites use case to the application.
     * Handles both viewing and removing favorites.
     *
     * @return this AppBuilder for method chaining
     */
    public AppBuilder addFavoritesUseCase() {
        // Create View Model
        FavoritesViewModel favoritesViewModel = new FavoritesViewModel();

        // Create unified Presenter with circular dependency resolution
        GetFavoritesInputBoundary getFavoritesInteractor;
        FavoritesPresenter favoritesPresenter;

        // Step 1: Create temporary presenter
        favoritesPresenter = new FavoritesPresenter(
                favoritesViewModel,
                googlePlacesGateway,
                null  // Temporarily null
        );

        // Step 2: Create Get Favorites Interactor
        getFavoritesInteractor = new GetFavoritesInteractor(
                (UserDataAccessInterface) userRepository,
                favoritesPresenter
        );

        // Step 3: Recreate presenter with interactor
        favoritesPresenter = new FavoritesPresenter(
                favoritesViewModel,
                googlePlacesGateway,
                getFavoritesInteractor
        );

        // Step 4: Recreate Get Favorites Interactor with new presenter
        getFavoritesInteractor = new GetFavoritesInteractor(
                (UserDataAccessInterface) userRepository,
                favoritesPresenter
        );

        // Step 5: Create Remove Favorite Interactor
        RemoveFavoriteInputBoundary removeFavoriteInteractor = new RemoveFavoriteInteractor(
                (UserDataAccessInterface) userRepository,
                favoritesPresenter
        );

        // Create Controllers
        GetFavoritesController getFavoritesController = new GetFavoritesController(getFavoritesInteractor);
        RemoveFavoriteController removeFavoriteController = new RemoveFavoriteController(removeFavoriteInteractor);

        // Create View
        FavoritesView favoritesView = new FavoritesView(favoritesViewModel);
        favoritesView.setGetFavoritesController(getFavoritesController);
        favoritesView.setRemoveFavoriteController(removeFavoriteController);

        // Set navigation dependencies so clicking restaurants works
        if (viewRestaurantViewModel != null && restaurantView != null) {
            favoritesView.setViewRestaurantController(restaurantView.getViewRestaurantController());
            favoritesView.setViewManagerModel(viewManagerModel);
            favoritesView.setViewRestaurantViewModel(viewRestaurantViewModel);
        }

        // Add to card panel
        cardPanel.add(favoritesView, favoritesView.getViewName());

        // Wire up favorites to LoggedInView
        if (loggedInView != null) {
            loggedInView.setFavoritesViewModel(favoritesViewModel);
            loggedInView.setGetFavoritesController(getFavoritesController);
            loggedInView.setAddFavoriteController(new AddFavoriteController(
                    new AddFavoriteInteractor((UserDataAccessInterface) userRepository,
                            new AddFavoritePresenter(favoritesViewModel))
            ));
            loggedInView.setRemoveFavoriteController(removeFavoriteController);
            loggedInView.setUserDataAccess((UserDataAccessInterface) userRepository);
            // Note: restaurantDataAccess and imageDataAccess already set in addLoggedInView()
        }

        return this;
    }

    /**
     * Adds Add/Remove Favorite functionality to RestaurantView.
     *
     * @return this AppBuilder for method chaining
     */
    public AppBuilder addAddFavoriteToRestaurantView() {
        if (restaurantView == null) {
            throw new IllegalStateException("Restaurant view must be created first");
        }

        // Create shared view model
        FavoritesViewModel favoritesViewModel = new FavoritesViewModel();

        // Create AddFavorite presenter
        AddFavoritePresenter addFavoritePresenter = new AddFavoritePresenter(favoritesViewModel);

        // Create AddFavorite interactor
        AddFavoriteInputBoundary addFavoriteInteractor = new AddFavoriteInteractor(
                (UserDataAccessInterface) userRepository,
                addFavoritePresenter
        );

        // Create AddFavorite controller
        AddFavoriteController addFavoriteController = new AddFavoriteController(addFavoriteInteractor);

        // Create RemoveFavorite presenter
        RemoveFavoritePresenter removeFavoritePresenter = new RemoveFavoritePresenter(favoritesViewModel);

        // Create RemoveFavorite interactor
        RemoveFavoriteInputBoundary removeFavoriteInteractor = new RemoveFavoriteInteractor(
                (UserDataAccessInterface) userRepository,
                removeFavoritePresenter
        );

        // Create RemoveFavorite controller
        RemoveFavoriteController removeFavoriteController = new RemoveFavoriteController(removeFavoriteInteractor);

        // Set controllers and data access on restaurant view
        restaurantView.setAddFavoriteController(addFavoriteController);
        restaurantView.setRemoveFavoriteController(removeFavoriteController);
        restaurantView.setUserDataAccess((UserDataAccessInterface) userRepository);

        return this;
    }

    /**
     * Creates the frame.
     */
    public JFrame build() {
        final JFrame application = new JFrame("UofT Eats - Restaurant Review App");
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        application.add(cardPanel);

        viewManager.setApp(application);

        viewManagerModel.setState(loginViewModel.getViewName());
        viewManagerModel.firePropertyChange();

        return application;
    }

    /**
     * Creates the shared Google Login Controller (used by both Login and Register views).
     */
    private void createGoogleLoginController() {
        // Ensure all required view models exist
        if (loginViewModel == null) {
            loginViewModel = new LoginViewModel();
        }
        if (registerViewModel == null) {
            registerViewModel = new RegisterViewModel();
        }
        if (loggedInViewModel == null) {
            loggedInViewModel = new LoggedInViewModel();
        }

        // Create Google Login Presenter
        GoogleLoginPresenter googleLoginPresenter = new GoogleLoginPresenter(
                viewManagerModel,
                loggedInViewModel,
                loginViewModel,
                registerViewModel
        );

        // Create Google Login Interactor
        GoogleLoginInputBoundary googleLoginInteractor = new GoogleLoginInteractor(
                authGateway,
                userRepository,
                googleLoginPresenter
        );

        // Create Google Login Controller
        googleLoginController = new GoogleLoginController(googleLoginInteractor);
    }
}
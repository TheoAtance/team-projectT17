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
import interface_adapter.filter.FilterController;
import interface_adapter.filter.FilterPresenter;
import interface_adapter.filter.FilterViewModel;
import interface_adapter.google_login.GoogleLoginController;
import interface_adapter.google_login.GoogleLoginPresenter;
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
import interface_adapter.translation.TranslationController;
import interface_adapter.translation.TranslationPresenter;
import interface_adapter.view_restaurant.ViewRestaurantController;
import interface_adapter.view_restaurant.ViewRestaurantPresenter;
import interface_adapter.view_restaurant.ViewRestaurantViewModel;
import interface_adapter.translation.TranslationViewModel;
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
import use_case.display_reviews.DisplayReviewsOutputData;
import use_case.filter.FilterInputBoundary;
import use_case.filter.FilterInteractor;
import use_case.google_login.GoogleLoginInputBoundary;
import use_case.google_login.GoogleLoginInteractor;
import use_case.logout.LogoutInputBoundary;
import use_case.logout.LogoutUserInteractor;

import use_case.random_restaurant.RandomRestaurantInputBoundary;
import use_case.random_restaurant.RandomRestaurantInteractor;
import use_case.translation.DeeplTranslationService;
import use_case.translation.TranslationInputBoundary;
import use_case.translation.TranslationInteractor;
import use_case.view_restaurant.ViewRestaurantInputBoundary;
import use_case.view_restaurant.ViewRestaurantInteractor;
import use_case.view_restaurant.ViewRestaurantOutputBoundary;
import use_case.translation.TranslationService;
import view.*;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * The AppBuilder is responsible for constructing and wiring together
 * all the components of the application using dependency injection.
 */
public class AppBuilder {
    private final JPanel cardPanel = new JPanel();
    private final CardLayout cardLayout = new CardLayout();
    final ViewManagerModel viewManagerModel = new ViewManagerModel();
    final ViewManager viewManager = new ViewManager(cardPanel, cardLayout, viewManagerModel);


    // ======== View Models ========
    //Home page
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

    //Add review
    private final AddReviewViewModel addReviewViewModel = new AddReviewViewModel();

    //Display Review
    private final DisplayReviewsViewModel displayReviewsViewModel = new DisplayReviewsViewModel();


    // Shared data access objects
    private final IAuthGateway authGateway = new FirebaseUserAuth();
    private final IUserRepo userRepository = new FirestoreUserRepo();
    private final CurrentUser currentUser = new CurrentUser(authGateway, userRepository);

    private JsonRestaurantDataAccessObject restaurantDataAccess;
    private JsonReviewDataAccessObject reviewDataAccess;

    private GooglePlacesGateway googlePlacesGateway = new GooglePlacesGateway();


    // Shared Google Login Controller
    private GoogleLoginController googleLoginController;
    private TranslationViewModel translationViewModel;

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
     * @return
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
     * @return
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
     * @return
     */
    public AppBuilder addLoggedInView() {
        // Create View Model (if not already created)
        if (loggedInViewModel == null) {
            loggedInViewModel = new LoggedInViewModel();
        }

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

        // Create View
        loggedInView = new LoggedInView(loggedInViewModel);
        loggedInView.setLogoutController(logoutController);
        loggedInView.setViewManagerModel(viewManagerModel);
        loggedInView.setViewRestaurantViewModel(viewRestaurantViewModel);

        // Add to card panel
        cardPanel.add(loggedInView, loggedInView.getViewName());

        return this;
    }

    /**
     * Adds the Filter View to the application.
     *
     * @return
     */
    public AppBuilder addFilterView() {
        // Create View Model
        filterViewModel = new FilterViewModel();

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

    public AppBuilder addRestaurantView(){

        viewRestaurantViewModel = new ViewRestaurantViewModel();
        restaurantView = new RestaurantView(viewRestaurantViewModel);
        cardPanel.add(restaurantView, restaurantView.getViewName());


        return this;
    }


    public AppBuilder addRestaurantUseCase(){
        final ViewRestaurantOutputBoundary viewRestaurantOutputBoundary =
                new ViewRestaurantPresenter(viewManagerModel, viewRestaurantViewModel, googlePlacesGateway);

        final ViewRestaurantInputBoundary viewRestaurantInteractor =
                new ViewRestaurantInteractor(restaurantDataAccess, viewRestaurantOutputBoundary);

        final RandomRestaurantInputBoundary randomRestaurantInteractor =
                new RandomRestaurantInteractor(restaurantDataAccess, viewRestaurantOutputBoundary);

        ViewRestaurantController viewRestaurantController = new ViewRestaurantController(viewRestaurantInteractor);
        RandomRestaurantController randomRestaurantController = new RandomRestaurantController(randomRestaurantInteractor);

        restaurantView.setViewRestaurantController(viewRestaurantController);
        restaurantView.setLoggedInViewModel(loggedInViewModel);
        restaurantView.setViewManagerModel(viewManagerModel);
        restaurantView.setAddReviewViewModel(addReviewViewModel);
        restaurantView.setDisplayReviewViewModel(displayReviewsViewModel);

        loggedInView.setViewRestaurantController(viewRestaurantController);
        loggedInView.setRandomRestaurantController(randomRestaurantController);

        return this;
    }


    public AppBuilder addAddReviewUseCase(){
        final AddReviewOutputBoundary addReviewOutputBoundary =
                new AddReviewPresenter(viewManagerModel, addReviewViewModel);

        final AddReviewInputBoundary addReviewInteractor =
                new AddReviewInteractor(addReviewOutputBoundary, reviewDataAccess, currentUser);

        AddReviewController addReviewController = new AddReviewController(addReviewInteractor);
        restaurantView.setAddReviewController(addReviewController);

        return this;
    }

    public AppBuilder addDisplayReviewUseCase(){
        final DisplayReviewsOutputBoundary DisplayReviewPresenter =
                new DisplayReviewsPresenter(displayReviewsViewModel);

        final DisplayReviewsInputBoundary displayReviewsInteractor =
                new DisplayReviewsInteractor(reviewDataAccess, DisplayReviewPresenter, userRepository);

        DisplayReviewsController displayReviewsController = new DisplayReviewsController(displayReviewsInteractor);
        restaurantView.setDisplayReviewController(displayReviewsController);

        return this;
    }


    public JFrame build() throws IOException {
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
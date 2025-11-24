package app;

import data_access.FirebaseUserAuth;
import data_access.FirestoreUserRepo;
import data_access.JsonRestaurantDataAccessObject;
import entity.RestaurantFactory;
import interface_adapter.ViewManagerModel;
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
import interface_adapter.register.RegisterController;
import interface_adapter.register.RegisterPresenter;
import interface_adapter.register.RegisterViewModel;
import use_case.IAuthGateway;
import use_case.IUserRepo;
import use_case.custom_login.CustomLoginInputBoundary;
import use_case.custom_login.CustomLoginUserInteractor;
import use_case.custom_register.RegisterInputBoundary;
import use_case.custom_register.RegisterUserInteractor;
import use_case.filter.FilterInputBoundary;
import use_case.filter.FilterInteractor;
import use_case.google_login.GoogleLoginInputBoundary;
import use_case.google_login.GoogleLoginInteractor;
import use_case.logout.LogoutInputBoundary;
import use_case.logout.LogoutUserInteractor;
import view.FilterView;
import view.LoginView;
import view.LoggedInView;
import view.RegisterView;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * The AppBuilder is responsible for constructing and wiring together
 * all the components of the application using dependency injection.
 */
public class AppBuilder {
    private final JPanel cardPanel;
    private final ViewManagerModel viewManagerModel;
    private final CardLayout cardLayout;

    // Shared data access objects
    private final IAuthGateway authGateway;
    private final IUserRepo userRepository;
    private JsonRestaurantDataAccessObject restaurantDataAccess;

    // View Models
    private LoginViewModel loginViewModel;
    private RegisterViewModel registerViewModel;
    private LoggedInViewModel loggedInViewModel;
    private FilterViewModel filterViewModel;

    // Shared Google Login Controller
    private GoogleLoginController googleLoginController;

    public AppBuilder(JPanel cardPanel, ViewManagerModel viewManagerModel, CardLayout cardLayout) {
        this.cardPanel = cardPanel;
        this.viewManagerModel = viewManagerModel;
        this.cardLayout = cardLayout;

        // Initialize shared data access objects
        this.authGateway = new FirebaseUserAuth();
        this.userRepository = new FirestoreUserRepo();

        // Initialize restaurant data access
        try {
            RestaurantFactory restaurantFactory = new RestaurantFactory();
            this.restaurantDataAccess = new JsonRestaurantDataAccessObject(
                    "src/main/java/data/restaurant.json",
                    restaurantFactory
            );
        } catch (IOException e) {
            System.err.println("Failed to load restaurant data: " + e.getMessage());
        }
    }

    /**
     * Adds the Login View to the application.
     */
    public void addLoginView() {
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
    }

    /**
     * Adds the Register View to the application.
     */
    public void addRegisterView() {
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
    }

    /**
     * Adds the Logged In View to the application.
     */
    public void addLoggedInView() {
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
                logoutPresenter
        );

        // Create Logout Controller
        LogoutController logoutController = new LogoutController(logoutInteractor);

        // Create View
        LoggedInView loggedInView = new LoggedInView(loggedInViewModel);
        loggedInView.setLogoutController(logoutController);
        loggedInView.setViewManagerModel(viewManagerModel);

        // Add to card panel
        cardPanel.add(loggedInView, loggedInView.getViewName());
    }

    /**
     * Adds the Filter View to the application.
     */
    public void addFilterView() {
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

        // Add to card panel
        cardPanel.add(filterView, filterView.getViewName());
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
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
import interface_adapter.view_restaurant.ViewRestaurantViewModel;
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
import use_case.list_search.ListSearchInputBoundary;
import use_case.logout.LogoutInputBoundary;
import use_case.logout.LogoutUserInteractor;
import use_case.list_search.ListSearchInteractor;
import use_case.filter.IRestaurantDataAccess;
import interface_adapter.list_search.ListSearchController;
import interface_adapter.list_search.ListSearchPresenter;
import interface_adapter.list_search.ListSearchViewModel;
import view.*; // NEW: Import all views from 'view' package, including RestaurantPanel and RestaurantListView

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

    // Account
    private LoginViewModel loginViewModel;
    private RegisterViewModel registerViewModel;
    private LoggedInViewModel loggedInViewModel;
    private ListSearchViewModel listSearchViewModel;

    // Filter
    private FilterViewModel filterViewModel;

    // Restaurant info
    private ViewRestaurantViewModel restaurantViewModel;
    private RestaurantView restaurantView;


    // Shared data access objects
    private final IAuthGateway authGateway = new FirebaseUserAuth();
    private final IUserRepo userRepository = new FirestoreUserRepo();
    private IRestaurantDataAccess restaurantDataAccess;


    // Shared Google Login Controller
    private GoogleLoginController googleLoginController;

    public AppBuilder() {
        cardPanel.setLayout(cardLayout);

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

    public AppBuilder addLoginView() {
        loginViewModel = new LoginViewModel();
        if (loggedInViewModel == null) {
            loggedInViewModel = new LoggedInViewModel();
        }

        LoginPresenter loginPresenter = new LoginPresenter(
                viewManagerModel,
                loggedInViewModel,
                loginViewModel
        );

        CustomLoginInputBoundary loginInteractor = new CustomLoginUserInteractor(
                authGateway,
                userRepository,
                loginPresenter
        );

        LoginController loginController = new LoginController(
                loginInteractor,
                viewManagerModel,
                "register"
        );

        if (googleLoginController == null) {
            createGoogleLoginController();
        }

        LoginView loginView = new LoginView(loginViewModel);
        loginView.setLoginController(loginController);
        loginView.setGoogleLoginController(googleLoginController);

        cardPanel.add(loginView, loginView.getViewName());

        return this;
    }

    public AppBuilder addRegisterView() {
        registerViewModel = new RegisterViewModel();
        if (loginViewModel == null) {
            loginViewModel = new LoginViewModel();
        }

        RegisterPresenter registerPresenter = new RegisterPresenter(
                registerViewModel,
                loginViewModel,
                viewManagerModel
        );

        RegisterInputBoundary registerInteractor = new RegisterUserInteractor(
                authGateway,
                userRepository,
                registerPresenter
        );

        RegisterController registerController = new RegisterController(
                registerInteractor,
                viewManagerModel,
                "login"
        );

        if (googleLoginController == null) {
            createGoogleLoginController();
        }

        RegisterView registerView = new RegisterView(registerViewModel);
        registerView.setRegisterController(registerController);
        registerView.setGoogleLoginController(googleLoginController);

        cardPanel.add(registerView, registerView.getViewName());

        return this;
    }

    /**
     * Adds the Logged In View to the application.
     *
     * @return
     */
    public AppBuilder addLoggedInView() {
        if (loggedInViewModel == null) {
            loggedInViewModel = new LoggedInViewModel();
        }
        listSearchViewModel = new ListSearchViewModel();

        LogoutPresenter logoutPresenter = new LogoutPresenter(
                loginViewModel,
                viewManagerModel,
                loggedInViewModel
        );

        LogoutInputBoundary logoutInteractor = new LogoutUserInteractor(
                authGateway,
                logoutPresenter
        );

        LogoutController logoutController = new LogoutController(logoutInteractor);

        // CHANGED: HeartClickListener now implements RestaurantPanel.HeartClickListener directly
        RestaurantPanel.HeartClickListener heartListener = (restaurantId, newState) -> {
            System.out.println("Heart toggled for: " + restaurantId + " → " + newState);
        };

        if (filterViewModel == null) {
            filterViewModel = new FilterViewModel();
        }

        // UPDATED: LoggedInView constructor now expects RestaurantPanel.HeartClickListener
        LoggedInView loggedInView = new LoggedInView(
                loggedInViewModel,
                listSearchViewModel,
                heartListener,
                FilterView.VIEW_NAME
        );
        loggedInView.setLogoutController(logoutController);
        loggedInView.setViewManagerModel(viewManagerModel);

        ListSearchPresenter listSearchPresenter = new ListSearchPresenter(listSearchViewModel);
        ListSearchInputBoundary listSearchInteractor = new ListSearchInteractor(restaurantDataAccess, listSearchPresenter);
        ListSearchController listSearchController = new ListSearchController(listSearchInteractor);

        loggedInView.setSearchController(listSearchController);
        listSearchController.search("");

        cardPanel.add(loggedInView, LoggedInView.VIEW_NAME);

        return this;
    }

    /**
     * Adds the Filter View to the application.
     *
     * @return
     */
    public AppBuilder addFilterView() {
        if (filterViewModel == null) {
            filterViewModel = new FilterViewModel();
        }

        FilterPresenter filterPresenter = new FilterPresenter(filterViewModel);

        FilterInputBoundary filterInteractor = new FilterInteractor(
                restaurantDataAccess,
                filterPresenter
        );

        FilterController filterController = new FilterController(filterInteractor);

        FilterView filterView = new FilterView(filterViewModel);
        filterView.setFilterController(filterController);

        cardPanel.add(filterView, FilterView.VIEW_NAME);

        return this;
    }

    public AppBuilder addRestaurantView(){
        restaurantViewModel = new ViewRestaurantViewModel();
        restaurantView = new RestaurantView(restaurantViewModel);
        cardPanel.add(restaurantView, restaurantView.getViewName());
        return this;
    }

    public JFrame build(){
        final JFrame application = new JFrame("UofT Eats - Restaurant Review App");
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        application.setSize(600, 500);

        application.add(cardPanel);

        viewManagerModel.setState(loginViewModel.getViewName());
        viewManagerModel.firePropertyChange();

        return application;
    }

    private void createGoogleLoginController() {
        if (loginViewModel == null) {
            loginViewModel = new LoginViewModel();
        }
        if (registerViewModel == null) {
            registerViewModel = new RegisterViewModel();
        }
        if (loggedInViewModel == null) {
            loggedInViewModel = new LoggedInViewModel();
        }

        GoogleLoginPresenter googleLoginPresenter = new GoogleLoginPresenter(
                viewManagerModel,
                loggedInViewModel,
                loginViewModel,
                registerViewModel
        );

        GoogleLoginInputBoundary googleLoginInteractor = new GoogleLoginInteractor(
                authGateway,
                userRepository,
                googleLoginPresenter
        );

        googleLoginController = new GoogleLoginController(googleLoginInteractor);
    }
}
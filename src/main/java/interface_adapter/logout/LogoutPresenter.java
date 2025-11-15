package interface_adapter.logout;

import interface_adapter.ViewManagerModel;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.logged_in.LoggedInState;
import interface_adapter.login.LoginState;
import interface_adapter.login.LoginViewModel;
import use_case.logout.LogoutOutputBoundary;
import use_case.logout.LogoutOutputData;

/**
 * Presenter for the Logout Use Case.
 * Clears the user session state and switches back to the login screen.
 */
public class LogoutPresenter implements LogoutOutputBoundary {

    private final LoginViewModel loginViewModel;
    private final ViewManagerModel viewManagerModel;
    private final LoggedInViewModel loggedInViewModel;

    public LogoutPresenter(LoginViewModel loginViewModel,
                           ViewManagerModel viewManagerModel,
                           LoggedInViewModel loggedInViewModel) {
        this.loginViewModel = loginViewModel;
        this.viewManagerModel = viewManagerModel;
        this.loggedInViewModel = loggedInViewModel;
    }

    @Override
    public void prepareSuccessView(LogoutOutputData outputData) {
        // 1. Clear the logged-in state
        final LoggedInState loggedInState = loggedInViewModel.getState();
        loggedInState.setUid("");
        loggedInState.setNickname("");
        loggedInState.setEmail("");
        this.loggedInViewModel.setState(loggedInState);
        this.loggedInViewModel.firePropertyChange();

        // 2. Clear any login errors
        final LoginState loginState = loginViewModel.getState();
        loginState.setLoginError(null);
        loginState.setEmail(""); // Clear the email field
        loginState.setPassword(""); // Clear the password field
        this.loginViewModel.setState(loginState);
        this.loginViewModel.firePropertyChange();

        // 3. Switch view back to login screen
        this.viewManagerModel.setState(loginViewModel.getViewName());
        this.viewManagerModel.firePropertyChange();
    }
}
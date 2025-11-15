package interface_adapter.login;

import interface_adapter.ViewManagerModel;
import interface_adapter.logged_in.LoggedInState;
import interface_adapter.logged_in.LoggedInViewModel;
import use_case.custom_login.CustomLoginOutputBoundary;
import use_case.custom_login.CustomLoginOutputData;

/**
 * Presenter for Custom (Email/Password) Login ONLY.
 */
public class LoginPresenter implements CustomLoginOutputBoundary {

    private final LoginViewModel loginViewModel;
    private final LoggedInViewModel loggedInViewModel;
    private final ViewManagerModel viewManagerModel;

    public LoginPresenter(ViewManagerModel viewManagerModel,
                          LoggedInViewModel loggedInViewModel,
                          LoginViewModel loginViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.loggedInViewModel = loggedInViewModel;
        this.loginViewModel = loginViewModel;
    }

    @Override
    public void prepareSuccessView(CustomLoginOutputData outputData) {
        // Update logged-in state
        final LoggedInState loggedInState = loggedInViewModel.getState();
        loggedInState.setNickname(outputData.getNickname());
        loggedInState.setUid(outputData.getUid());
        this.loggedInViewModel.setState(loggedInState);
        this.loggedInViewModel.firePropertyChange();

        // Clear login errors
        final LoginState loginState = loginViewModel.getState();
        loginState.setLoginError(null);
        this.loginViewModel.setState(loginState);

        // Switch to logged-in view
        this.viewManagerModel.setState(loggedInViewModel.getViewName());
        this.viewManagerModel.firePropertyChange();
    }

    @Override
    public void prepareFailView(String error) {
        LoginState loginState = loginViewModel.getState();
        loginState.setLoginError(error);
        loginViewModel.setState(loginState);
        loginViewModel.firePropertyChange();
    }
}
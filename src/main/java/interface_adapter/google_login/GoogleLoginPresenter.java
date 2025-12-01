package interface_adapter.google_login;

import interface_adapter.ViewManagerModel;
import interface_adapter.logged_in.LoggedInState;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.login.LoginState;
import interface_adapter.login.LoginViewModel;
import interface_adapter.register.RegisterState;
import interface_adapter.register.RegisterViewModel;
import use_case.google_login.GoogleLoginOutputBoundary;
import use_case.google_login.GoogleLoginOutputData;

/**
 * Presenter for Google Login use case. Since Google login can be triggered from either login or
 * register view, this presenter can update error states in both views.
 */
public class GoogleLoginPresenter implements GoogleLoginOutputBoundary {

  private final ViewManagerModel viewManagerModel;
  private final LoggedInViewModel loggedInViewModel;
  private final LoginViewModel loginViewModel;
  private final RegisterViewModel registerViewModel;

  public GoogleLoginPresenter(ViewManagerModel viewManagerModel,
      LoggedInViewModel loggedInViewModel,
      LoginViewModel loginViewModel,
      RegisterViewModel registerViewModel) {
    this.viewManagerModel = viewManagerModel;
    this.loggedInViewModel = loggedInViewModel;
    this.loginViewModel = loginViewModel;
    this.registerViewModel = registerViewModel;
  }

  @Override
  public void prepareSuccessView(GoogleLoginOutputData outputData) {
    // Update logged-in state
    final LoggedInState loggedInState = loggedInViewModel.getState();
    loggedInState.setNickname(outputData.getNickname());
    loggedInState.setUid(outputData.getUid());
    this.loggedInViewModel.setState(loggedInState);
    this.loggedInViewModel.firePropertyChange();

    // Clear any errors from both login and register views
    final LoginState loginState = loginViewModel.getState();
    loginState.setLoginError(null);
    this.loginViewModel.setState(loginState);

    final RegisterState registerState = registerViewModel.getState();
    registerState.setGeneralError(null);
    this.registerViewModel.setState(registerState);

    // Switch to logged-in view
    this.viewManagerModel.setState(loggedInViewModel.getViewName());
    this.viewManagerModel.firePropertyChange();
  }

  @Override
  public void prepareFailView(String errorMessage) {
    // Determine which view is currently active and update its error state
    String currentView = viewManagerModel.getState();

    if ("login".equals(currentView)) {
      LoginState loginState = loginViewModel.getState();
      loginState.setLoginError("Google Sign-In failed: " + errorMessage);
      loginViewModel.setState(loginState);
      loginViewModel.firePropertyChange();
    } else if ("register".equals(currentView)) {
      RegisterState registerState = registerViewModel.getState();
      registerState.setGeneralError("Google Sign-In failed: " + errorMessage);
      registerViewModel.setState(registerState);
      registerViewModel.firePropertyChange();
    }
  }
}
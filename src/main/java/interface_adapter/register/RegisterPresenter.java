package interface_adapter.register;

import interface_adapter.ViewManagerModel;
import interface_adapter.login.LoginViewModel;
import use_case.custom_register.RegisterOutputBoundary;
import use_case.custom_register.RegisterOutputData;

/**
 * The Presenter for the Register Use Case.
 */
public class RegisterPresenter implements RegisterOutputBoundary {

  private final RegisterViewModel registerViewModel;
  private final LoginViewModel loginViewModel;
  private final ViewManagerModel viewManagerModel;

  public RegisterPresenter(RegisterViewModel registerViewModel,
      LoginViewModel loginViewModel,
      ViewManagerModel viewManagerModel) {
    this.registerViewModel = registerViewModel;
    this.loginViewModel = loginViewModel;
    this.viewManagerModel = viewManagerModel;
  }

  @Override
  public void prepareSuccessView(RegisterOutputData response) {
    // Clear any errors
    RegisterState registerState = registerViewModel.getState();
    registerState.setEmailError(null);
    registerState.setPasswordError(null);
    registerState.setRepeatPasswordError(null);
    registerState.setGeneralError(null);
    registerViewModel.setState(registerState);
    registerViewModel.firePropertyChange();

    // Switch to login view (user can now log in)
    viewManagerModel.setState(loginViewModel.getViewName());
    viewManagerModel.firePropertyChange();
  }

  @Override
  public void prepareFailView(String errorMessage) {
    RegisterState state = registerViewModel.getState();

    // Clear field-specific errors
    state.setEmailError(null);
    state.setPasswordError(null);
    state.setRepeatPasswordError(null);

    // Set general error message
    state.setGeneralError(errorMessage);

    registerViewModel.setState(state);
    registerViewModel.firePropertyChange();
  }

  @Override
  public void switchToLoginView() {
    viewManagerModel.setState(loginViewModel.getViewName());
    viewManagerModel.firePropertyChange();
  }
}
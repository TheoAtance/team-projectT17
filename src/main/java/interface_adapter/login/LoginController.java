package interface_adapter.login;

import interface_adapter.ViewManagerModel;
import use_case.custom_login.CustomLoginInputBoundary;
import use_case.custom_login.CustomLoginInputData;

/**
 * Controller for Custom (Email/Password) Login ONLY.
 */
public class LoginController {

  private final CustomLoginInputBoundary loginUseCaseInteractor;
  private final ViewManagerModel viewManagerModel;
  private final String registerViewName;

  public LoginController(
      CustomLoginInputBoundary loginUseCaseInteractor,
      ViewManagerModel viewManagerModel,
      String registerViewName) {
    this.loginUseCaseInteractor = loginUseCaseInteractor;
    this.viewManagerModel = viewManagerModel;
    this.registerViewName = registerViewName;
  }

  public void execute(String email, String password) {
    CustomLoginInputData loginInputData = new CustomLoginInputData(email, password);
    loginUseCaseInteractor.execute(loginInputData);
  }

  /**
   * Switches from Login view to Register view.
   */
  public void switchToRegisterView() {
    viewManagerModel.setState(registerViewName);
    viewManagerModel.firePropertyChange();
  }
}
package interface_adapter.register;

import interface_adapter.ViewManagerModel;
import use_case.custom_register.RegisterInputBoundary;
import use_case.custom_register.RegisterInputData;

/**
 * Controller for Custom Registration ONLY.
 */
public class RegisterController {

  private final RegisterInputBoundary customRegisterUseCaseInteractor;
  private final ViewManagerModel viewManagerModel;
  private final String loginViewName;

  public RegisterController(
      RegisterInputBoundary customRegisterUserInteractor,
      ViewManagerModel viewManagerModel,
      String loginViewName) {
    this.customRegisterUseCaseInteractor = customRegisterUserInteractor;
    this.viewManagerModel = viewManagerModel;
    this.loginViewName = loginViewName;
  }

  /**
   * Switches from Register view to Login view.
   */
  public void switchToLoginView() {
    viewManagerModel.setState(loginViewName);
    viewManagerModel.firePropertyChange();
  }

  public void execute(String email, String nickname, String password, String repeatPassword) {
    RegisterInputData registerInputData = new RegisterInputData(
        email,
        password,
        repeatPassword,
        nickname
    );
    customRegisterUseCaseInteractor.execute(registerInputData);
  }
}
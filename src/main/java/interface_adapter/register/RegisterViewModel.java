package interface_adapter.register;

import interface_adapter.ViewModel;

/**
 * The ViewModel for the Registration View.
 */
public class RegisterViewModel extends ViewModel<RegisterState> {

  public static final String TITLE_LABEL = "Register New Account";
  public static final String EMAIL_LABEL = "Email";
  public static final String NICKNAME_LABEL = "Nickname";
  public static final String PASSWORD_LABEL = "Password";
  public static final String REPEAT_PASSWORD_LABEL = "Repeat Password";
  public static final String REGISTER_BUTTON_LABEL = "Sign Up";
  public static final String GOOGLE_BUTTON_LABEL = "Continue with Google";
  public static final String TO_LOGIN_BUTTON_LABEL = "Go to Login";

  public RegisterViewModel() {
    super("register");
    setState(new RegisterState());
  }
}
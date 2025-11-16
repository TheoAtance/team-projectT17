package interface_adapter.login;

import interface_adapter.ViewModel;

/**
 * The ViewModel for the Login View.
 */
public class LoginViewModel extends ViewModel<LoginState> {

    public static final String TITLE_LABEL = "Login View";
    public static final String EMAIL_LABEL = "Email";
    public static final String PASSWORD_LABEL = "Password";
    public static final String LOGIN_BUTTON_LABEL = "Log In";
    public static final String GOOGLE_BUTTON_LABEL = "Continue with Google";
    public static final String TO_REGISTER_BUTTON_LABEL = "Go to Sign Up";

    public LoginViewModel() {
        super("login");
        setState(new LoginState());
    }
}
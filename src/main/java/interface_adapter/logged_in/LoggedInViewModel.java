package interface_adapter.logged_in;

import interface_adapter.ViewModel;

/**
 * The ViewModel for the Logged In View.
 */
public class LoggedInViewModel extends ViewModel<LoggedInState> {

    public static final String TITLE_LABEL = "Welcome";

    public LoggedInViewModel() {
        super("logged in");
        setState(new LoggedInState());
    }
}
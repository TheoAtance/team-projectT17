package interface_adapter.login;

/**
 * The state object for the Login View.
 */
public class LoginState {
    private String email = "";
    private String password = "";
    private String loginError = null;

    // Copy constructor
    public LoginState(LoginState copy) {
        this.email = copy.email;
        this.password = copy.password;
        this.loginError = copy.loginError;
    }

    public LoginState() {
    }

    // Getters and Setters

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLoginError() {
        return loginError;
    }

    public void setLoginError(String loginError) {
        this.loginError = loginError;
    }
}
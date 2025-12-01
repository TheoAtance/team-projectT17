package interface_adapter.register;

/**
 * The state object for the Register View.
 */
public class RegisterState {

  private String email = "";
  private String nickname = "";
  private String password = "";
  private String repeatPassword = "";

  private String generalError = null;
  private String emailError = null;
  private String nicknameError = null;
  private String passwordError = null;
  private String repeatPasswordError = null;

  // Copy constructor
  public RegisterState(RegisterState copy) {
    this.email = copy.email;
    this.nickname = copy.nickname;
    this.password = copy.password;
    this.repeatPassword = copy.repeatPassword;
    this.generalError = copy.generalError;
    this.emailError = copy.emailError;
    this.nicknameError = copy.nicknameError;
    this.passwordError = copy.passwordError;
    this.repeatPasswordError = copy.repeatPasswordError;
  }

  public RegisterState() {
  }

  // --- Getters ---

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getRepeatPassword() {
    return repeatPassword;
  }

  public void setRepeatPassword(String repeatPassword) {
    this.repeatPassword = repeatPassword;
  }

  public String getGeneralError() {
    return generalError;
  }

  // --- Setters ---

  public void setGeneralError(String generalError) {
    this.generalError = generalError;
  }

  public String getEmailError() {
    return emailError;
  }

  public void setEmailError(String emailError) {
    this.emailError = emailError;
  }

  public String getNicknameError() {
    return nicknameError;
  }

  public void setNicknameError(String nicknameError) {
    this.nicknameError = nicknameError;
  }

  public String getPasswordError() {
    return passwordError;
  }

  public void setPasswordError(String passwordError) {
    this.passwordError = passwordError;
  }

  public String getRepeatPasswordError() {
    return repeatPasswordError;
  }

  public void setRepeatPasswordError(String repeatPasswordError) {
    this.repeatPasswordError = repeatPasswordError;
  }
}
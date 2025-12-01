package use_case.custom_register;

/**
 * Input Data for the Register Use Case.
 */
public class RegisterInputData {

  private final String email;
  private final String password;
  private final String repeatPassword;
  private final String nickname;

  public RegisterInputData(String email, String password, String repeatPassword, String nickname) {
    this.email = email;
    this.password = password;
    this.repeatPassword = repeatPassword;
    this.nickname = nickname;
  }

  public String getEmail() {
    return email;
  }

  public String getPassword() {
    return password;
  }

  public String getRepeatPassword() {
    return repeatPassword;
  }

  public String getNickname() {
    return nickname;
  }
}
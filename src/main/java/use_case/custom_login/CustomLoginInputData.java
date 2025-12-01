package use_case.custom_login;

/**
 * The Input Data for the Login Use Case.
 */
public class CustomLoginInputData {

  private final String email;
  private final String password;

  public CustomLoginInputData(String email, String password) {
    this.email = email;
    this.password = password;
  }

  public String getEmail() {
    return email;
  }

  public String getPassword() {
    return password;
  }
}
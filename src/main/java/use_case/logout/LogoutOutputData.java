package use_case.logout;

/**
 * Output Data for the Logout Use Case.
 */
public class LogoutOutputData {

  private final String message;

  public LogoutOutputData(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
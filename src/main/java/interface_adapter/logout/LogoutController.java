package interface_adapter.logout;

import use_case.logout.LogoutInputBoundary;

/**
 * Controller for the Logout Use Case.
 */
public class LogoutController {

  private final LogoutInputBoundary logoutInteractor;

  public LogoutController(LogoutInputBoundary logoutInteractor) {
    this.logoutInteractor = logoutInteractor;
  }

  /**
   * Executes the logout process.
   */
  public void execute() {
    logoutInteractor.execute();
  }
}
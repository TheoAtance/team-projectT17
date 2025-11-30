package use_case.google_login;

/**
 * The Input Boundary for the Google Login/Registration Use Case. Called by the Controller to
 * initiate the OAuth flow.
 */
public interface GoogleLoginInputBoundary {

  /**
   * Initiates the Google OAuth process. The actual input data is handled by the IAuthGateway (which
   * shows the pop-up/redirects).
   */
  void execute();
}
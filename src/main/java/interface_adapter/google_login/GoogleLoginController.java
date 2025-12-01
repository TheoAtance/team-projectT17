package interface_adapter.google_login;

import use_case.google_login.GoogleLoginInputBoundary;

/**
 * Controller for Google Login use case. Note: Google login can be triggered from either
 * RegisterView or LoginView, so this controller is shared/reused by both.
 */
public class GoogleLoginController {

  private final GoogleLoginInputBoundary googleLoginInteractor;

  public GoogleLoginController(GoogleLoginInputBoundary googleLoginInteractor) {
    this.googleLoginInteractor = googleLoginInteractor;
  }

  /**
   * Executes the Google OAuth login/registration flow.
   */
  public void execute() {
    googleLoginInteractor.execute();
  }
}
package use_case.custom_login;

/**
 * Output Boundary interface for the Login Use Case. The Interactor calls this, and the Presenter
 * implements it.
 */
public interface CustomLoginOutputBoundary {

  /**
   * Called by the Interactor when the login process is successful.
   *
   * @param outputData The data transfer object containing the user's details for display.
   */
  void prepareSuccessView(CustomLoginOutputData outputData);

  /**
   * Called by the Interactor when the login process fails.
   *
   * @param errorMessage The description of the error (such as "Invalid password").
   */
  void prepareFailView(String errorMessage);
}
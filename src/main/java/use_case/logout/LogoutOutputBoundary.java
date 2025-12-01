package use_case.logout;

/**
 * Output Boundary for the Logout Use Case.
 */
public interface LogoutOutputBoundary {

  /**
   * Prepares the success view for logout.
   *
   * @param outputData The logout output data.
   */
  void prepareSuccessView(LogoutOutputData outputData);
}
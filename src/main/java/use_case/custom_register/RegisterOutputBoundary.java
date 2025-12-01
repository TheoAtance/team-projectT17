package use_case.custom_register;

/**
 * The Output Boundary interface for the Register Use Case.
 */
public interface RegisterOutputBoundary {

  /**
   * Called when registration is successful.
   *
   * @param outputData The data transfer object containing success information.
   */
  void prepareSuccessView(RegisterOutputData outputData);

  /**
   * Called when registration fails.
   *
   * @param errorMessage The detailed message explaining the failure reason.
   */
  void prepareFailView(String errorMessage);

  void switchToLoginView();
}
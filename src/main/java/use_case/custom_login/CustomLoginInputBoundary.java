package use_case.custom_login;

/**
 * Input Boundary interface for the Login Use Case. The Controller calls this method, and the
 * Interactor implements it.
 */
public interface CustomLoginInputBoundary {

  /**
   * Executes the login process using the provided input data.
   *
   * @param customLoginInputData The data transfer object containing the nickname and password.
   */
  void execute(CustomLoginInputData customLoginInputData);
}
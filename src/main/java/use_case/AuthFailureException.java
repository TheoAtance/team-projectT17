package use_case;

/**
 * Custom Exception for when an authentication attempt fails. This can be used for both login and
 * registration failures.
 */
public class AuthFailureException extends RuntimeException {

  /**
   * Constructs a new AuthFailureException with the specified detail message.
   *
   * @param message the detail message (e.g., "Invalid password", "Email already in use").
   */
  public AuthFailureException(String message) {
    super(message);
  }
}
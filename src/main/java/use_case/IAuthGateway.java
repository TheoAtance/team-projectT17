package use_case;

import java.io.IOException;

/**
 * The gateway for Firebase Auth. The methods below (apart from logout) should return the User's
 * uid.
 */
public interface IAuthGateway {

  /**
   * Attempts to sign in a user with an email and password.
   *
   * @throws AuthFailureException if authentication fails (e.g., invalid credentials).
   */
  String loginWithEmailAndPassword(String email, String password) throws AuthFailureException;

  /**
   * Registers a new user with an email and password.
   *
   * @throws AuthFailureException if registration fails (e.g., email already in use, weak
   *                              password).
   */
  String registerWithEmailAndPassword(String email, String password) throws AuthFailureException;

  /**
   * Initiates and handles the Google OAuth sign-in flow.
   *
   * @return GoogleAuthResult containing uid, email, and display name
   * @throws AuthFailureException if the OAuth flow is cancelled or fails
   */
  GoogleAuthResult loginWithGoogle() throws AuthFailureException, IOException;

  /**
   * Clears the current user session (signs out the user).
   */
  void logout();


  /**
   * Get the current user's uid
   *
   * @return current user's uid
   */
  String getCurrentUserUid();

}
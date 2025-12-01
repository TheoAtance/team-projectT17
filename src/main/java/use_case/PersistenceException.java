package use_case;

/**
 * Custom Exception for when an operation on the persistence layer (Firestore/database) fails. This
 * is used for save, update, or unexpected retrieval failures.
 */
public class PersistenceException extends RuntimeException {

  /**
   * Constructs a new PersistenceException with the specified detail message.
   *
   * @param message the detail message (e.g., "Could not connect to database", "User data
   *                corrupted").
   */
  public PersistenceException(String message) {
    super(message);
  }
}
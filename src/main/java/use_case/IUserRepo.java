package use_case;

import entity.User;
import java.util.Optional;

/**
 * The Repository for accessing and persisting the User Entity in the database (Firebase Firestore).
 */
public interface IUserRepo {

    /**
     * Attempts to find a User Entity by their unique Firebase ID.
     * @param uid The unique Firebase ID to check.
     * @return An Optional containing the User entity if found, or an empty Optional otherwise.
     * @throws PersistenceException if an unexpected database error occurs during retrieval.
     */
    Optional<User> getUserByUid(String uid) throws PersistenceException;

    /**
     * Saves a new User Entity to the database. This is used immediately after registration.
     * @param user The User Entity to be saved.
     * @throws PersistenceException if the save operation fails.
     */
    void save(User user) throws PersistenceException;

    /**
     * Updates an existing User Entity's data in the database (e.g., changing favorites).
     * @param user The User Entity with updated fields.
     * @throws PersistenceException if the update operation fails.
     */
    void update(User user) throws PersistenceException;

    /**
     * Checks if a User Entity with the given UID already exists in the Firestore database.
     * This will be useful for determining if a Google sign-in is a "login" or a "first-time registration."
     * @param uid The unique Firebase ID to check.
     * @return true if the user exists in Firestore, false otherwise.
     */
    boolean existsByUid(String uid);
}

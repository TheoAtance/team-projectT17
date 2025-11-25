package data_access;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import entity.User;
import entity.UserFactory;
import use_case.IUserRepo;
import use_case.PersistenceException;

import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Firestore implementation of the IUserRepo interface.
 * This class handles all database operations for User entities using Firebase Firestore.
 *
 * Firestore is a NoSQL document database where data is stored in collections of documents.
 * Each User is stored as a document in the "users" collection, with the Firebase Auth UID
 * as the document ID for efficient O(1) lookups.
 *
 * All operations are asynchronous but use ApiFuture.get() to block until completion,
 * simplifying the programming model at the cost of blocking the calling thread.
 *
 * @see IUserRepo
 * @see User
 * @see PersistenceException
 */
public class FirestoreUserRepo implements IUserRepo {

    private final Firestore db;
    private final Map<String, User> usersById = new HashMap<>();

    /** Flag to ensure the cache is loaded only once. */
    private volatile boolean isCacheLoaded = false;

    /**
     * The name of the Firestore collection where user documents are stored.
     * Collection structure: users/{uid}/
     */
    private static final String COLLECTION_NAME = "users";

    /**
     * Constructs a new FirestoreUserRepo and initializes the Firestore database connection.
     * The Firestore instance is obtained from the FirebaseService singleton.
     */
    public FirestoreUserRepo() {
        this.db = FirebaseService.getInstance().getFirestore();
    }

    /**
     * Converts a Firestore DocumentSnapshot into a User entity.
     *
     * This method performs type-safe extraction of data from the Firestore document,
     * handling potential type mismatches and missing fields gracefully. It uses the
     * UserFactory to construct the User entity with proper validation.
     *
     * Firestore Document Structure:
     *
     * users/{uid}/
     *   ├── email: string
     *   ├── nickname: string
     *   ├── language: string
     *   └── favoriteRestaurantIds: array&lt;string&gt;
     *
     *
     * @param document The Firestore DocumentSnapshot to convert
     * @return A User entity constructed from the document data, or null if document doesn't exist
     * @throws PersistenceException if the document data is malformed or cannot be parsed
     */
    private User documentToUser(DocumentSnapshot document) throws PersistenceException {
        if (!document.exists()) {
            return null;
        }

        try {
            // Extract document ID as the UID (primary key)
            String uid = document.getId();

            // Extract string fields with Firestore's type-safe getters
            String email = document.getString("email");
            String nickname = document.getString("nickname");
            String language = document.getString("language");

            // Safely extract and convert the favoriteRestaurantIds array
            // Firestore returns Object for unknown types, so we must check instanceof
            Object favoritesObj = document.get("favoriteRestaurantIds");
            List<String> favoriteRestaurantIds = new ArrayList<>();

            if (favoritesObj instanceof List<?>) {
                List<?> rawList = (List<?>) favoritesObj;
                // Validate each item is a String before adding
                for (Object item : rawList) {
                    if (item instanceof String) {
                        favoriteRestaurantIds.add((String) item);
                    }
                }
            }

            // Use UserFactory to construct User with validation and defaults
            UserFactory userFactory = new UserFactory();
            return userFactory.createExistingUser(uid, email, nickname, language, favoriteRestaurantIds);

        } catch (Exception e) {
            throw new PersistenceException("Failed to parse user document: " + e.getMessage());
        }
    }

    /**
     * Retrieves a User entity from Firestore by their unique Firebase Authentication UID.
     *
     * This method performs a direct document lookup using the UID as the document ID,
     * resulting in O(1) time complexity. The operation is asynchronous but blocks until
     * the document is retrieved from Firestore.
     *
     * <p>Usage Example:</p>
     *
     * Optional&lt;User&gt; user = userRepo.getUserByUid("tRcfmLH7o1dYrUkm9");
     * if (user.isPresent()) {
     *     System.out.println("Found user: " + user.get().getNickname());
     * } else {
     *     System.out.println("User not found");
     * }
     *
     *
     * @param uid The unique Firebase Authentication ID of the user
     * @return An Optional containing the User if found, or Optional.empty() if not found
     * @throws PersistenceException if a database error occurs during retrieval
     */
    @Override
    public User getUserByUid(String uid) throws PersistenceException {
        if(!isCacheLoaded){
            loadAllUsers();
        }

        User user = usersById.get(uid);

        return user;
    }



    /**
     * Saves a User entity to Firestore, creating a new document or overwriting an existing one.
     *
     * <p>This method converts the User entity into a Map&lt;String, Object&gt; that Firestore
     * can store, then performs a set() operation which acts as an "upsert" (insert or update).
     * The operation is asynchronous but blocks until the write is confirmed by Firestore.</p>
     *
     * <p>Document ID Strategy:</p>
     * <ul>
     *   <li>Uses the User's UID (from Firebase Auth) as the document ID</li>
     *   <li>This ensures O(1) lookups by UID</li>
     *   <li>Prevents duplicate users (UID is unique)</li>
     * </ul>
     *
     * <p>Usage Example:</p>
     * <pre>
     * User newUser = new User("uid123", "john@example.com", "Johnny");
     * userRepo.save(newUser);
     * // User document created in Firestore at: users/uid123/
     * </pre>
     *
     * @param user The User entity to save
     * @throws PersistenceException if the save operation fails
     */
    @Override
    public void save(User user) throws PersistenceException {
        try {
            // Get reference to document with ID = user's UID
            DocumentReference docRef = db.collection(COLLECTION_NAME).document(user.getUid());

            // Convert User entity to Firestore-compatible Map
            HashMap<String, Object> data = new HashMap<>();
            data.put("email", user.getEmail());
            data.put("nickname", user.getNickname());
            data.put("language", user.getLanguage());
            data.put("favoriteRestaurantIds", user.getFavoriteRestaurantIds());

            // Asynchronously write to Firestore (set = upsert: create or replace)
            ApiFuture<WriteResult> future = docRef.set(data);

            // Block until write is confirmed (future.get() returns WriteResult with timestamp)
            future.get();

            usersById.put(user.getUid(), user);

        } catch (InterruptedException | ExecutionException e) {
            // Wrap checked exceptions in domain-specific exception
            throw new PersistenceException("Failed to save user " + user.getUid() + ": " + e.getMessage());
        }
    }

    @Override
    public void loadAllUsers() throws PersistenceException{
        if(isCacheLoaded){
            return;
        }

        System.out.println("DEBUG: Loading all users from Firestore into cache");

        try {
            // gets a list containing all user document
            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();


            // convert each document into user objects
            for (QueryDocumentSnapshot document : documents) {
                try {
                    User user = documentToUser(document);
                    if (user != null) {
                        usersById.put(user.getUid(), user);
                    }
                } catch (PersistenceException e) {
                    System.err.println("Skipping malformed user document " + document.getId() + ": " + e.getMessage());
                }
            }

            isCacheLoaded = true; // Set flag after successful load
            System.out.println("DEBUG: Cache loaded with " + usersById.size() + " users.");

        } catch (InterruptedException | ExecutionException e) {
            throw new PersistenceException("Failed to load all users into cache: " + e.getMessage());
        }
    }

    /**
     * Updates an existing User entity in Firestore.
     *
     * <p>In Firestore, the set() operation with default options acts as an upsert
     * (insert or update), so this method simply delegates to save(). If you need
     * to update only specific fields without replacing the entire document, use
     * Firestore's update() method instead.</p>
     *
     * <p>Usage Example:</p>
     * <pre>
     * User user = userRepo.getUserByUid("uid123").get();
     * user.setNickname("Updated Name");
     * userRepo.update(user);
     * // Entire user document is replaced with new data
     * </pre>
     *
     * @param user The User entity with updated data
     * @throws PersistenceException if the update operation fails
     */
    @Override
    public void update(User user) throws PersistenceException {
        // Firestore set() with default options performs upsert (create or replace)
        // If you need to update only specific fields, use:
        // docRef.update("fieldName", newValue) instead
        this.save(user);
    }

    /**
     * Checks if a User document exists in Firestore for the given UID.
     *
     * <p>This method performs a document fetch and checks the exists() property.
     * It does NOT retrieve the document data, only checks for existence. However,
     * Firestore still charges a read operation for this check.</p>
     *
     * <p>This method is commonly used to determine if a Google OAuth user is
     * logging in for the first time (register) or returning (login).</p>
     *
     * <p>Usage Example:</p>
     * <pre>
     * if (userRepo.existsByUid(googleAuthResult.getUid())) {
     *     // Existing user - perform login
     * } else {
     *     // New user - perform registration
     * }
     * </pre>
     *
     * @param uid The unique Firebase Authentication ID to check
     * @return true if a user document exists, false otherwise
     */
    @Override
    public boolean existsByUid(String uid) {
        try {
            // Get reference to the document
            DocumentReference docRef = db.collection(COLLECTION_NAME).document(uid);

            // Asynchronously fetch the document
            ApiFuture<DocumentSnapshot> future = docRef.get();

            // Block until document is retrieved
            DocumentSnapshot document = future.get();

            // Check if document exists (doesn't load data, just checks existence)
            return document.exists();

        } catch (InterruptedException | ExecutionException e) {
            // Log error but don't throw - return false to indicate "doesn't exist"
            // This prevents registration failures due to network issues
            System.err.println("Error checking existence for user " + uid + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes a User document from Firestore.
     *
     * <p>This method permanently removes the user's document from the database.
     * Note that this does NOT delete the user's Firebase Authentication account -
     * that must be done separately through IAuthGateway.</p>
     *
     * <p>Warning: This operation is irreversible. The user's profile data,
     * including all favorites and preferences, will be permanently lost.</p>
     *
     * <p>Usage Example:</p>
     * <pre>
     * // To fully delete a user:
     * authGateway.deleteUser(uid);        // Delete auth account
     * userRepo.delete(uid);                // Delete profile data
     * </pre>
     *
     * @param uid The unique Firebase Authentication ID of the user to delete
     * @throws PersistenceException if the delete operation fails
     */
    public void delete(String uid) throws PersistenceException {
        try {
            // Get reference to document and delete it
            ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME).document(uid).delete();

            // Block until delete is confirmed
            future.get();

            usersById.remove(uid);

        } catch (InterruptedException | ExecutionException e) {
            // Wrap checked exceptions in domain-specific exception
            throw new PersistenceException("Failed to delete user " + uid + ": " + e.getMessage());
        }
    }
}
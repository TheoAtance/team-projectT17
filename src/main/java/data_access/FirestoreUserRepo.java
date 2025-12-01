package data_access;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import entity.User;
import entity.UserFactory;
import use_case.IUserRepo;
import use_case.PersistenceException;
import entity.Restaurant;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.JSONArray;
import org.json.JSONObject;

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
public class FirestoreUserRepo implements IUserRepo, UserDataAccessInterface {

    private final Firestore db;
    private final Map<String, User> usersById = new HashMap<>();

    /** Flag to ensure the cache is loaded only once. */
    private volatile boolean isCacheLoaded = false;

    /** Cache for restaurants loaded from JSON file. */
    private final Map<String, Restaurant> restaurantsById = new HashMap<>();

    /** Flag to ensure the restaurant cache is loaded only once. */
    private volatile boolean isRestaurantCacheLoaded = false;

    /** Path to the restaurant data JSON file. */
    private static final String RESTAURANT_JSON_PATH = "src/main/java/data/restaurant.json";

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
     * @param uid The unique Firebase Authentication ID of the user
     * @return User associated with the uid
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
            System.out.println("DEBUG: Saving user " + user.getUid() + " with favorites: " + user.getFavoriteRestaurantIds());

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

            System.out.println("DEBUG: User saved successfully. Cache updated with favorites: " + user.getFavoriteRestaurantIds());

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
                        System.out.println(user.getUid());
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

    // ========= UserDataAccessInterface Implementation =========

    @Override
    public User getUser(String userId) {
        try {
            return getUserByUid(userId);
        } catch (PersistenceException e) {
            System.err.println("Error getting user: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void saveUser(User user) {
        try {
            save(user);
        } catch (PersistenceException e) {
            System.err.println("Error saving user: " + e.getMessage());
        }
    }

    /**
     * Loads all restaurants from the restaurant.json file into memory cache.
     * This method is called lazily on first access to restaurant data.
     *
     * @throws RuntimeException if the JSON file cannot be read or parsed
     */
    private void loadRestaurantsFromJson() {
        if (isRestaurantCacheLoaded) {
            return;
        }

        System.out.println("DEBUG: Loading restaurants from " + RESTAURANT_JSON_PATH);

        try {
            // Read the entire JSON file as a string
            String jsonContent = new String(Files.readAllBytes(Paths.get(RESTAURANT_JSON_PATH)));

            // Parse as JSON array
            JSONArray restaurantsArray = new JSONArray(jsonContent);

            // Convert each JSON object to a Restaurant entity
            for (int i = 0; i < restaurantsArray.length(); i++) {
                JSONObject restaurantJson = restaurantsArray.getJSONObject(i);

                try {
                    // Extract CID from placeUri
                    String placeUri = restaurantJson.getJSONObject("googleMapsLinks").getString("placeUri");
                    String cid = extractCidFromPlaceUri(placeUri);

                    // Extract Google Places ID from first photo
                    String placesId = null;
                    if (restaurantJson.has("photos") && restaurantJson.getJSONArray("photos").length() > 0) {
                        JSONObject firstPhoto = restaurantJson.getJSONArray("photos").getJSONObject(0);
                        if (firstPhoto.has("name")) {
                            String photoName = firstPhoto.getString("name");
                            placesId = extractPlacesIdFromPhotoName(photoName);
                        }
                    }

                    Restaurant restaurant = jsonToRestaurant(restaurantJson);

                    // Store by CID
                    restaurantsById.put(cid, restaurant);

                    // ALSO store by Google Places ID if we found one
                    if (placesId != null) {
                        restaurantsById.put(placesId, restaurant);
                        System.out.println("DEBUG FirestoreUserRepo: Mapped both " + cid + " and " + placesId);
                    }

                } catch (Exception e) {
                    System.err.println("Skipping malformed restaurant at index " + i + ": " + e.getMessage());
                }
            }

            isRestaurantCacheLoaded = true;
            System.out.println("DEBUG: Restaurant cache loaded with " + restaurantsById.size() + " entries.");

        } catch (IOException e) {
            throw new RuntimeException("Failed to read restaurant.json: " + e.getMessage(), e);
        }
    }

    /**
     * Extracts the Google Places ID from a photo name.
     * Example: "places/ChIJs_Gr4rE0K4gR4PCci36eEkg/photos/AWn5..." -> "places/ChIJs_Gr4rE0K4gR4PCci36eEkg"
     */
    private String extractPlacesIdFromPhotoName(String photoName) {
        if (photoName != null && photoName.startsWith("places/")) {
            int photosIndex = photoName.indexOf("/photos/");
            if (photosIndex != -1) {
                return photoName.substring(0, photosIndex);
            }
        }
        return null;
    }

    /**
     * Converts a JSON object to a Restaurant entity using the Builder pattern.
     * Extracts all available data from the Google Places API JSON format.
     *
     * Expected JSON structure:
     * {
     *   "displayName": { "text": "Restaurant Name", "languageCode": "en" },
     *   "googleMapsLinks": {
     *     "placeUri": "https://maps.google.com/?cid=5193387586656989408&...",
     *     "directionsUri": "https://..."
     *   },
     *   "location": { "latitude": 43.6532, "longitude": -79.3832 },
     *   "formattedAddress": "123 Street Name, City, Province",
     *   "rating": 4.5,
     *   "userRatingCount": 150,
     *   "primaryTypeDisplayName": { "text": "Cafe", "languageCode": "en-US" },
     *   "nationalPhoneNumber": "+1 416-555-1234",
     *   "websiteUri": "https://example.com",
     *   "regularOpeningHours": { "weekdayDescriptions": ["Monday: 9:00 AM – 5:00 PM", ...] },
     *   "photos": [
     *     { "name": "places/.../photos/photo_id_1" }
     *   ]
     * }
     *
     * @param json The JSON object representing a restaurant
     * @return A Restaurant entity
     */
    private Restaurant jsonToRestaurant(JSONObject json) {
        Restaurant.Builder builder = new Restaurant.Builder();

        // Extract CID from placeUri (required)
        String placeUri = json.getJSONObject("googleMapsLinks").getString("placeUri");
        String cid = extractCidFromPlaceUri(placeUri);
        builder.id(cid);

        // Extract name (required)
        String name = json.getJSONObject("displayName").getString("text");
        builder.name(name);

        // Extract location data (address, mapUri, latitude, longitude)
        String address = json.optString("formattedAddress", "Address not available");
        String mapUri = json.getJSONObject("googleMapsLinks").optString("directionsUri", "");
        double latitude = 0.0;
        double longitude = 0.0;
        if (json.has("location")) {
            JSONObject location = json.getJSONObject("location");
            latitude = location.optDouble("latitude", 0.0);
            longitude = location.optDouble("longitude", 0.0);
        }
        builder.location(address, mapUri, latitude, longitude);

        // Extract type
        String type = "Restaurant"; // Default
        if (json.has("primaryTypeDisplayName")) {
            type = json.getJSONObject("primaryTypeDisplayName").optString("text", "Restaurant");
        }
        builder.type(type);

        // Extract rating and count
        double rating = json.optDouble("rating", 0.0);
        int ratingCount = json.optInt("userRatingCount", 0);
        builder.rating(rating, ratingCount);

        // Extract contact info
        String phoneNumber = json.optString("nationalPhoneNumber", "");
        String websiteUri = json.optString("websiteUri", "");
        builder.contact(phoneNumber, websiteUri);

        // Extract opening hours
        List<String> openingHours = new ArrayList<>();
        if (json.has("regularOpeningHours")) {
            JSONObject hoursObj = json.getJSONObject("regularOpeningHours");
            if (hoursObj.has("weekdayDescriptions")) {
                JSONArray hoursArray = hoursObj.getJSONArray("weekdayDescriptions");
                for (int i = 0; i < hoursArray.length(); i++) {
                    openingHours.add(hoursArray.getString(i));
                }
            }
        }
        builder.openingHours(openingHours);

        // Extract student discount (default to no discount since Google Places doesn't have this)
        builder.studentDiscount(false, 0.0);

        // Extract photo IDs - KEEP THE FULL PATH
        List<String> photoIds = new ArrayList<>();
        if (json.has("photos")) {
            JSONArray photosArray = json.getJSONArray("photos");
            for (int i = 0; i < photosArray.length(); i++) {
                JSONObject photo = photosArray.getJSONObject(i);
                if (photo.has("name")) {
                    // Keep the FULL photo name including places/ChIJ.../photos/...
                    String photoName = photo.getString("name");
                    System.out.println("DEBUG FirestoreUserRepo: Extracted photo name: " + photoName);
                    photoIds.add(photoName);  // Use full path, not just the ID!
                }
            }
        }
        builder.photoIds(photoIds);

        return builder.build();
    }

    /**
     * Extracts the CID parameter from a Google Maps placeUri.
     * Example: "https://maps.google.com/?cid=5193387586656989408&..." -> "5193387586656989408"
     *
     * @param placeUri The Google Maps place URI
     * @return The CID string
     * @throws IllegalArgumentException if no CID is found in the URI
     */
    private String extractCidFromPlaceUri(String placeUri) {
        int cidStart = placeUri.indexOf("cid=");
        if (cidStart == -1) {
            throw new IllegalArgumentException("No CID found in placeUri: " + placeUri);
        }

        cidStart += 4; // Move past "cid="
        int cidEnd = placeUri.indexOf('&', cidStart);

        if (cidEnd == -1) {
            // CID is at the end of the URL
            return placeUri.substring(cidStart);
        } else {
            return placeUri.substring(cidStart, cidEnd);
        }
    }

    @Override
    public Restaurant getRestaurantById(String restaurantId) {
        if (!isRestaurantCacheLoaded) {
            loadRestaurantsFromJson();
        }

        Restaurant restaurant = restaurantsById.get(restaurantId);

        if (restaurant == null) {
            System.err.println("Restaurant not found: " + restaurantId);
        }

        return restaurant;
    }

    @Override
    public List<Restaurant> getRestaurantsByIds(List<String> restaurantIds) {
        if (!isRestaurantCacheLoaded) {
            loadRestaurantsFromJson();
        }

        List<Restaurant> restaurants = new ArrayList<>();

        for (String id : restaurantIds) {
            Restaurant restaurant = restaurantsById.get(id);
            if (restaurant != null) {
                restaurants.add(restaurant);
            } else {
                System.err.println("Restaurant not found: " + id);
            }
        }

        System.out.println("DEBUG: Loaded " + restaurants.size() + " restaurants out of " + restaurantIds.size() + " requested IDs");

        return restaurants;
    }
}
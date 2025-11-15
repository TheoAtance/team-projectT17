package entity;

import java.util.ArrayList;
import java.util.List;


public class User {
    // Unique identifier provided by Firebase Authentication. This is the primary key.
    private final String uid;

    // Email address used for authentication (Firebase Auth).
    private String email;

    // The display name chosen by the user or provided by Google.
    private String nickname;

    private String language;

    private List<String> favoriteRestaurantIds;

    /**
     * Constructor used for creating a NEW User during registration.
     * Initializes lists as empty.
     * @param uid the Unique identifier provided by Firebase Authentication.
     * @param email Email address used for authentication (Firebase Auth).
     * @param nickname The user's nickname
     * @throws IllegalArgumentException if the uid, email and/or nickname is null or empty.
     */
    public User(String uid, String email, String nickname) {
        if ("".equals(uid) || uid == null) {
            throw new IllegalArgumentException("Uid cannot be null or empty");
        }
        if ("".equals(email) || email == null) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if ("".equals(nickname) || nickname == null) {
            throw new IllegalArgumentException("Nickname cannot be null or empty");
        }
        this.uid = uid;
        this.email = email;
        this.nickname = nickname;
        this.language = "en"; // Default language
        this.favoriteRestaurantIds = new ArrayList<>();
    }

    /**
     * Constructor used for loading an EXISTING User from Firebase Firestore.
     */
    public User(String uid, String email, String nickname, String language,
                List<String> favoriteRestaurantIds) {
        this.uid = uid;
        this.email = email;
        this.nickname = nickname;
        this.language = language;
        this.favoriteRestaurantIds = favoriteRestaurantIds;
    }

    // Getters

    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }

    public String getNickname() {
        return nickname;
    }

    public String getLanguage() {
        return language;
    }

    public List<String> getFavoriteRestaurantIds() {
        return favoriteRestaurantIds;
    }


    // Setters

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void addFavoriteRestaurantId(String restaurantId) {
        if (!this.favoriteRestaurantIds.contains(restaurantId)) {
            this.favoriteRestaurantIds.add(restaurantId);
        }
    }

    public void removeFavoriteRestaurantId(String restaurantId) {
        this.favoriteRestaurantIds.remove(restaurantId);
    }
}
package entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory for creating User entities. Encapsulates the creation logic and validation.
 */
public class UserFactory {

  /**
   * Creates a new User for registration (with defaults).
   */
  public User createNewUser(String uid, String email, String nickname) {
    validateRequiredFields(uid, email, nickname);
    return new User(uid, email, nickname);
  }

  /**
   * Creates an existing User loaded from database.
   */
  public User createExistingUser(String uid, String email, String nickname,
      String language, List<String> favoriteRestaurantIds) {
    validateRequiredFields(uid, email, nickname);

    // Apply defaults if null
    String finalLanguage = (language == null || language.isEmpty()) ? "en" : language;
    List<String> finalFavorites = (favoriteRestaurantIds == null)
        ? new ArrayList<>()
        : new ArrayList<>(favoriteRestaurantIds); // Defensive copy

    return new User(uid, email, nickname, finalLanguage, finalFavorites);
  }

  private void validateRequiredFields(String uid, String email, String nickname) {
    if (uid == null || uid.isEmpty()) {
      throw new IllegalArgumentException("UID cannot be null or empty");
    }
    if (email == null || email.isEmpty()) {
      throw new IllegalArgumentException("Email cannot be null or empty");
    }
    if (nickname == null || nickname.isEmpty()) {
      throw new IllegalArgumentException("Nickname cannot be null or empty");
    }
  }
}
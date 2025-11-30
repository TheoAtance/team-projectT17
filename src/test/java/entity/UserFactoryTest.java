package entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Minimal test suite for UserFactory achieving 100% code coverage.
 */
@DisplayName("UserFactory Tests")
class UserFactoryTest {

  private UserFactory userFactory;

  @BeforeEach
  void setUp() {
    userFactory = new UserFactory();
  }

  // ==================== CREATE NEW USER TESTS ====================

  @Test
  @DisplayName("createNewUser: Creates user with valid data")
  void testCreateNewUser() {
    User user = userFactory.createNewUser("uid123", "test@example.com", "TestUser");

    assertEquals("uid123", user.getUid());
    assertEquals("test@example.com", user.getEmail());
    assertEquals("TestUser", user.getNickname());
    assertEquals("en", user.getLanguage());
    assertTrue(user.getFavoriteRestaurantIds().isEmpty());
  }

  @Test
  @DisplayName("createNewUser: Null UID throws exception")
  void testCreateNewUserNullUid() {
    assertThrows(IllegalArgumentException.class, () ->
        userFactory.createNewUser(null, "test@example.com", "TestUser")
    );
  }

  @Test
  @DisplayName("createNewUser: Empty UID throws exception")
  void testCreateNewUserEmptyUid() {
    assertThrows(IllegalArgumentException.class, () ->
        userFactory.createNewUser("", "test@example.com", "TestUser")
    );
  }

  @Test
  @DisplayName("createNewUser: Null email throws exception")
  void testCreateNewUserNullEmail() {
    assertThrows(IllegalArgumentException.class, () ->
        userFactory.createNewUser("uid123", null, "TestUser")
    );
  }

  @Test
  @DisplayName("createNewUser: Empty email throws exception")
  void testCreateNewUserEmptyEmail() {
    assertThrows(IllegalArgumentException.class, () ->
        userFactory.createNewUser("uid123", "", "TestUser")
    );
  }

  @Test
  @DisplayName("createNewUser: Null nickname throws exception")
  void testCreateNewUserNullNickname() {
    assertThrows(IllegalArgumentException.class, () ->
        userFactory.createNewUser("uid123", "test@example.com", null)
    );
  }

  @Test
  @DisplayName("createNewUser: Empty nickname throws exception")
  void testCreateNewUserEmptyNickname() {
    assertThrows(IllegalArgumentException.class, () ->
        userFactory.createNewUser("uid123", "test@example.com", "")
    );
  }

  // ==================== CREATE EXISTING USER TESTS ====================

  @Test
  @DisplayName("createExistingUser: Creates user with full data")
  void testCreateExistingUser() {
    List<String> favorites = Arrays.asList("rest1", "rest2");
    User user = userFactory.createExistingUser(
        "uid123",
        "test@example.com",
        "TestUser",
        "fr",
        favorites
    );

    assertEquals("uid123", user.getUid());
    assertEquals("test@example.com", user.getEmail());
    assertEquals("TestUser", user.getNickname());
    assertEquals("fr", user.getLanguage());
    assertEquals(2, user.getFavoriteRestaurantIds().size());
    assertTrue(user.getFavoriteRestaurantIds().contains("rest1"));
  }

  @Test
  @DisplayName("createExistingUser: Null language defaults to 'en'")
  void testCreateExistingUserNullLanguage() {
    User user = userFactory.createExistingUser(
        "uid123",
        "test@example.com",
        "TestUser",
        null,
        new ArrayList<>()
    );

    assertEquals("en", user.getLanguage());
  }

  @Test
  @DisplayName("createExistingUser: Empty language defaults to 'en'")
  void testCreateExistingUserEmptyLanguage() {
    User user = userFactory.createExistingUser(
        "uid123",
        "test@example.com",
        "TestUser",
        "",
        new ArrayList<>()
    );

    assertEquals("en", user.getLanguage());
  }

  @Test
  @DisplayName("createExistingUser: Null favorites list defaults to empty list")
  void testCreateExistingUserNullFavorites() {
    User user = userFactory.createExistingUser(
        "uid123",
        "test@example.com",
        "TestUser",
        "en",
        null
    );

    assertNotNull(user.getFavoriteRestaurantIds());
    assertTrue(user.getFavoriteRestaurantIds().isEmpty());
  }

  @Test
  @DisplayName("createExistingUser: Creates defensive copy of favorites list")
  void testCreateExistingUserDefensiveCopy() {
    List<String> originalFavorites = new ArrayList<>(List.of("rest1"));
    User user = userFactory.createExistingUser(
        "uid123",
        "test@example.com",
        "TestUser",
        "en",
        originalFavorites
    );

    // Modify original list
    originalFavorites.add("rest2");

    // User's list should not be affected
    assertEquals(1, user.getFavoriteRestaurantIds().size());
    assertTrue(user.getFavoriteRestaurantIds().contains("rest1"));
    assertFalse(user.getFavoriteRestaurantIds().contains("rest2"));
  }

  @Test
  @DisplayName("createExistingUser: Null UID throws exception")
  void testCreateExistingUserNullUid() {
    assertThrows(IllegalArgumentException.class, () ->
        userFactory.createExistingUser(null, "test@example.com", "TestUser", "en",
            new ArrayList<>())
    );
  }

  @Test
  @DisplayName("createExistingUser: Empty UID throws exception")
  void testCreateExistingUserEmptyUid() {
    assertThrows(IllegalArgumentException.class, () ->
        userFactory.createExistingUser("", "test@example.com", "TestUser", "en", new ArrayList<>())
    );
  }

  @Test
  @DisplayName("createExistingUser: Null email throws exception")
  void testCreateExistingUserNullEmail() {
    assertThrows(IllegalArgumentException.class, () ->
        userFactory.createExistingUser("uid123", null, "TestUser", "en", new ArrayList<>())
    );
  }

  @Test
  @DisplayName("createExistingUser: Empty email throws exception")
  void testCreateExistingUserEmptyEmail() {
    assertThrows(IllegalArgumentException.class, () ->
        userFactory.createExistingUser("uid123", "", "TestUser", "en", new ArrayList<>())
    );
  }

  @Test
  @DisplayName("createExistingUser: Null nickname throws exception")
  void testCreateExistingUserNullNickname() {
    assertThrows(IllegalArgumentException.class, () ->
        userFactory.createExistingUser("uid123", "test@example.com", null, "en", new ArrayList<>())
    );
  }

  @Test
  @DisplayName("createExistingUser: Empty nickname throws exception")
  void testCreateExistingUserEmptyNickname() {
    assertThrows(IllegalArgumentException.class, () ->
        userFactory.createExistingUser("uid123", "test@example.com", "", "en", new ArrayList<>())
    );
  }
}
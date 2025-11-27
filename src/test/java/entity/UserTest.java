package entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Minimal test suite for User entity achieving 100% code coverage.
 */
@DisplayName("User Entity Tests")
class UserTest {

    // ==================== CONSTRUCTOR TESTS (NEW USER) ====================

    @Test
    @DisplayName("Constructor: Create new user with valid data")
    void testNewUserConstructor() {
        User user = new User("uid123", "test@example.com", "TestUser");

        assertEquals("uid123", user.getUid());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("TestUser", user.getNickname());
        assertEquals("en", user.getLanguage());
        assertTrue(user.getFavoriteRestaurantIds().isEmpty());
    }

    @Test
    @DisplayName("Constructor: Null UID throws exception")
    void testNullUid() {
        assertThrows(IllegalArgumentException.class, () ->
                new User(null, "test@example.com", "TestUser")
        );
    }

    @Test
    @DisplayName("Constructor: Empty UID throws exception")
    void testEmptyUid() {
        assertThrows(IllegalArgumentException.class, () ->
                new User("", "test@example.com", "TestUser")
        );
    }

    @Test
    @DisplayName("Constructor: Null email throws exception")
    void testNullEmail() {
        assertThrows(IllegalArgumentException.class, () ->
                new User("uid123", null, "TestUser")
        );
    }

    @Test
    @DisplayName("Constructor: Empty email throws exception")
    void testEmptyEmail() {
        assertThrows(IllegalArgumentException.class, () ->
                new User("uid123", "", "TestUser")
        );
    }

    @Test
    @DisplayName("Constructor: Null nickname throws exception")
    void testNullNickname() {
        assertThrows(IllegalArgumentException.class, () ->
                new User("uid123", "test@example.com", null)
        );
    }

    @Test
    @DisplayName("Constructor: Empty nickname throws exception")
    void testEmptyNickname() {
        assertThrows(IllegalArgumentException.class, () ->
                new User("uid123", "test@example.com", "")
        );
    }

    // ==================== CONSTRUCTOR TESTS (EXISTING USER) ====================

    @Test
    @DisplayName("Constructor: Create existing user with full data")
    void testExistingUserConstructor() {
        List<String> favorites = Arrays.asList("rest1", "rest2");
        User user = new User("uid123", "test@example.com", "TestUser", "fr", favorites);

        assertEquals("uid123", user.getUid());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("TestUser", user.getNickname());
        assertEquals("fr", user.getLanguage());
        assertEquals(2, user.getFavoriteRestaurantIds().size());
        assertTrue(user.getFavoriteRestaurantIds().contains("rest1"));
        assertTrue(user.getFavoriteRestaurantIds().contains("rest2"));
    }

    // ==================== SETTER TESTS ====================

    @Test
    @DisplayName("Setter: Update email")
    void testSetEmail() {
        User user = new User("uid123", "old@example.com", "TestUser");
        user.setEmail("new@example.com");
        assertEquals("new@example.com", user.getEmail());
    }

    @Test
    @DisplayName("Setter: Update nickname")
    void testSetNickname() {
        User user = new User("uid123", "test@example.com", "OldName");
        user.setNickname("NewName");
        assertEquals("NewName", user.getNickname());
    }

    @Test
    @DisplayName("Setter: Update language")
    void testSetLanguage() {
        User user = new User("uid123", "test@example.com", "TestUser");
        user.setLanguage("es");
        assertEquals("es", user.getLanguage());
    }

    // ==================== FAVORITE RESTAURANT TESTS ====================

    @Test
    @DisplayName("Add favorite restaurant: New restaurant is added")
    void testAddFavoriteRestaurant() {
        User user = new User("uid123", "test@example.com", "TestUser");
        user.addFavoriteRestaurantId("restaurant1");

        assertEquals(1, user.getFavoriteRestaurantIds().size());
        assertTrue(user.getFavoriteRestaurantIds().contains("restaurant1"));
    }

    @Test
    @DisplayName("Add favorite restaurant: Duplicate is not added")
    void testAddDuplicateFavoriteRestaurant() {
        User user = new User("uid123", "test@example.com", "TestUser");
        user.addFavoriteRestaurantId("restaurant1");
        user.addFavoriteRestaurantId("restaurant1"); // Try to add duplicate

        assertEquals(1, user.getFavoriteRestaurantIds().size());
    }

    @Test
    @DisplayName("Remove favorite restaurant: Restaurant is removed")
    void testRemoveFavoriteRestaurant() {
        User user = new User("uid123", "test@example.com", "TestUser");
        user.addFavoriteRestaurantId("restaurant1");
        user.addFavoriteRestaurantId("restaurant2");

        user.removeFavoriteRestaurantId("restaurant1");

        assertEquals(1, user.getFavoriteRestaurantIds().size());
        assertFalse(user.getFavoriteRestaurantIds().contains("restaurant1"));
        assertTrue(user.getFavoriteRestaurantIds().contains("restaurant2"));
    }

    @Test
    @DisplayName("Remove favorite restaurant: Non-existent restaurant doesn't cause error")
    void testRemoveNonExistentFavoriteRestaurant() {
        User user = new User("uid123", "test@example.com", "TestUser");
        user.removeFavoriteRestaurantId("nonexistent"); // Should not throw exception

        assertEquals(0, user.getFavoriteRestaurantIds().size());
    }
}
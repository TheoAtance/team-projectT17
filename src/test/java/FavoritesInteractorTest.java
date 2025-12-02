import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import data_access.UserDataAccessInterface;
import entity.Restaurant;
import entity.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import use_case.favorites.add_favorite.AddFavoriteInputData;
import use_case.favorites.add_favorite.AddFavoriteInteractor;
import use_case.favorites.add_favorite.AddFavoriteOutputBoundary;
import use_case.favorites.add_favorite.AddFavoriteOutputData;
import use_case.favorites.remove_favorite.RemoveFavoriteInputData;
import use_case.favorites.remove_favorite.RemoveFavoriteInteractor;
import use_case.favorites.remove_favorite.RemoveFavoriteOutputBoundary;
import use_case.favorites.remove_favorite.RemoveFavoriteOutputData;
import use_case.favorites.get_favorites.GetFavoritesInputData;
import use_case.favorites.get_favorites.GetFavoritesInteractor;
import use_case.favorites.get_favorites.GetFavoritesOutputBoundary;
import use_case.favorites.get_favorites.GetFavoritesOutputData;

/**
 * Unit tests for Favorites Interactors: AddFavorite, RemoveFavorite, GetFavorites.
 */
public class FavoritesInteractorTest {

    // ==================== AddFavoriteInteractor Tests ====================

    @Test
    public void testAddFavoriteSuccess() {
        // Arrange
        TestUserDataAccess testDataAccess = new TestUserDataAccess();
        TestAddFavoritePresenter testPresenter = new TestAddFavoritePresenter();
        AddFavoriteInteractor interactor = new AddFavoriteInteractor(testDataAccess, testPresenter);

        String userId = "user123";
        String restaurantId = "rest456";
        testDataAccess.addUser(userId, "TestUser");

        AddFavoriteInputData inputData = new AddFavoriteInputData(userId, restaurantId);

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue("Present success should be called", testPresenter.isSuccessCalled());
        assertFalse("Present error should not be called", testPresenter.isErrorCalled());
        assertTrue("User should have the restaurant in favorites",
                testDataAccess.getUser(userId).getFavoriteRestaurantIds().contains(restaurantId));
    }

    @Test
    public void testAddFavoriteUserNotFound() {
        // Arrange
        TestUserDataAccess testDataAccess = new TestUserDataAccess();
        TestAddFavoritePresenter testPresenter = new TestAddFavoritePresenter();
        AddFavoriteInteractor interactor = new AddFavoriteInteractor(testDataAccess, testPresenter);

        String userId = "nonexistentUser";
        String restaurantId = "rest456";

        AddFavoriteInputData inputData = new AddFavoriteInputData(userId, restaurantId);

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue("Present error should be called for non-existent user", testPresenter.isErrorCalled());
        assertFalse("Present success should not be called", testPresenter.isSuccessCalled());
        assertTrue("Error message should mention user not found",
                testPresenter.getErrorMessage().contains("User not found"));
    }

    @Test
    public void testAddFavoriteAlreadyExists() {
        // Arrange
        TestUserDataAccess testDataAccess = new TestUserDataAccess();
        TestAddFavoritePresenter testPresenter = new TestAddFavoritePresenter();
        AddFavoriteInteractor interactor = new AddFavoriteInteractor(testDataAccess, testPresenter);

        String userId = "user123";
        String restaurantId = "rest456";
        testDataAccess.addUser(userId, "TestUser");
        testDataAccess.addFavoriteToUser(userId, restaurantId); // Already a favorite

        AddFavoriteInputData inputData = new AddFavoriteInputData(userId, restaurantId);

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue("Present error should be called for duplicate", testPresenter.isErrorCalled());
        assertFalse("Present success should not be called", testPresenter.isSuccessCalled());
        assertTrue("Error message should mention already in favorites",
                testPresenter.getErrorMessage().contains("already in favorites"));
    }

    @Test
    public void testAddMultipleFavorites() {
        // Arrange
        TestUserDataAccess testDataAccess = new TestUserDataAccess();
        TestAddFavoritePresenter testPresenter = new TestAddFavoritePresenter();
        AddFavoriteInteractor interactor = new AddFavoriteInteractor(testDataAccess, testPresenter);

        String userId = "user123";
        testDataAccess.addUser(userId, "TestUser");

        // Act - Add multiple favorites
        interactor.execute(new AddFavoriteInputData(userId, "rest1"));
        interactor.execute(new AddFavoriteInputData(userId, "rest2"));
        interactor.execute(new AddFavoriteInputData(userId, "rest3"));

        // Assert
        List<String> favorites = testDataAccess.getUser(userId).getFavoriteRestaurantIds();
        assertEquals("User should have 3 favorites", 3, favorites.size());
        assertTrue("Should contain rest1", favorites.contains("rest1"));
        assertTrue("Should contain rest2", favorites.contains("rest2"));
        assertTrue("Should contain rest3", favorites.contains("rest3"));
    }

    // ==================== RemoveFavoriteInteractor Tests ====================

    @Test
    public void testRemoveFavoriteSuccess() {
        // Arrange
        TestUserDataAccess testDataAccess = new TestUserDataAccess();
        TestRemoveFavoritePresenter testPresenter = new TestRemoveFavoritePresenter();
        RemoveFavoriteInteractor interactor = new RemoveFavoriteInteractor(testDataAccess, testPresenter);

        String userId = "user123";
        String restaurantId = "rest456";
        testDataAccess.addUser(userId, "TestUser");
        testDataAccess.addFavoriteToUser(userId, restaurantId);
        testDataAccess.addRestaurant(restaurantId, "Test Restaurant", "Cafe");

        RemoveFavoriteInputData inputData = new RemoveFavoriteInputData(userId, restaurantId);

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue("Present success should be called", testPresenter.isSuccessCalled());
        assertFalse("Present error should not be called", testPresenter.isErrorCalled());
        assertFalse("Restaurant should be removed from favorites",
                testDataAccess.getUser(userId).getFavoriteRestaurantIds().contains(restaurantId));
    }

    @Test
    public void testRemoveFavoriteUserNotFound() {
        // Arrange
        TestUserDataAccess testDataAccess = new TestUserDataAccess();
        TestRemoveFavoritePresenter testPresenter = new TestRemoveFavoritePresenter();
        RemoveFavoriteInteractor interactor = new RemoveFavoriteInteractor(testDataAccess, testPresenter);

        String userId = "nonexistentUser";
        String restaurantId = "rest456";

        RemoveFavoriteInputData inputData = new RemoveFavoriteInputData(userId, restaurantId);

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue("Present error should be called for non-existent user", testPresenter.isErrorCalled());
        assertFalse("Present success should not be called", testPresenter.isSuccessCalled());
        assertTrue("Error message should mention user not found",
                testPresenter.getErrorMessage().contains("User not found"));
    }

    @Test
    public void testRemoveFavoriteNotInList() {
        // Arrange
        TestUserDataAccess testDataAccess = new TestUserDataAccess();
        TestRemoveFavoritePresenter testPresenter = new TestRemoveFavoritePresenter();
        RemoveFavoriteInteractor interactor = new RemoveFavoriteInteractor(testDataAccess, testPresenter);

        String userId = "user123";
        String restaurantId = "rest456";
        testDataAccess.addUser(userId, "TestUser");
        // Don't add the restaurant to favorites

        RemoveFavoriteInputData inputData = new RemoveFavoriteInputData(userId, restaurantId);

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue("Present error should be called when not in favorites", testPresenter.isErrorCalled());
        assertFalse("Present success should not be called", testPresenter.isSuccessCalled());
        assertTrue("Error message should mention not in favorites",
                testPresenter.getErrorMessage().contains("not in favorites"));
    }

    @Test
    public void testRemoveFromMultipleFavorites() {
        // Arrange
        TestUserDataAccess testDataAccess = new TestUserDataAccess();
        TestRemoveFavoritePresenter testPresenter = new TestRemoveFavoritePresenter();
        RemoveFavoriteInteractor interactor = new RemoveFavoriteInteractor(testDataAccess, testPresenter);

        String userId = "user123";
        testDataAccess.addUser(userId, "TestUser");
        testDataAccess.addFavoriteToUser(userId, "rest1");
        testDataAccess.addFavoriteToUser(userId, "rest2");
        testDataAccess.addFavoriteToUser(userId, "rest3");
        testDataAccess.addRestaurant("rest2", "Restaurant 2", "Cafe");

        RemoveFavoriteInputData inputData = new RemoveFavoriteInputData(userId, "rest2");

        // Act
        interactor.execute(inputData);

        // Assert
        List<String> favorites = testDataAccess.getUser(userId).getFavoriteRestaurantIds();
        assertEquals("User should have 2 favorites remaining", 2, favorites.size());
        assertTrue("Should still contain rest1", favorites.contains("rest1"));
        assertFalse("Should not contain rest2", favorites.contains("rest2"));
        assertTrue("Should still contain rest3", favorites.contains("rest3"));
    }

    // ==================== GetFavoritesInteractor Tests ====================

    @Test
    public void testGetFavoritesSuccess() {
        // Arrange
        TestUserDataAccess testDataAccess = new TestUserDataAccess();
        TestGetFavoritesPresenter testPresenter = new TestGetFavoritesPresenter();
        GetFavoritesInteractor interactor = new GetFavoritesInteractor(testDataAccess, testPresenter);

        String userId = "user123";
        testDataAccess.addUser(userId, "TestUser");
        testDataAccess.addFavoriteToUser(userId, "rest1");
        testDataAccess.addFavoriteToUser(userId, "rest2");
        testDataAccess.addRestaurant("rest1", "Pizza Place", "Italian");
        testDataAccess.addRestaurant("rest2", "Sushi Bar", "Japanese");

        GetFavoritesInputData inputData = new GetFavoritesInputData(userId);

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue("Present favorites should be called", testPresenter.isSuccessCalled());
        assertFalse("Present error should not be called", testPresenter.isErrorCalled());
        assertEquals("Should return 2 favorite restaurants", 2,
                testPresenter.getOutputData().getRestaurants().size());
    }

    @Test
    public void testGetFavoritesEmpty() {
        // Arrange
        TestUserDataAccess testDataAccess = new TestUserDataAccess();
        TestGetFavoritesPresenter testPresenter = new TestGetFavoritesPresenter();
        GetFavoritesInteractor interactor = new GetFavoritesInteractor(testDataAccess, testPresenter);

        String userId = "user123";
        testDataAccess.addUser(userId, "TestUser");
        // Don't add any favorites

        GetFavoritesInputData inputData = new GetFavoritesInputData(userId);

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue("Present favorites should be called even for empty favorites", testPresenter.isSuccessCalled());
        assertFalse("Present error should not be called", testPresenter.isErrorCalled());
        assertEquals("Should return empty list", 0,
                testPresenter.getOutputData().getRestaurants().size());
    }

    @Test
    public void testGetFavoritesUserNotFound() {
        // Arrange
        TestUserDataAccess testDataAccess = new TestUserDataAccess();
        TestGetFavoritesPresenter testPresenter = new TestGetFavoritesPresenter();
        GetFavoritesInteractor interactor = new GetFavoritesInteractor(testDataAccess, testPresenter);

        String userId = "nonexistentUser";

        GetFavoritesInputData inputData = new GetFavoritesInputData(userId);

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue("Present error should be called for non-existent user", testPresenter.isErrorCalled());
        assertFalse("Present favorites should not be called", testPresenter.isSuccessCalled());
        assertTrue("Error message should mention user not found",
                testPresenter.getErrorMessage().contains("User not found"));
    }

    @Test
    public void testGetFavoritesWithMissingRestaurant() {
        // Arrange
        TestUserDataAccess testDataAccess = new TestUserDataAccess();
        TestGetFavoritesPresenter testPresenter = new TestGetFavoritesPresenter();
        GetFavoritesInteractor interactor = new GetFavoritesInteractor(testDataAccess, testPresenter);

        String userId = "user123";
        testDataAccess.addUser(userId, "TestUser");
        testDataAccess.addFavoriteToUser(userId, "rest1");
        testDataAccess.addFavoriteToUser(userId, "rest_deleted"); // Restaurant doesn't exist
        testDataAccess.addRestaurant("rest1", "Pizza Place", "Italian");
        // Don't add rest_deleted to restaurants

        GetFavoritesInputData inputData = new GetFavoritesInputData(userId);

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue("Should handle missing restaurant gracefully", testPresenter.isSuccessCalled());
        // Should return only the restaurant that exists
        assertEquals("Should return 1 restaurant (the one that exists)", 1,
                testPresenter.getOutputData().getRestaurants().size());
    }

    // ==================== Test Doubles ====================

    /**
     * Test double for UserDataAccessInterface
     */
    private static class TestUserDataAccess implements UserDataAccessInterface {

        private final Map<String, TestUser> users = new HashMap<>();
        private final Map<String, Restaurant> restaurants = new HashMap<>();

        public void addUser(String userId, String username) {
            users.put(userId, new TestUser(userId, username));
        }

        public void addFavoriteToUser(String userId, String restaurantId) {
            TestUser user = users.get(userId);
            if (user != null) {
                user.addFavoriteRestaurantId(restaurantId);
            }
        }

        public void addRestaurant(String id, String name, String type) {
            Restaurant restaurant = new Restaurant.Builder()
                    .id(id)
                    .name(name)
                    .type(type)
                    .location("123 Test St", "http://maps.google.com", 43.0, -79.0)
                    .rating(4.5, 100)
                    .contact("416-123-4567", "http://example.com")
                    .openingHours(List.of("Mon-Fri: 9AM-5PM"))
                    .studentDiscount(false, 0.0)
                    .photoIds(List.of("photo1"))
                    .build();
            restaurants.put(id, restaurant);
        }

        @Override
        public User getUser(String userId) {
            return users.get(userId);
        }

        @Override
        public void saveUser(User user) {
            // Already saved in memory via TestUser reference
        }

        @Override
        public Restaurant getRestaurantById(String restaurantId) {
            return restaurants.get(restaurantId);
        }

        @Override
        public List<Restaurant> getRestaurantsByIds(List<String> restaurantIds) {
            List<Restaurant> result = new ArrayList<>();
            for (String id : restaurantIds) {
                Restaurant restaurant = restaurants.get(id);
                if (restaurant != null) {
                    result.add(restaurant);
                }
            }
            return result;
        }
    }

    /**
     * Test implementation of User for testing
     */
    private static class TestUser extends User {

        private final String uid;
        private final String nickname;
        private final List<String> favoriteRestaurantIds = new ArrayList<>();

        public TestUser(String uid, String nickname) {
            super(uid, nickname, nickname + "@test.com");
            this.uid = uid;
            this.nickname = nickname;
        }

        @Override
        public void addFavoriteRestaurantId(String restaurantId) {
            if (!favoriteRestaurantIds.contains(restaurantId)) {
                favoriteRestaurantIds.add(restaurantId);
            }
        }

        @Override
        public void removeFavoriteRestaurantId(String restaurantId) {
            favoriteRestaurantIds.remove(restaurantId);
        }

        @Override
        public List<String> getFavoriteRestaurantIds() {
            return new ArrayList<>(favoriteRestaurantIds);
        }

        @Override
        public String getUid() {
            return uid;
        }

        @Override
        public String getNickname() {
            return nickname;
        }
    }

    /**
     * Test double for AddFavoriteOutputBoundary
     */
    private static class TestAddFavoritePresenter implements AddFavoriteOutputBoundary {

        private boolean successCalled = false;
        private boolean errorCalled = false;
        private AddFavoriteOutputData outputData;
        private String errorMessage;

        @Override
        public void presentSuccess(AddFavoriteOutputData outputData) {
            this.successCalled = true;
            this.outputData = outputData;
        }

        @Override
        public void presentError(String errorMessage) {
            this.errorCalled = true;
            this.errorMessage = errorMessage;
        }

        public boolean isSuccessCalled() {
            return successCalled;
        }

        public boolean isErrorCalled() {
            return errorCalled;
        }

        public AddFavoriteOutputData getOutputData() {
            return outputData;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    /**
     * Test double for RemoveFavoriteOutputBoundary
     */
    private static class TestRemoveFavoritePresenter implements RemoveFavoriteOutputBoundary {

        private boolean successCalled = false;
        private boolean errorCalled = false;
        private RemoveFavoriteOutputData outputData;
        private String errorMessage;

        @Override
        public void presentSuccess(RemoveFavoriteOutputData outputData) {
            this.successCalled = true;
            this.outputData = outputData;
        }

        @Override
        public void presentError(String errorMessage) {
            this.errorCalled = true;
            this.errorMessage = errorMessage;
        }

        public boolean isSuccessCalled() {
            return successCalled;
        }

        public boolean isErrorCalled() {
            return errorCalled;
        }

        public RemoveFavoriteOutputData getOutputData() {
            return outputData;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    /**
     * Test double for GetFavoritesOutputBoundary
     */
    private static class TestGetFavoritesPresenter implements GetFavoritesOutputBoundary {

        private boolean successCalled = false;
        private boolean errorCalled = false;
        private GetFavoritesOutputData outputData;
        private String errorMessage;

        @Override
        public void presentFavorites(GetFavoritesOutputData outputData) {
            this.successCalled = true;
            this.outputData = outputData;
        }

        @Override
        public void presentError(String errorMessage) {
            this.errorCalled = true;
            this.errorMessage = errorMessage;
        }

        public boolean isSuccessCalled() {
            return successCalled;
        }

        public boolean isErrorCalled() {
            return errorCalled;
        }

        public GetFavoritesOutputData getOutputData() {
            return outputData;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
import entity.Restaurant;
import org.junit.Test;
import static org.junit.Assert.*;
import use_case.filter.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Unit test for FilterInteractor.
 */
public class FilterInteractorTest {

    @Test
    public void testExecuteWithValidType() {
        // Arrange
        TestRestaurantDataAccess testDataAccess = new TestRestaurantDataAccess();
        TestFilterPresenter testPresenter = new TestFilterPresenter();
        FilterInteractor filterInteractor = new FilterInteractor(testDataAccess, testPresenter);

        String restaurantType = "Italian Restaurant";
        FilterInputData inputData = new FilterInputData(restaurantType);

        // Act
        filterInteractor.execute(inputData);

        // Assert
        assertTrue("Success view should be called", testPresenter.isSuccessViewCalled());
        assertFalse("Fail view should not be called", testPresenter.isFailViewCalled());
        assertEquals("Filter type should match input", restaurantType, testPresenter.getOutputData().getFilterType());
        assertEquals("Should return 3 Italian restaurants", 3, testPresenter.getOutputData().getRestaurantNames().size());
    }

    @Test
    public void testExecuteWithEmptyResults() {
        // Arrange
        TestRestaurantDataAccess testDataAccess = new TestRestaurantDataAccess();
        TestFilterPresenter testPresenter = new TestFilterPresenter();
        FilterInteractor filterInteractor = new FilterInteractor(testDataAccess, testPresenter);

        String restaurantType = "Nonexistent Type";
        FilterInputData inputData = new FilterInputData(restaurantType);

        // Act
        filterInteractor.execute(inputData);

        // Assert
        assertTrue("Success view should be called even with empty results", testPresenter.isSuccessViewCalled());
        assertEquals("Should return empty list for nonexistent type", 0, testPresenter.getOutputData().getRestaurantNames().size());
    }

    @Test
    public void testExecuteLimitsToTenRestaurants() {
        // Arrange
        TestRestaurantDataAccess testDataAccess = new TestRestaurantDataAccess();
        TestFilterPresenter testPresenter = new TestFilterPresenter();
        FilterInteractor filterInteractor = new FilterInteractor(testDataAccess, testPresenter);

        String restaurantType = "Chinese Restaurant";
        FilterInputData inputData = new FilterInputData(restaurantType);

        // Act
        filterInteractor.execute(inputData);

        // Assert
        assertTrue("Success view should be called", testPresenter.isSuccessViewCalled());
        assertEquals("Should limit results to 10 restaurants", 10, testPresenter.getOutputData().getRestaurantNames().size());
    }

    @Test
    public void testExecuteWithException() {
        // Arrange
        TestRestaurantDataAccess testDataAccess = new TestRestaurantDataAccess();
        testDataAccess.setShouldThrowException(true);
        TestFilterPresenter testPresenter = new TestFilterPresenter();
        FilterInteractor filterInteractor = new FilterInteractor(testDataAccess, testPresenter);

        FilterInputData inputData = new FilterInputData("Any Type");

        // Act
        filterInteractor.execute(inputData);

        // Assert
        assertTrue("Fail view should be called when exception occurs", testPresenter.isFailViewCalled());
        assertFalse("Success view should not be called when exception occurs", testPresenter.isSuccessViewCalled());
        assertTrue("Error message should indicate filtering error",
                testPresenter.getErrorMessage().contains("Error filtering restaurants"));
    }

    @Test
    public void testGetAvailableTypes() {
        // Arrange
        TestRestaurantDataAccess testDataAccess = new TestRestaurantDataAccess();
        TestFilterPresenter testPresenter = new TestFilterPresenter();
        FilterInteractor filterInteractor = new FilterInteractor(testDataAccess, testPresenter);

        // Act
        String[] types = filterInteractor.getAvailableTypes();

        // Assert
        assertNotNull("Available types should not be null", types);
        assertEquals("Should return 3 types", 3, types.length);
        assertTrue("Should contain Italian Restaurant", Arrays.asList(types).contains("Italian Restaurant"));
        assertTrue("Should contain Chinese Restaurant", Arrays.asList(types).contains("Chinese Restaurant"));
        assertTrue("Should contain Japanese Restaurant", Arrays.asList(types).contains("Japanese Restaurant"));
    }

    @Test
    public void testRestaurantNamesAreExtractedCorrectly() {
        // Arrange
        TestRestaurantDataAccess testDataAccess = new TestRestaurantDataAccess();
        TestFilterPresenter testPresenter = new TestFilterPresenter();
        FilterInteractor filterInteractor = new FilterInteractor(testDataAccess, testPresenter);

        String restaurantType = "Japanese Restaurant";
        FilterInputData inputData = new FilterInputData(restaurantType);

        // Act
        filterInteractor.execute(inputData);

        // Assert
        List<String> names = testPresenter.getOutputData().getRestaurantNames();
        assertEquals("Should return 2 Japanese restaurants", 2, names.size());
        assertTrue("Should contain Sushi Place", names.contains("Sushi Place"));
        assertTrue("Should contain Ramen House", names.contains("Ramen House"));
    }

    // ==================== Test Doubles ====================

    /**
     * Test double for IRestaurantDataAccess
     */
    private static class TestRestaurantDataAccess implements IRestaurantDataAccess {
        private boolean shouldThrowException = false;

        public void setShouldThrowException(boolean shouldThrow) {
            this.shouldThrowException = shouldThrow;
        }

        @Override
        public List<Restaurant> getRestaurantsByType(String type) {
            if (shouldThrowException) {
                throw new RuntimeException("Test exception");
            }

            List<Restaurant> restaurants = new ArrayList<>();

            if (type.equals("Italian Restaurant")) {
                restaurants.add(createRestaurant("R1", "Pasta Palace", "Italian Restaurant"));
                restaurants.add(createRestaurant("R2", "Pizza Parlor", "Italian Restaurant"));
                restaurants.add(createRestaurant("R3", "Trattoria", "Italian Restaurant"));
            } else if (type.equals("Chinese Restaurant")) {
                // Create 12 restaurants to test the 10 restaurant limit
                for (int i = 1; i <= 12; i++) {
                    restaurants.add(createRestaurant("C" + i, "Chinese Place " + i, "Chinese Restaurant"));
                }
            } else if (type.equals("Japanese Restaurant")) {
                restaurants.add(createRestaurant("J1", "Sushi Place", "Japanese Restaurant"));
                restaurants.add(createRestaurant("J2", "Ramen House", "Japanese Restaurant"));
            }

            return restaurants;
        }

        @Override
        public String[] getAllRestaurantTypes() {
            return new String[]{"Italian Restaurant", "Chinese Restaurant", "Japanese Restaurant"};
        }

        @Override
        public List<Restaurant> getAllRestaurants() {
            List<Restaurant> allRestaurants = new ArrayList<>();
            allRestaurants.addAll(getRestaurantsByType("Italian Restaurant"));
            allRestaurants.addAll(getRestaurantsByType("Chinese Restaurant"));
            allRestaurants.addAll(getRestaurantsByType("Japanese Restaurant"));
            return allRestaurants;
        }

        private Restaurant createRestaurant(String id, String name, String type) {
            return new Restaurant.Builder()
                    .id(id)
                    .name(name)
                    .location("123 Main St", "http://maps.google.com", 43.0, -79.0)
                    .type(type)
                    .rating(4.5, 100)
                    .contact("416-123-4567", "http://example.com")
                    .openingHours(Arrays.asList("Mon-Fri: 9AM-5PM"))
                    .studentDiscount(false, 0.0)
                    .photoIds(Arrays.asList("photo1"))
                    .build();
        }
    }

    /**
     * Test double for FilterOutputBoundary
     */
    private static class TestFilterPresenter implements FilterOutputBoundary {
        private boolean successViewCalled = false;
        private boolean failViewCalled = false;
        private FilterOutputData outputData;
        private String errorMessage;

        @Override
        public void prepareSuccessView(FilterOutputData outputData) {
            this.successViewCalled = true;
            this.outputData = outputData;
        }

        @Override
        public void prepareFailView(String error) {
            this.failViewCalled = true;
            this.errorMessage = error;
        }

        public boolean isSuccessViewCalled() {
            return successViewCalled;
        }

        public boolean isFailViewCalled() {
            return failViewCalled;
        }

        public FilterOutputData getOutputData() {
            return outputData;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
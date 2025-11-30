import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import entity.Restaurant;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import use_case.filter.IRestaurantDataAccess;
import use_case.list_search.ListSearchInputData;
import use_case.list_search.ListSearchInteractor;
import use_case.list_search.ListSearchOutputBoundary;
import use_case.list_search.ListSearchOutputData;

/**
 * Unit test for ListSearchInteractor.
 */
public class ListSearchInteractorTest {

  @Test
  public void testSearchWithMatchingQuery() {
    // Arrange
    TestRestaurantDataAccess testDataAccess = new TestRestaurantDataAccess();
    TestListSearchPresenter testPresenter = new TestListSearchPresenter();
    ListSearchInteractor interactor = new ListSearchInteractor(testDataAccess, testPresenter);

    String query = "Pizza";
    ListSearchInputData inputData = new ListSearchInputData(query);

    // Act
    interactor.search(inputData);

    // Assert
    assertTrue("Present results should be called", testPresenter.isPresentResultsCalled());
    assertFalse("Present error should not be called", testPresenter.isPresentErrorCalled());
    assertEquals("Should return 1 matching restaurant", 1,
        testPresenter.getOutputData().getFilteredRestaurants().size());
    assertEquals("Should match Pizza Parlor", "Pizza Parlor",
        testPresenter.getOutputData().getFilteredRestaurants().get(0).getName());
  }

  @Test
  public void testSearchWithEmptyQuery() {
    // Arrange
    TestRestaurantDataAccess testDataAccess = new TestRestaurantDataAccess();
    TestListSearchPresenter testPresenter = new TestListSearchPresenter();
    ListSearchInteractor interactor = new ListSearchInteractor(testDataAccess, testPresenter);

    String query = "";
    ListSearchInputData inputData = new ListSearchInputData(query);

    // Act
    interactor.search(inputData);

    // Assert
    assertTrue("Present results should be called", testPresenter.isPresentResultsCalled());
    assertFalse("Present error should not be called", testPresenter.isPresentErrorCalled());
    assertEquals("Should return all restaurants for empty query", 5,
        testPresenter.getOutputData().getFilteredRestaurants().size());
  }

  @Test
  public void testSearchWithNoMatches() {
    // Arrange
    TestRestaurantDataAccess testDataAccess = new TestRestaurantDataAccess();
    TestListSearchPresenter testPresenter = new TestListSearchPresenter();
    ListSearchInteractor interactor = new ListSearchInteractor(testDataAccess, testPresenter);

    String query = "Nonexistent";
    ListSearchInputData inputData = new ListSearchInputData(query);

    // Act
    interactor.search(inputData);

    // Assert
    assertTrue("Present results should be called", testPresenter.isPresentResultsCalled());
    assertFalse("Present error should not be called", testPresenter.isPresentErrorCalled());
    assertEquals("Should return empty list for no matches", 0,
        testPresenter.getOutputData().getFilteredRestaurants().size());
  }

  @Test
  public void testSearchCaseInsensitive() {
    // Arrange
    TestRestaurantDataAccess testDataAccess = new TestRestaurantDataAccess();
    TestListSearchPresenter testPresenter = new TestListSearchPresenter();
    ListSearchInteractor interactor = new ListSearchInteractor(testDataAccess, testPresenter);

    String query = "sushi";  // lowercase
    ListSearchInputData inputData = new ListSearchInputData(query);

    // Act
    interactor.search(inputData);

    // Assert
    assertTrue("Present results should be called", testPresenter.isPresentResultsCalled());
    assertEquals("Should match case-insensitively", 1,
        testPresenter.getOutputData().getFilteredRestaurants().size());
    assertEquals("Should match Sushi Place", "Sushi Place",
        testPresenter.getOutputData().getFilteredRestaurants().get(0).getName());
  }

  @Test
  public void testSearchSortsByRatingToReviewRatio() {
    // Arrange
    TestRestaurantDataAccess testDataAccess = new TestRestaurantDataAccess();
    TestListSearchPresenter testPresenter = new TestListSearchPresenter();
    ListSearchInteractor interactor = new ListSearchInteractor(testDataAccess, testPresenter);

    String query = "P";  // matches Pasta Palace and Pizza Parlor
    ListSearchInputData inputData = new ListSearchInputData(query);

    // Act
    interactor.search(inputData);

    // Assert
    assertTrue("Present results should be called", testPresenter.isPresentResultsCalled());
    List<Restaurant> results = testPresenter.getOutputData().getFilteredRestaurants();
    assertEquals("Should return 2 restaurants starting with P", 2, results.size());

    // Pizza Parlor has rating 5.0/10 = 0.5, Pasta Palace has 4.0/100 = 0.04
    // Pizza Parlor should be first (higher ratio)
    assertEquals("First result should be Pizza Parlor (higher ratio)", "Pizza Parlor",
        results.get(0).getName());
    assertEquals("Second result should be Pasta Palace (lower ratio)", "Pasta Palace",
        results.get(1).getName());
  }

  @Test
  public void testSearchWithException() {
    // Arrange
    TestRestaurantDataAccess testDataAccess = new TestRestaurantDataAccess();
    testDataAccess.setShouldThrowException(true);
    TestListSearchPresenter testPresenter = new TestListSearchPresenter();
    ListSearchInteractor interactor = new ListSearchInteractor(testDataAccess, testPresenter);

    ListSearchInputData inputData = new ListSearchInputData("Any Query");

    // Act
    interactor.search(inputData);

    // Assert
    assertTrue("Present error should be called when exception occurs",
        testPresenter.isPresentErrorCalled());
    assertFalse("Present results should not be called when exception occurs",
        testPresenter.isPresentResultsCalled());
    assertTrue("Error message should indicate search error",
        testPresenter.getErrorMessage().contains("Error searching restaurants"));
  }

  @Test
  public void testSearchLimitsTo300Restaurants() {
    // Arrange
    TestRestaurantDataAccess testDataAccess = new TestRestaurantDataAccess();
    testDataAccess.setGenerateLargeDataset(true);  // generates 400 restaurants
    TestListSearchPresenter testPresenter = new TestListSearchPresenter();
    ListSearchInteractor interactor = new ListSearchInteractor(testDataAccess, testPresenter);

    String query = "";  // empty query to get all
    ListSearchInputData inputData = new ListSearchInputData(query);

    // Act
    interactor.search(inputData);

    // Assert
    assertTrue("Present results should be called", testPresenter.isPresentResultsCalled());
    assertEquals("Should limit results to 300 restaurants", 300,
        testPresenter.getOutputData().getFilteredRestaurants().size());
  }

  // ==================== Test Doubles ====================

  /**
   * Test double for IRestaurantDataAccess
   */
  private static class TestRestaurantDataAccess implements IRestaurantDataAccess {

    private boolean shouldThrowException = false;
    private boolean generateLargeDataset = false;

    public void setShouldThrowException(boolean shouldThrow) {
      this.shouldThrowException = shouldThrow;
    }

    public void setGenerateLargeDataset(boolean generate) {
      this.generateLargeDataset = generate;
    }

    @Override
    public List<Restaurant> getRestaurantsByType(String type) {
      return new ArrayList<>();  // Not used in ListSearchInteractor
    }

    @Override
    public String[] getAllRestaurantTypes() {
      return new String[0];  // Not used in ListSearchInteractor
    }

    @Override
    public List<Restaurant> getAllRestaurants() {
      if (shouldThrowException) {
        throw new RuntimeException("Test exception");
      }

      List<Restaurant> restaurants = new ArrayList<>();

      if (generateLargeDataset) {
        // Generate 400 restaurants for limit testing
        for (int i = 1; i <= 400; i++) {
          restaurants.add(createRestaurant("R" + i, "Restaurant " + i, "Test Type", 4.0, 100));
        }
      } else {
        // Standard test dataset
        restaurants.add(createRestaurant("R1", "Pasta Palace", "Italian Restaurant", 4.0, 100));
        restaurants.add(createRestaurant("R2", "Pizza Parlor", "Italian Restaurant", 5.0, 10));
        restaurants.add(createRestaurant("R3", "Trattoria", "Italian Restaurant", 4.5, 50));
        restaurants.add(createRestaurant("J1", "Sushi Place", "Japanese Restaurant", 4.8, 80));
        restaurants.add(createRestaurant("J2", "Ramen House", "Japanese Restaurant", 4.2, 60));
      }

      return restaurants;
    }

    private Restaurant createRestaurant(String id, String name, String type, double rating,
        int ratingCount) {
      return new Restaurant.Builder()
          .id(id)
          .name(name)
          .location("123 Main St", "http://maps.google.com", 43.0, -79.0)
          .type(type)
          .rating(rating, ratingCount)
          .contact("416-123-4567", "http://example.com")
          .openingHours(List.of("Mon-Fri: 9AM-5PM"))
          .studentDiscount(false, 0.0)
          .photoIds(List.of("photo1"))
          .build();
    }
  }

  /**
   * Test double for ListSearchOutputBoundary
   */
  private static class TestListSearchPresenter implements ListSearchOutputBoundary {

    private boolean presentResultsCalled = false;
    private boolean presentErrorCalled = false;
    private ListSearchOutputData outputData;
    private String errorMessage;

    @Override
    public void presentResults(ListSearchOutputData outputData) {
      this.presentResultsCalled = true;
      this.outputData = outputData;
    }

    @Override
    public void presentError(String error) {
      this.presentErrorCalled = true;
      this.errorMessage = error;
    }

    public boolean isPresentResultsCalled() {
      return presentResultsCalled;
    }

    public boolean isPresentErrorCalled() {
      return presentErrorCalled;
    }

    public ListSearchOutputData getOutputData() {
      return outputData;
    }

    public String getErrorMessage() {
      return errorMessage;
    }
  }
}
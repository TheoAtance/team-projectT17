package use_case.filter;

import entity.Restaurant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Interactor for the filter use case.
 */
public class FilterInteractor implements FilterInputBoundary {

  private static final int MAX_RESTAURANTS = 10;
  private final IRestaurantDataAccess restaurantDataAccess;
  private final FilterOutputBoundary filterPresenter;

  public FilterInteractor(IRestaurantDataAccess restaurantDataAccess,
      FilterOutputBoundary filterPresenter) {
    this.restaurantDataAccess = restaurantDataAccess;
    this.filterPresenter = filterPresenter;
  }

  @Override
  public void execute(FilterInputData filterInputData) {
    try {
      String type = filterInputData.getRestaurantType();

      // Get restaurants of the specified type
      List<Restaurant> restaurants = restaurantDataAccess.getRestaurantsByType(type);

      // Limit to MAX_RESTAURANTS and extract names
      List<String> restaurantNames = restaurants.stream()
          .limit(MAX_RESTAURANTS)
          .map(Restaurant::getName)
          .collect(Collectors.toList());

      // Create output data and pass to presenter
      FilterOutputData outputData = new FilterOutputData(restaurantNames, type);
      filterPresenter.prepareSuccessView(outputData);

    } catch (Exception e) {
      filterPresenter.prepareFailView("Error filtering restaurants: " + e.getMessage());
    }
  }

  @Override
  public String[] getAvailableTypes() {
    return restaurantDataAccess.getAllRestaurantTypes();
  }
}
package interface_adapter.filter;

import use_case.filter.FilterInputBoundary;
import use_case.filter.FilterInputData;

/**
 * Controller for the filter use case.
 */
public class FilterController {

  private final FilterInputBoundary filterInteractor;

  public FilterController(FilterInputBoundary filterInteractor) {
    this.filterInteractor = filterInteractor;
  }

  /**
   * Execute the filter use case with the given restaurant type.
   *
   * @param restaurantType the type of restaurant to filter by
   */
  public void execute(String restaurantType) {
    FilterInputData inputData = new FilterInputData(restaurantType);
    filterInteractor.execute(inputData);
  }

  /**
   * Get all available restaurant types.
   *
   * @return array of unique restaurant types
   */
  public String[] getAvailableTypes() {
    return filterInteractor.getAvailableTypes();
  }
}
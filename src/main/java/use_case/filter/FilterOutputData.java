package use_case.filter;

import java.util.List;

/**
 * Output data for the filter use case.
 */
public class FilterOutputData {

  private final List<String> restaurantNames;
  private final String filterType;

  public FilterOutputData(List<String> restaurantNames, String filterType) {
    this.restaurantNames = restaurantNames;
    this.filterType = filterType;
  }

  public List<String> getRestaurantNames() {
    return restaurantNames;
  }

  public String getFilterType() {
    return filterType;
  }
}
package interface_adapter.filter;

import java.util.ArrayList;
import java.util.List;

/**
 * State for the Filter View.
 */
public class FilterState {

  private List<String> restaurantNames = new ArrayList<>();
  private String currentFilterType = "";
  private String errorMessage = "";

  public List<String> getRestaurantNames() {
    return restaurantNames;
  }

  public void setRestaurantNames(List<String> restaurantNames) {
    this.restaurantNames = restaurantNames;
  }

  public String getCurrentFilterType() {
    return currentFilterType;
  }

  public void setCurrentFilterType(String currentFilterType) {
    this.currentFilterType = currentFilterType;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }
}
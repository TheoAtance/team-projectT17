package use_case.view_restaurant;

import java.io.IOException;

/**
 * Input Boundary for actions which are related to viewing restaurant info
 */
public interface ViewRestaurantInputBoundary {

  /**
   * Executes the view restaurant use case
   *
   * @param viewRestaurantInputData input DTO containing input data
   */
  void execute(ViewRestaurantInputData viewRestaurantInputData) throws IOException;
}

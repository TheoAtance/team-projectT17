package use_case.view_restaurant;

import java.io.IOException;

/**
 * The output boundary for the view restaurant use case.
 */
public interface ViewRestaurantOutputBoundary {

  /**
   * Prepare the success view for the view restaurant use case
   *
   * @param outputData the output data
   */
  void prepareSuccessView(ViewRestaurantOutputData outputData) throws IOException;

  /**
   * Prepares the failure view for the view restaurant use case
   *
   * @param errorMessage the explanation for failure
   */
  void prepareFailView(String errorMessage);
}

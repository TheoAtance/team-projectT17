package use_case.display_reviews;

public interface DisplayReviewsInputBoundary {

  /**
   * Executes the display reviews use case
   *
   * @param displayReviewsInputData display reviews DTO containing input data
   */
  void execute(DisplayReviewsInputData displayReviewsInputData);
}

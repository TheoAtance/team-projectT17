package use_case.filter;

/**
 * Output boundary for the filter use case.
 */
public interface FilterOutputBoundary {

  /**
   * Prepares the success view with filtered restaurant data.
   *
   * @param outputData the output data containing filtered restaurants
   */
  void prepareSuccessView(FilterOutputData outputData);

  /**
   * Prepares the failure view with an error message.
   *
   * @param error the error message
   */
  void prepareFailView(String error);
}
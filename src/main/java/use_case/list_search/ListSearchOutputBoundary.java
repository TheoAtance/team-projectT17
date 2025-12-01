package use_case.list_search;

/**
 * Output boundary for the ListSearch use case.
 */
public interface ListSearchOutputBoundary {

  /**
   * Passes the search results to the presenter (or directly to the view)
   *
   * @param outputData list of restaurants matching the query
   */
  void presentResults(ListSearchOutputData outputData);

  /**
   * Optional: handle error case
   *
   * @param error message to show
   */
  void presentError(String error);
}

package use_case.filter;

/**
 * Input boundary for the filter use case.
 */
public interface FilterInputBoundary {

  /**
   * Execute the filter use case.
   *
   * @param filterInputData the input data containing filter criteria
   */
  void execute(FilterInputData filterInputData);

  /**
   * Get all available restaurant types for filtering.
   *
   * @return array of unique restaurant types
   */
  String[] getAvailableTypes();
}
package use_case.list_search;

/**
 * Input data for the ListSearch use case. Carries the search query from the view to the
 * interactor.
 */
public class ListSearchInputData {

  private final String query;

  public ListSearchInputData(String query) {
    this.query = query;
  }

  /**
   * Returns the search query entered by the user.
   */
  public String getQuery() {
    return query;
  }
}

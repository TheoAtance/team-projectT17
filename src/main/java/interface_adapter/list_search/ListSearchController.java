package interface_adapter.list_search;

import use_case.list_search.ListSearchInputBoundary;
import use_case.list_search.ListSearchInputData;

/**
 * Controller for the ListSearch use case. Receives search queries from the UI and passes them to
 * the interactor.
 */
public class ListSearchController {

  private final ListSearchInputBoundary interactor;

  public ListSearchController(ListSearchInputBoundary interactor) {
    this.interactor = interactor;
  }

  /**
   * Trigger a search based on the user's query.
   *
   * @param query the text typed in the search bar
   */
  public void search(String query) {
    if (query == null) {
      query = "";
    }
    interactor.search(new ListSearchInputData(query));
  }
}
package interface_adapter.display_reviews;

import interface_adapter.ViewModel;

public class DisplayReviewsViewModel extends ViewModel<DisplayReviewsStateList> {

  public DisplayReviewsViewModel() {
    super("display reviews");
    setState(new DisplayReviewsStateList());
  }
}

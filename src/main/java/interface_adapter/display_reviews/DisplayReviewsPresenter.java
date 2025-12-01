package interface_adapter.display_reviews;

import java.util.ArrayList;
import java.util.List;
import use_case.display_reviews.DisplayReviewsOutputBoundary;
import use_case.display_reviews.DisplayReviewsOutputData;

public class DisplayReviewsPresenter implements DisplayReviewsOutputBoundary {

  private final DisplayReviewsViewModel displayReviewsViewModel;

  public DisplayReviewsPresenter(DisplayReviewsViewModel displayReviewsViewModel) {
    this.displayReviewsViewModel = displayReviewsViewModel;
  }

  @Override
  public void prepareSuccessView(List<DisplayReviewsOutputData> displayReviewsOutputDataList) {
    ArrayList<DisplayReviewsState> newStates = new ArrayList<>();

    for (DisplayReviewsOutputData data : displayReviewsOutputDataList) {
      DisplayReviewsState state = new DisplayReviewsState();

      state.setAuthorDisplayName(data.getAuthorDisplayName());
      state.setContent(data.getContent());
      state.setCreationDate(data.getCreationDate());

      newStates.add(state);
    }

    DisplayReviewsStateList displayReviewsStateList = new DisplayReviewsStateList();
    displayReviewsStateList.setDisplayReviewsStateList(newStates);

    displayReviewsViewModel.setState(displayReviewsStateList);
    displayReviewsViewModel.firePropertyChange("display reviews");
  }

  @Override
  public void prepareFailView(String errorMessage) {
    displayReviewsViewModel.getState().setErrorMessage(errorMessage);
  }


}

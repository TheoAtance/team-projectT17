package interface_adapter.display_reviews;

import use_case.display_reviews.DisplayReviewsInputBoundary;
import use_case.display_reviews.DisplayReviewsOutputBoundary;
import use_case.display_reviews.DisplayReviewsOutputData;

import java.util.ArrayList;
import java.util.List;

public class DisplayReviewsPresenter implements DisplayReviewsOutputBoundary {

   private DisplayReviewsViewModel displayReviewsViewModel;

   @Override
    public void prepareSuccessView(List<DisplayReviewsOutputData> displayReviewsOutputDataList){
       ArrayList<DisplayReviewsState> newStates = new ArrayList<>();

       for(DisplayReviewsOutputData data : displayReviewsOutputDataList){
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
    public void prepareFailView(String errorMessage){
       displayReviewsViewModel.getState().setErrorMessage(errorMessage);
   }


}

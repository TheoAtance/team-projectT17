package interface_adapter.add_review;

import interface_adapter.ViewManagerModel;
import use_case.add_review.AddReviewOutputBoundary;


public class AddReviewPresenter implements AddReviewOutputBoundary {

    private final ViewManagerModel viewManagerModel;
    private final AddReviewViewModel addReviewViewModel;

    public AddReviewPresenter(ViewManagerModel viewManagerModel, AddReviewViewModel addReviewViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.addReviewViewModel = addReviewViewModel;
    }

    @Override
    public void prepareSuccessView(String successMessage){

        addReviewViewModel.getState().setStatus(successMessage);
        addReviewViewModel.firePropertyChange("review status");
    }

    @Override
    public void prepareFailView(String errorMessage){
        addReviewViewModel.getState().setStatus(errorMessage);
        addReviewViewModel.firePropertyChange("review status");
    }
}

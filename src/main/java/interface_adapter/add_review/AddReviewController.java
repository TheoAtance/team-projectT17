package interface_adapter.add_review;

import use_case.add_review.AddReviewInputBoundary;

public class AddReviewController {
    private final AddReviewInputBoundary addReviewUseCaseInteractor;

    public AddReviewController(AddReviewInputBoundary addReviewUseCaseInteractor) {
        this.addReviewUseCaseInteractor = addReviewUseCaseInteractor;
    }

    //public void execute(String userId);
}

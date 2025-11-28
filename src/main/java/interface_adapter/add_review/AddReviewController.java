package interface_adapter.add_review;

import use_case.add_review.AddReviewInputBoundary;
import use_case.add_review.AddReviewInputData;

import java.io.IOException;

public class AddReviewController {
    private final AddReviewInputBoundary addReviewUseCaseInteractor;

    public AddReviewController(AddReviewInputBoundary addReviewUseCaseInteractor) {
        this.addReviewUseCaseInteractor = addReviewUseCaseInteractor;
    }

    public void execute(String restaurantId, String content) throws IOException {
        final AddReviewInputData addReviewInputData = new AddReviewInputData(restaurantId, content);
        addReviewUseCaseInteractor.execute(addReviewInputData);
    }
}

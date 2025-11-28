package interface_adapter.display_reviews;

import use_case.display_reviews.DisplayReviewsInputBoundary;
import use_case.display_reviews.DisplayReviewsInputData;
import use_case.display_reviews.DisplayReviewsInteractor;

import java.io.IOException;

public class DisplayReviewsController {
    private final DisplayReviewsInputBoundary displayReviewsInteractor;

    public DisplayReviewsController(DisplayReviewsInputBoundary displayReviewsInteractor) {
        this.displayReviewsInteractor = displayReviewsInteractor;
    }

    public void execute(String restaurantId){
        final DisplayReviewsInputData inputData = new DisplayReviewsInputData(restaurantId);
        displayReviewsInteractor.execute(inputData);
    }
}

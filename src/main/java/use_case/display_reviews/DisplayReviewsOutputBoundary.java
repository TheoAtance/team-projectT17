package use_case.display_reviews;
import java.util.List;

/**
 * The output boundary for the display reviews use case.
 */
public interface DisplayReviewsOutputBoundary {

    /**
     * Prepare the success view for the display reviews use case
     */
    void prepareSuccessView(List<DisplayReviewsOutputData> displayReviewsOutputDataList);


    /**
     * Prepare the fao; view for the display reviews use case
     */
    void prepareFailView(String errorMessage);
}

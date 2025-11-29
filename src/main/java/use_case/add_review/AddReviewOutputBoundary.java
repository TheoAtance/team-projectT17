package use_case.add_review;

/**
 * The output boundary for the view restaurant use case.
 */
public interface AddReviewOutputBoundary {

    /**
     * Prepare the success view for the view restaurant use case
     */
    void prepareSuccessView(String successMessage);

    /**
     * Prepares the failure view for the view restaurant use case
     * @param errorMessage the explanation for failure
     */
    void prepareFailView(String errorMessage);
}

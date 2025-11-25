package use_case.add_review;

import java.io.IOException;

public interface AddReviewInputBoundary {

    /**
     * Executes the add review use case
     * @param addReviewInputData add review DTO containing input data
     */
    void execute(AddReviewInputData addReviewInputData) throws IOException;
}

package use_case.add_review;

import java.io.IOException;

public interface AddReviewDataAccessInterface {

    /**
     * Adds a review to database
     * @param reviewId review id
     * @param userId user id
     * @param restaurantId restaurant id
     * @param content content of the review
     */

    void addReview(String reviewId, String userId, String restaurantId, String content, String creationDate) throws IOException;
}

//package use_case.add_review;
//
//import java.io.IOException;
//import java.util.UUID;
//
//public class AddReviewInteractor implements AddReviewInputBoundary{
//    private final AddReviewOutputBoundary addReviewPresenter;
//    private final AddReviewDataAccessInterface jsonReviewDataAccessObject;
//    private final
//
//    public AddReviewInteractor(AddReviewOutputBoundary addReviewPresenter, AddReviewDataAccessInterface jsonReviewDataAccessObject) {
//        this.addReviewPresenter = addReviewPresenter;
//        this.jsonReviewDataAccessObject = jsonReviewDataAccessObject;
//    }
//
//    @Override
//    public void execute(AddReviewInputData addReviewInputData) throws IOException {
//
//
//
//        // generate a random unique id
//        String reviewId = UUID.randomUUID().toString();
//
//        String userId = addReviewInputData.getUserId();
//        String restaurantId = addReviewInputData.getRestaurantId();
//        String content = addReviewInputData.getContent();
//
//        if (userId == null || userId.isEmpty()) {
//            addReviewPresenter.prepareFailView("User ID is missing.");
//            return;
//        }
//
//        if (restaurantId == null || restaurantId.isEmpty()) {
//            addReviewPresenter.prepareFailView("Restaurant ID is missing.");
//            return;
//        }
//
//        if (content == null || content.trim().isEmpty()) {
//            addReviewPresenter.prepareFailView("Cannot leave an empty review.");
//            return;
//        }
//
//        try {
//            jsonReviewDataAccessObject.addReview(reviewId, userId, restaurantId, content);
//            addReviewPresenter.prepareSuccessView("Review added!");   // no output data needed
//
//        } catch (IOException e) {
//            addReviewPresenter.prepareFailView("Failed to save review.");
//        }
//    }
//}

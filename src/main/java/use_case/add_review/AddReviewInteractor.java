package use_case.add_review;

import data_access.CurrentUser;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class AddReviewInteractor implements AddReviewInputBoundary{
    private final AddReviewOutputBoundary addReviewPresenter;
    private final AddReviewDataAccessInterface jsonReviewDataAccessObject;
    private final CurrentUser currentUser;

    public AddReviewInteractor(AddReviewOutputBoundary addReviewPresenter,
                               AddReviewDataAccessInterface jsonReviewDataAccessObject,
                               CurrentUser currentUser) {
        this.addReviewPresenter = addReviewPresenter;
        this.jsonReviewDataAccessObject = jsonReviewDataAccessObject;
        this.currentUser = currentUser;
    }

    @Override
    public void execute(AddReviewInputData addReviewInputData) throws IOException {

        // generate a random unique id
        String reviewId = UUID.randomUUID().toString();

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        String date = today.format(formatter);

        String userId = currentUser.getCurrentUser().getUid();
        String restaurantId = addReviewInputData.getRestaurantId();
        String content = addReviewInputData.getContent()
                .replaceAll("\\R", " ")
                .replaceAll("\\s+", " ")
                .trim();

        if (userId == null || userId.isEmpty()) {
            addReviewPresenter.prepareFailView("User ID is missing.");
            return;
        }

        if (restaurantId == null || restaurantId.isEmpty()) {
            addReviewPresenter.prepareFailView("Restaurant ID is missing.");
            return;
        }

        if (content == null || content.trim().isEmpty()) {
            addReviewPresenter.prepareFailView("Cannot leave an empty review.");
            return;
        }

        try {
            jsonReviewDataAccessObject.addReview(reviewId, userId, restaurantId, date, content);
            addReviewPresenter.prepareSuccessView("Review added!");   // no output data needed

        } catch (IOException e) {
            addReviewPresenter.prepareFailView("Failed to save review.");
        }
    }
}

package use_case.display_reviews;

import data_access.FirestoreUserRepo;
import data_access.JsonReviewDataAccessObject;
import entity.Review;
import use_case.IUserRepo;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DisplayReviewsInteractor implements DisplayReviewsInputBoundary{
    private final JsonReviewDataAccessObject jsonReviewDataAccessObject;
    private final DisplayReviewsOutputBoundary displayReviewsPresenter;
    private final IUserRepo firestoreUserRepo;

    public DisplayReviewsInteractor(JsonReviewDataAccessObject jsonReviewDataAccessObject,
                                    DisplayReviewsOutputBoundary displayReviewsPresenter,
                                    IUserRepo firestoreUserRepo) {
        this.jsonReviewDataAccessObject = jsonReviewDataAccessObject;
        this.displayReviewsPresenter = displayReviewsPresenter;
        this.firestoreUserRepo = firestoreUserRepo;
    }

    @Override
    public void execute(DisplayReviewsInputData displayReviewsInputData){

        String restaurantId = displayReviewsInputData.getRestaurantId();
        Map<String, Review> reviews = jsonReviewDataAccessObject.reviewByRestaurant(restaurantId);
        ArrayList<DisplayReviewsOutputData> outputDataList = new ArrayList<>();

        for(Review review : reviews.values()){

            if(review.getReviewId().isEmpty()){
                displayReviewsPresenter.prepareFailView("ReviewId missing");
            }

            if(review.getContent().isEmpty()){
                displayReviewsPresenter.prepareFailView("Empty review in database");
            }

            if(review.getAuthorId() == null || review.getAuthorId().isEmpty()){
                displayReviewsPresenter.prepareFailView("User not found for review: " + review.getReviewId());
            }

            else{

                String authorDisplayName = firestoreUserRepo.getUserByUid(review.getAuthorId()).getNickname();
                String content = review.getContent();
                String creationDate = review.getCreationDate();


                DisplayReviewsOutputData displayReviewsOutputData = new DisplayReviewsOutputData(
                        authorDisplayName,
                        content,
                        creationDate
                );


                outputDataList.add(displayReviewsOutputData);
            }
        }
        displayReviewsPresenter.prepareSuccessView(outputDataList);
    }
}

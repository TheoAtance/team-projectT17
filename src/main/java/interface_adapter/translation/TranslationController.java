package interface_adapter.translation;

import entity.Review;
import use_case.translation.TranslationInputBoundary;
import use_case.translation.TranslationInputData;

import java.util.ArrayList;
import java.util.List;

public class TranslationController {

    private final TranslationInputBoundary translationInteractor;

    public TranslationController(TranslationInputBoundary translationInteractor) {
        this.translationInteractor = translationInteractor;
    }

    // helper: translate one review
    public void translateOneReview(Review review, String targetLanguage) {
        List<Review> reviews = new ArrayList<Review>();
        reviews.add(review);

        TranslationInputData inputData =
                new TranslationInputData(reviews, targetLanguage);
        translationInteractor.execute(inputData);
    }
}
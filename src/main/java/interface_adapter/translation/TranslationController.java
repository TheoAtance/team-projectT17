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

    // translate a list of reviews
    public void execute(List<Review> reviews, String targetLanguage) {
        TranslationInputData inputData =
                new TranslationInputData(reviews, targetLanguage);
        translationInteractor.execute(inputData);
    }

    // translate a single review
    public void translateOneReview(Review review, String targetLanguage) {
        List<Review> reviews = new ArrayList<>();
        reviews.add(review);
        execute(reviews, targetLanguage);
    }
}

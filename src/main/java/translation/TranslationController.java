package translation;

import entity.Review;
import java.util.List;
import use_case.translation.TranslationInputBoundary;
import use_case.translation.TranslationInputData;

/**
 * Controller for the translation use case.
 * Adapts UI input into TranslationInputData and invokes the interactor.
 */
public class TranslationController {

    private final TranslationInputBoundary translationInputBoundary;

    public TranslationController(TranslationInputBoundary translationInputBoundary) {
        this.translationInputBoundary = translationInputBoundary;
    }

    /**
     * Request translation of the given reviews into the target language code
     * (e.g., "EN", "FR", "JA").
     */
    public void translate(List<Review> reviews, String targetLanguage) {
        TranslationInputData inputData = new TranslationInputData(reviews, targetLanguage);
        translationInputBoundary.execute(inputData);
    }
}

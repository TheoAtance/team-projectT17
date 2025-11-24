package interface_adapter.translation;

import entity.Review;
import use_case.translation.TranslationInputBoundary;
import use_case.translation.TranslationInputData;

import java.util.List;

/**
 * Controller for the translation use case.
 * Called by the UI to start a translation.
 */
public class TranslationController {

    private final TranslationInputBoundary translationInteractor;

    public TranslationController(TranslationInputBoundary translationInteractor) {
        this.translationInteractor = translationInteractor;
    }

    /**
     * Trigger the translation use case.
     *
     * @param reviews        list of reviews to translate
     * @param targetLanguage target language code (e.g., "EN", "FR")
     */
    public void execute(List<Review> reviews, String targetLanguage) {
        TranslationInputData inputData =
                new TranslationInputData(reviews, targetLanguage);
        translationInteractor.execute(inputData);
    }
}

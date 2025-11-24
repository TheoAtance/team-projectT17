package use_case.translation;
import entity.Review;
import java.util.List;

/**
 * Input data for the translation use case.
 * Contains the reviews to translate and the target language code (e.g. "EN", "FR").
 */
public class TranslationInputData {
    private final List<Review> reviews;
    private final String targetLanguage;

    public TranslationInputData(List<Review> reviews, String targetLanguage) {
        this.reviews = reviews;
        this.targetLanguage = targetLanguage;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public String getTargetLanguage() {
        return targetLanguage;
    }
}

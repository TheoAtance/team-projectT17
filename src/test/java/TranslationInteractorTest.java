import entity.Review;
import org.junit.jupiter.api.Test;
import use_case.translation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TranslationInteractorTest {

    // Fake TranslationService for testing
    private static class FakeTranslationService implements TranslationService {

        boolean throwOnCall = false;

        @Override
        public List<TranslatedText> translateTexts(List<String> texts, String targetLanguage) {
            if (throwOnCall) {
                throw new RuntimeException("Service down");
            }

            List<TranslatedText> result = new ArrayList<>();
            for (String text : texts) {
                result.add(new TranslatedText("translated: " + text, "XX"));
            }
            return result;
        }
    }

    // Presenter that just captures the output
    private static class CapturingOutputBoundary implements TranslationOutputBoundary {
        TranslationOutputData lastOutput;

        @Override
        public void present(TranslationOutputData outputData) {
            lastOutput = outputData;
        }
    }

    @Test
    void testSuccessfulTranslation() {
        FakeTranslationService service = new FakeTranslationService();
        CapturingOutputBoundary presenter = new CapturingOutputBoundary();
        TranslationInteractor interactor = new TranslationInteractor(service, presenter);

        Review review = new Review("Alice", "121", "Best pasta in this area", 2);

        List<Review> reviews = new ArrayList<>();
        reviews.add(review);

        TranslationInputData inputData = new TranslationInputData(reviews, "EN");

        interactor.execute(inputData);

        assertNotNull(presenter.lastOutput);
        assertFalse(presenter.lastOutput.isError());
        assertEquals("EN", presenter.lastOutput.getTargetLanguage());
        assertEquals(
                Collections.singletonList("translated: " + review.getContent()),
                presenter.lastOutput.getTranslatedContents()
        );
        assertNull(presenter.lastOutput.getErrorMessage());
    }
}

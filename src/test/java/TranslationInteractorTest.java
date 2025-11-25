import entity.Review;
import org.testng.annotations.Test;
import use_case.translation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.testng.AssertJUnit.*;

/**
 * Unit tests for TranslationInteractor.
 *
 * NOTE: You must adjust how the Review object is created
 *       to match your actual Review class constructor.
 */
public class TranslationInteractorTest {

    /**
     * Fake TranslationService that returns predictable data.
     */
    private static class FakeTranslationService implements TranslationService {

        boolean throwOnCall = false;

        @Override
        public List<TranslatedText> translateTexts(List<String> texts, String targetLanguage) {
            if (throwOnCall) {
                throw new RuntimeException("Service down");
            }

            List<TranslatedText> result = new ArrayList<TranslatedText>();
            for (String text : texts) {
                // Pretend the API translated it by adding a prefix
                result.add(new TranslatedText("translated: " + text, "XX"));
            }
            return result;
        }
    }

    /**
     * Captures the last output passed to the presenter.
     */
    private static class CapturingOutputBoundary implements TranslationOutputBoundary {
        TranslationOutputData lastOutput;

        @Override
        public void present(TranslationOutputData outputData) {
            this.lastOutput = outputData;
        }
    }

    @Test
    public void testSuccessfulTranslation() {
        // Arrange
        FakeTranslationService service = new FakeTranslationService();
        CapturingOutputBoundary presenter = new CapturingOutputBoundary();
        TranslationInteractor interactor = new TranslationInteractor(service, presenter);

        // Example if you have (String user, String content, int rating, ...):
        Review review = new Review("alice", "1002", "Best noodle in this area", 2);

        List<Review> reviews = new ArrayList<Review>();
        reviews.add(review);

        TranslationInputData inputData =
                new TranslationInputData(reviews, "EN");

        // Act
        interactor.execute(inputData);

        // Assert
        assertNotNull("Presenter should have been called", presenter.lastOutput);
        assertFalse("Should not be an error", presenter.lastOutput.isError());
        assertEquals("EN", presenter.lastOutput.getTargetLanguage());
        assertEquals(Collections.singletonList("translated: " + review.getContent()),
                presenter.lastOutput.getTranslatedContents());
        assertNull(presenter.lastOutput.getErrorMessage());
    }

    @Test
    public void testNoReviewsProducesError() {
        // Arrange
        FakeTranslationService service = new FakeTranslationService();
        CapturingOutputBoundary presenter = new CapturingOutputBoundary();
        TranslationInteractor interactor = new TranslationInteractor(service, presenter);

        TranslationInputData inputData =
                new TranslationInputData(Collections.<Review>emptyList(), "EN");

        // Act
        interactor.execute(inputData);

        // Assert
        assertNotNull(presenter.lastOutput);
        assertTrue("Error when no reviews", presenter.lastOutput.isError());
        assertEquals("EN", presenter.lastOutput.getTargetLanguage());
        assertTrue(presenter.lastOutput.getErrorMessage().toLowerCase().contains("no reviews"));
    }

    @Test
    public void testServiceExceptionProducesError() {
        // Arrange
        FakeTranslationService service = new FakeTranslationService();
        service.throwOnCall = true;  // make it throw

        CapturingOutputBoundary presenter = new CapturingOutputBoundary();
        TranslationInteractor interactor = new TranslationInteractor(service, presenter);

        // TODO: adjust Review constructor here too
        Review review = new Review("bob", "105", "Service are good but with bad environment", 7);
        List<Review> reviews = new ArrayList<Review>();
        reviews.add(review);

        TranslationInputData inputData =
                new TranslationInputData(reviews, "EN");

        // Act
        interactor.execute(inputData);

        // Assert
        assertNotNull(presenter.lastOutput);
        assertTrue("Should be an error when service throws", presenter.lastOutput.isError());
        assertEquals("EN", presenter.lastOutput.getTargetLanguage());
        assertNotNull("Error message should be set", presenter.lastOutput.getErrorMessage());
    }
}

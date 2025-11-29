import entity.Review;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import use_case.translation.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for TranslationInteractor.
 */
class TranslationInteractorTest {

    /** Presenter that just remembers the last output it was given. */
    private static class RecordingPresenter implements TranslationOutputBoundary {
        TranslationOutputData lastOutput;

        @Override
        public void present(TranslationOutputData outputData) {
            this.lastOutput = outputData;
        }
    }

    /** Fake translation service for unit tests (no network). */
    private static class FakeTranslationService implements TranslationService {
        List<String> lastTexts = new ArrayList<>();
        String lastTargetLanguage;

        @Override
        public List<TranslatedText> translateTexts(List<String> texts, String targetLanguage)
                throws Exception {
            lastTexts = new ArrayList<>(texts);
            lastTargetLanguage = targetLanguage;

            List<TranslatedText> out = new ArrayList<>();
            for (String t : texts) {
                out.add(new TranslatedText("X:" + t, "en"));
            }
            return out;
        }
    }

    /* ------------ Helper to build a Review ------------ */

    private Review makeReview(String content) {
        return new Review("review001","user101", "rest222", content,"date");
    }

    /* ------------ Unit tests (no API calls) ------------ */

    @Test
    void noReviews_returnsErrorAndDoesNotCallService() {
        FakeTranslationService fakeService = new FakeTranslationService();
        RecordingPresenter presenter = new RecordingPresenter();
        TranslationInteractor interactor = new TranslationInteractor(fakeService, presenter);

        TranslationInputData input =
                new TranslationInputData(List.of(), "FR"); // (List<Review>, String)

        interactor.execute(input);

        assertNotNull(presenter.lastOutput, "Presenter should have been called");

        TranslationOutputData out = presenter.lastOutput;

        assertTrue(out.isError());
        assertEquals("FR", out.getTargetLanguage());
        assertEquals("No reviews to translate.", out.getErrorMessage());

        // Service should NOT have been called
        assertTrue(fakeService.lastTexts.isEmpty());
        assertNull(fakeService.lastTargetLanguage);
    }

    @Test
    void happyPath_usesServiceAndMapsOutputCorrectly() {
        FakeTranslationService fakeService = new FakeTranslationService();
        RecordingPresenter presenter = new RecordingPresenter();
        TranslationInteractor interactor = new TranslationInteractor(fakeService, presenter);

        Review r1 = makeReview("Hello");
        Review r2 = makeReview("Goodbye");

        TranslationInputData input =
                new TranslationInputData(List.of(r1, r2), "EN");

        interactor.execute(input);

        TranslationOutputData out = presenter.lastOutput;
        assertNotNull(out);

        assertFalse(out.isError());
        assertEquals("EN", out.getTargetLanguage());

        List<String> translated = out.getTranslatedContents();
        assertEquals(2, translated.size());
        assertEquals("X:Hello", translated.get(0));
        assertEquals("X:Goodbye", translated.get(1));

        // Service saw the original texts and target language
        assertEquals(List.of("Hello", "Goodbye"), fakeService.lastTexts);
        assertEquals("EN", fakeService.lastTargetLanguage);
    }

    /* ------------ Integration test with real DeepL ------------ */

    /**
     * This test calls the REAL DeepL API through the interactor,
     * and prints exact outputs to the console so you can see them.
     * It is automatically SKIPPED if DEEPL_API_KEY is not set.
     */
    @Test
    void helloWorld_realDeepL_printsExactOutput() throws Exception {
        String key = System.getenv("DEEPL_API_KEY");
        Assumptions.assumeTrue(key != null && !key.isBlank(),
                "DEEPL_API_KEY not set; skipping DeepL integration test");

        TranslationService realService = new DeeplTranslationService(key, false);
        RecordingPresenter presenter = new RecordingPresenter();
        TranslationInteractor interactor = new TranslationInteractor(realService, presenter);

        Review review = makeReview("Hello, world!");
        TranslationInputData input =
                new TranslationInputData(List.of(review), "FR");

        interactor.execute(input);

        TranslationOutputData out = presenter.lastOutput;
        assertNotNull(out);
        assertFalse(out.isError());
        assertEquals("FR", out.getTargetLanguage());

        List<String> translated = out.getTranslatedContents();
        assertEquals(1, translated.size());

        String fr = translated.get(0);

        // Print exact output
        System.out.println("=== DeepL example ===");
        System.out.println("Original : Hello, world!");
        System.out.println("Translated (FR): " + fr);
        System.out.println("=====================");
    }
}

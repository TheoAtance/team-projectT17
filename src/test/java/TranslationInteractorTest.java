import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import entity.Review;
import java.io.BufferedReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import use_case.translation.DeeplTranslationService;
import use_case.translation.TranslationInputData;
import use_case.translation.TranslationInteractor;
import use_case.translation.TranslationOutputBoundary;
import use_case.translation.TranslationOutputData;
import use_case.translation.TranslationService;

/**
 * Tests for TranslationInteractor.
 */
class TranslationInteractorTest {

  /**
   * Load all reviews from the JSON file at src/main/java/data/reviews.json and convert them to
   * Review entities.
   */
  private List<Review> loadReviewsFromJson() {
    try {
      Path path = Path.of("src/main/java/data/reviews.json");
      try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<ReviewDto>>() {
        }.getType();
        List<ReviewDto> dtos = gson.fromJson(reader, listType);

        List<Review> reviews = new ArrayList<>();
        for (ReviewDto dto : dtos) {
          // Use the 6-arg constructor:
          // (reviewId, authorId, restaurantId, content, creationDate, likes)
          reviews.add(new Review(
              dto.reviewId,
              dto.authorId,
              dto.restaurantId,
              dto.content,
              dto.creationDate,
              dto.likes
          ));
        }
        return reviews;
      }
    } catch (Exception e) {
      throw new RuntimeException("Failed to load src/main/java/data/reviews.json", e);
    }
  }

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

  /* ------------ Helpers to load Reviews from src/main/java/data/reviews.json ------------ */

  @Test
  void happyPath_usesServiceAndMapsOutputCorrectly() {
    FakeTranslationService fakeService = new FakeTranslationService();
    RecordingPresenter presenter = new RecordingPresenter();
    TranslationInteractor interactor = new TranslationInteractor(fakeService, presenter);

    // Use real-looking reviews loaded from src/main/java/data/reviews.json
    List<Review> allReviews = loadReviewsFromJson();
    // Just take the first two for this test
    Review r1 = allReviews.get(0);
    Review r2 = allReviews.get(1);

    TranslationInputData input =
        new TranslationInputData(List.of(r1, r2), "EN");

    interactor.execute(input);

    TranslationOutputData out = presenter.lastOutput;
    assertNotNull(out);

    assertFalse(out.isError());
    assertEquals("EN", out.getTargetLanguage());

    List<String> translated = out.getTranslatedContents();
    assertEquals(2, translated.size());
    assertEquals("X:" + r1.getContent(), translated.get(0));
    assertEquals("X:" + r2.getContent(), translated.get(1));

    // Service saw the original texts and target language
    assertEquals(List.of(r1.getContent(), r2.getContent()), fakeService.lastTexts);
    assertEquals("EN", fakeService.lastTargetLanguage);
  }

  /**
   * This test calls the REAL DeepL API through the interactor, and prints exact outputs to the
   * console so you can see them. It is automatically SKIPPED if DEEPL_API_KEY is not set.
   */
  @Test
  void helloWorld_realDeepL_printsExactOutput() throws Exception {
    String key = System.getenv("DEEPL_API_KEY");
    Assumptions.assumeTrue(key != null && !key.isBlank(),
        "DEEPL_API_KEY not set; skipping DeepL integration test");

    TranslationService realService = new DeeplTranslationService(key, false);
    RecordingPresenter presenter = new RecordingPresenter();
    TranslationInteractor interactor = new TranslationInteractor(realService, presenter);

    // Simple one-off Review for integration test
    Review review = new Review(
        "test-review-id",
        "test-author",
        "test-restaurant",
        "Hello, world!",
        "2025-01-01",
        0
    );

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

  /* ------------ Unit tests (no API calls) ------------ */

  /**
   * Presenter that just remembers the last output it was given.
   */
  private static class RecordingPresenter implements TranslationOutputBoundary {

    TranslationOutputData lastOutput;

    @Override
    public void present(TranslationOutputData outputData) {
      this.lastOutput = outputData;
    }
  }

  /**
   * Fake translation service for unit tests (no network).
   */
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

  /* ------------ Integration test with real DeepL ------------ */

  /**
   * DTO matching the JSON structure in reviews.json.
   */
  private static class ReviewDto {

    String creationDate;
    String restaurantId;
    String authorId;
    String reviewId;
    String content;
    int likes;
  }
}

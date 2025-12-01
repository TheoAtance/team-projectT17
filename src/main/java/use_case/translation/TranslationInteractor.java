package use_case.translation;

import entity.Review;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Interactor for the translation use case.
 * Takes reviews + target language, calls TranslationService,
 * and passes a TranslationOutputData to the output boundary.
 */
public class TranslationInteractor implements TranslationInputBoundary {

  private final TranslationService translationService;
  private final TranslationOutputBoundary outputBoundary;

  public TranslationInteractor(TranslationService translationService,
                               TranslationOutputBoundary outputBoundary) {
    this.translationService = translationService;
    this.outputBoundary = outputBoundary;
  }

  @Override
  public void execute(TranslationInputData inputData) {
    List<Review> reviews = inputData.getReviews();
    String targetLanguage = inputData.getTargetLanguage();

    // Validation
    if (reviews == null || reviews.isEmpty()) {
      outputBoundary.present(new TranslationOutputData(
              Collections.emptyList(),
              targetLanguage,
              true,
              "No reviews to translate."
      ));
      return;
    }

    try {
      // Extract review contents
      List<String> texts = reviews.stream()
              .map(Review::getContent)
              .collect(Collectors.toList());

      // Translate using TranslationService
      List<TranslationService.TranslatedText> translated =
              translationService.translateTexts(texts, targetLanguage);

      // Collect translated strings
      List<String> translatedContents = translated.stream()
              .map(TranslationService.TranslatedText::translated)
              .collect(Collectors.toList());

      // Build and send output
      outputBoundary.present(new TranslationOutputData(
              translatedContents,
              targetLanguage,
              false,
              null
      ));

    } catch (Exception e) {
      // Handle errors
      String msg = (e.getMessage() == null || e.getMessage().isBlank())
              ? "An error occurred during translation."
              : e.getMessage();

      outputBoundary.present(new TranslationOutputData(
              null,
              targetLanguage,
              true,
              msg
      ));
    }
  }
}

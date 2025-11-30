package use_case.translation;

import entity.Review;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Interactor for the translation use case. Takes reviews + target language, calls
 * TranslationService, and passes a TranslationOutputData to the output boundary.
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

    // Basic validation
    if (reviews == null || reviews.isEmpty()) {
      TranslationOutputData outputData = new TranslationOutputData(
          Collections.emptyList(),
          targetLanguage,
          true,
          "No reviews to translate."
      );
      outputBoundary.present(outputData);
      return;
    }

    try {
      // extract raw text from reviews
      List<String> texts = reviews.stream()
          .map(Review::getContent)
          .collect(Collectors.toList());

      // call the translation service
      List<TranslationService.TranslatedText> translated =
          translationService.translateTexts(texts, targetLanguage);

      // translated strings
      List<String> translatedContents = translated.stream()
          .map(TranslationService.TranslatedText::translated)
          .collect(Collectors.toList());

      // output data
      TranslationOutputData outputData = new TranslationOutputData(
          translatedContents,
          targetLanguage,
          false,
          null
      );
      outputBoundary.present(outputData);

    } catch (Exception e) {
      String msg = (e.getMessage() == null || e.getMessage().isBlank())
          ? "An error occurred during translation."
          : e.getMessage();

      TranslationOutputData outputData = new TranslationOutputData(
          null,
          targetLanguage,
          true,
          msg
      );
      outputBoundary.present(outputData);
    }
  }
}

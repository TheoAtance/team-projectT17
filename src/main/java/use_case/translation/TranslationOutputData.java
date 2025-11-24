package use_case.translation;

import java.util.List;

/**
 * Output data for the translation use case.
 */
public class TranslationOutputData {
    private final List<String> translatedContents;
    private final String targetLanguage;
    private final boolean error;
    private final String errorMessage;

    public TranslationOutputData(List<String> translatedContents,
                                 String targetLanguage,
                                 boolean error,
                                 String errorMessage) {
        this.translatedContents = translatedContents;
        this.targetLanguage = targetLanguage;
        this.error = error;
        this.errorMessage = errorMessage;
    }

    public List<String> getTranslatedContents() {
        return translatedContents;
    }

    public String getTargetLanguage() {
        return targetLanguage;
    }

    public boolean isError() {
        return error;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
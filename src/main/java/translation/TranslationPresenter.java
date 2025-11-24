package translation;

import java.util.Collections;
import java.util.List;
import use_case.translation.TranslationOutputBoundary;
import use_case.translation.TranslationOutputData;

/**
 * Presenter for the translation use case.
 * Stores the latest output so that a view / view model can read it.
 */
public class TranslationPresenter implements TranslationOutputBoundary {

    private List<String> translatedContents = Collections.emptyList();
    private String targetLanguage = "";
    private boolean error = false;
    private String errorMessage = "";

    @Override
    public void present(TranslationOutputData outputData) {
        this.translatedContents = outputData.getTranslatedContents();
        this.targetLanguage = outputData.getTargetLanguage();
        this.error = outputData.isError();
        this.errorMessage = outputData.getErrorMessage();
        // If you have a ViewModel, you would update it here instead of / in addition to storing fields.
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

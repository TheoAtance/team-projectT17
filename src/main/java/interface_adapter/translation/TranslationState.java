package interface_adapter.translation;

import java.util.List;

/**
 * State object for the translation view.
 */
public class TranslationState {
    private List<String> translatedContents;
    private String targetLanguage;
    private String errorMessage;

    public List<String> getTranslatedContents() {
        return translatedContents;
    }

    public void setTranslatedContents(List<String> translatedContents) {
        this.translatedContents = translatedContents;
    }

    public String getTargetLanguage() {
        return targetLanguage;
    }

    public void setTargetLanguage(String targetLanguage) {
        this.targetLanguage = targetLanguage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}

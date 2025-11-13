package translation;

import java.util.List;


public interface TranslationService {
    List<TranslatedText> translateTexts(List<String> texts, String targetLanguage) throws Exception;

    final class TranslatedText {
        private final String translated;
        private final String detectedSourceLanguage;
        public TranslatedText(String translated, String detectedSourceLanguage) {
            this.translated = translated; this.detectedSourceLanguage = detectedSourceLanguage;
        }
        public String translated() { return translated; }
        public String detectedSourceLanguage() { return detectedSourceLanguage; }
    }
}


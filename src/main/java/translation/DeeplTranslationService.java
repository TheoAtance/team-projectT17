package translation;

import com.deepl.api.*;
import java.util.ArrayList;
import java.util.List;

public final class DeeplTranslationService implements TranslationService {
    private final Translator translator;
    private final boolean htmlMode;

    public DeeplTranslationService(String apiKey, boolean htmlMode) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("missing DEEPL_API_KEY");
        }
        this.translator = new Translator(apiKey);
        this.htmlMode = htmlMode;
    }

    // only for test
    DeeplTranslationService(Translator translator, boolean htmlMode) {
        this.translator = translator;
        this.htmlMode = htmlMode;
    }

    @Override
    public List<TranslatedText> translateTexts(List<String> texts, String targetLanguage)
            throws DeepLException, InterruptedException {

        if (texts == null || texts.isEmpty()) return List.of();
        if (targetLanguage == null || targetLanguage.isBlank()) {
            throw new IllegalArgumentException("targetLanguage is required");
        }

        TextTranslationOptions opts = new TextTranslationOptions().setPreserveFormatting(true);
        if (htmlMode) opts.setTagHandling("html");

        String code = targetLanguage.trim().toUpperCase(); // tiny cleanup, no mapping

        List<TranslatedText> out = new ArrayList<>(texts.size());
        for (String t : texts) {
            TextResult r = translator.translateText(t, null, code, opts);
            out.add(new TranslatedText(r.getText(), r.getDetectedSourceLanguage()));
        }
        return out;
    }
}

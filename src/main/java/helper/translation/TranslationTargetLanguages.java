package helper.translation;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Central registry of target languages for the translation feature.
 *
 * Keys in LANGUAGE_CODES are the labels shown in the combo box.
 * They are written in (or close to) each language's own name.
 *
 * Values are language codes understood by the TranslationService
 * (e.g., DeepL-style codes like "EN-US", "FR", "JA", "ZH-HANS", etc.).
 */
public final class TranslationTargetLanguages {

    /**
     * Combo-box label (native name) -> language code.
     *
     * LinkedHashMap is used to preserve insertion order for the UI.
     */
    private static final LinkedHashMap<String, String> LANGUAGE_CODES = new LinkedHashMap<>();

    static {
        // English variants
        LANGUAGE_CODES.put("English (US)", "EN-US");
        LANGUAGE_CODES.put("English (UK)", "EN-GB");

        // Major European languages (self-names)
        LANGUAGE_CODES.put("Français", "FR");
        LANGUAGE_CODES.put("Español (España)", "ES");
        LANGUAGE_CODES.put("Español (Latinoamérica)", "ES-419");
        LANGUAGE_CODES.put("Deutsch", "DE");
        LANGUAGE_CODES.put("Italiano", "IT");
        LANGUAGE_CODES.put("Português (Portugal)", "PT-PT");
        LANGUAGE_CODES.put("Português (Brasil)", "PT-BR");
        LANGUAGE_CODES.put("Nederlands", "NL");
        LANGUAGE_CODES.put("Svenska", "SV");
        LANGUAGE_CODES.put("Dansk", "DA");
        LANGUAGE_CODES.put("Suomi", "FI");
        LANGUAGE_CODES.put("Norsk bokmål", "NB");
        LANGUAGE_CODES.put("Polski", "PL");
        LANGUAGE_CODES.put("Čeština", "CS");
        LANGUAGE_CODES.put("Slovenčina", "SK");
        LANGUAGE_CODES.put("Slovenščina", "SL");
        LANGUAGE_CODES.put("Română", "RO");
        LANGUAGE_CODES.put("Magyar", "HU");
        LANGUAGE_CODES.put("Български", "BG");
        LANGUAGE_CODES.put("Ελληνικά", "EL");
        LANGUAGE_CODES.put("Eesti", "ET");
        LANGUAGE_CODES.put("Latviešu", "LV");
        LANGUAGE_CODES.put("Lietuvių", "LT");

        // Others
        LANGUAGE_CODES.put("Русский", "RU");
        LANGUAGE_CODES.put("Türkçe", "TR");
        LANGUAGE_CODES.put("العربية", "AR");
        LANGUAGE_CODES.put("עברית", "HE");
        LANGUAGE_CODES.put("ไทย", "TH");
        LANGUAGE_CODES.put("Tiếng Việt", "VI");
        LANGUAGE_CODES.put("Bahasa Indonesia", "ID");
        LANGUAGE_CODES.put("Українська", "UK");

        // East Asian
        LANGUAGE_CODES.put("日本語", "JA");
        LANGUAGE_CODES.put("한국어", "KO");
        LANGUAGE_CODES.put("简体中文", "ZH-HANS");
        LANGUAGE_CODES.put("繁體中文", "ZH-HANT");
    }

    private TranslationTargetLanguages() {
        // utility class, no instances
    }

    /**
     * Immutable view of label -> code map.
     */
    public static Map<String, String> getLanguageCodes() {
        return Collections.unmodifiableMap(LANGUAGE_CODES);
    }
}

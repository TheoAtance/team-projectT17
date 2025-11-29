package helper.translation;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Central place to configure which target languages we expose in the UI.
 *
 * Keys  = labels shown in the combo box (human-readable).
 * Values = language codes used by the TranslationService / DeepL wrapper.
 */
public final class TranslationTargetLanguages {

    // preserve insertion order for a nice, predictable dropdown
    private static final LinkedHashMap<String, String> LANGUAGE_CODES = new LinkedHashMap<>();

    static {
        // English variants
        LANGUAGE_CODES.put("English (US)", "EN-US");
        LANGUAGE_CODES.put("English (UK)", "EN-GB");

        // Major European languages
        LANGUAGE_CODES.put("French", "FR");
        LANGUAGE_CODES.put("Spanish (Spain)", "ES");
        LANGUAGE_CODES.put("Spanish (Latin America)", "ES-419");
        LANGUAGE_CODES.put("German", "DE");
        LANGUAGE_CODES.put("Italian", "IT");
        LANGUAGE_CODES.put("Portuguese (Portugal)", "PT-PT");
        LANGUAGE_CODES.put("Portuguese (Brazil)", "PT-BR");
        LANGUAGE_CODES.put("Dutch", "NL");
        LANGUAGE_CODES.put("Swedish", "SV");
        LANGUAGE_CODES.put("Danish", "DA");
        LANGUAGE_CODES.put("Finnish", "FI");
        LANGUAGE_CODES.put("Norwegian Bokmål", "NB");
        LANGUAGE_CODES.put("Polish", "PL");
        LANGUAGE_CODES.put("Czech", "CS");
        LANGUAGE_CODES.put("Slovak", "SK");
        LANGUAGE_CODES.put("Slovenian", "SL");
        LANGUAGE_CODES.put("Romanian", "RO");
        LANGUAGE_CODES.put("Hungarian", "HU");
        LANGUAGE_CODES.put("Bulgarian", "BG");
        LANGUAGE_CODES.put("Greek", "EL");
        LANGUAGE_CODES.put("Estonian", "ET");
        LANGUAGE_CODES.put("Latvian", "LV");
        LANGUAGE_CODES.put("Lithuanian", "LT");

        // Others
        LANGUAGE_CODES.put("Russian", "RU");
        LANGUAGE_CODES.put("Turkish", "TR");
        LANGUAGE_CODES.put("Arabic", "AR");
        LANGUAGE_CODES.put("Hebrew", "HE");
        LANGUAGE_CODES.put("Thai", "TH");
        LANGUAGE_CODES.put("Vietnamese", "VI");
        LANGUAGE_CODES.put("Indonesian", "ID");
        LANGUAGE_CODES.put("Ukrainian", "UK");   // matches your code choice

        // East Asian
        LANGUAGE_CODES.put("Japanese", "JA");
        LANGUAGE_CODES.put("Korean", "KO");
        LANGUAGE_CODES.put("Chinese (Simplified)", "ZH-HANS");
        LANGUAGE_CODES.put("Chinese (Traditional)", "ZH-HANT");
    }

    private TranslationTargetLanguages() {
        // utility class
    }

    /**
     * Map from dropdown label -> language code.
     * Returned map is unmodifiable; modify {@link #LANGUAGE_CODES} here instead.
     */
    public static Map<String, String> getLanguageCodes() {
        return Collections.unmodifiableMap(LANGUAGE_CODES);
    }
}

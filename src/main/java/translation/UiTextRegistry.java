package translation;
import java.util.*;

/**
 * - Holds keyed UI strings with English defaults
 * - Call translateAll(TranslationService, String) once to translate all
 *   registered strings into a target language; apply them to your UI
 *   components ( setTitle(currText(FAVORITES_TITLE)) ).
 * - (Better to place it in ui package)
 */
public final class UiTextRegistry {
    // Keys
    public static final String FAVORITES_TITLE  = "favorites.title";
    public static final String FAVORITES_HEADER = "favorites.header";

    // set default to English
    private static final LinkedHashMap<String, String> TEXTS = new LinkedHashMap<>();
    static {
        TEXTS.put(FAVORITES_TITLE,  "Favorite Restaurants");
        TEXTS.put(FAVORITES_HEADER, "Favorites List Here");
    }

    /** Read current text for a key */
    public static String currText(String key) {
        return TEXTS.getOrDefault(key, key);
    }

    /** Translate all registered texts */
    public static void translateAll(TranslationService tls, String targetLang) throws Exception {
        var originals = new ArrayList<>(TEXTS.values());
        var results   = tls.translateTexts(originals, targetLang);

        int i = 0;
        for (String key : TEXTS.keySet()) {
            TEXTS.put(key, results.get(i++).translated());
        }
    }

    private UiTextRegistry() {}
}

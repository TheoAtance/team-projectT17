package translation;

/**
 * - Holds keyed UI strings with English defaults
 * - Call translateAll(TranslationService, String) once to translate all
 *   registered strings into a target language; apply them to your UI
 *   components ( setTitle(currText(FAVORITES_TITLE)) ).
 * - (Better to place it in ui package, same as ui_en.properties)
 */
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/** Load UI strings from .properties and serve them by key. */
public final class UiTextRegistry {

    // Current language’s texts (values are mutable; keys are stable)
    private static final LinkedHashMap<String, String> TEXTS = new LinkedHashMap<>();

    /** Initialize to English */
    public static void initFromClasspath(String resourcePath) {
        Map<String,String> loaded = load(resourcePath);
        TEXTS.clear();
        TEXTS.putAll(loaded);
    }

    /** Switch to another language file at runtime. */
    public static void switchTo(String resourcePath) {
        Map<String,String> loaded = load(resourcePath);
        TEXTS.clear();
        TEXTS.putAll(loaded);
    }

    /** Lookup the current text for a key (falls back to key to avoid nulls). */
    public static String t(String key) {
        return TEXTS.getOrDefault(key, key);
    }

    // --- internals ---

    private static Map<String,String> load(String resourcePath) {
        try (InputStream in = UiTextRegistry.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (in == null) throw new IllegalStateException("Missing resource: " + resourcePath);
            Properties p = new Properties();
            // UTF-8 reader for natural text
            p.load(new InputStreamReader(in, StandardCharsets.UTF_8));
            LinkedHashMap<String,String> map = new LinkedHashMap<>();
            for (String name : p.stringPropertyNames()) {
                map.put(name, p.getProperty(name));
            }
            return map;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load " + resourcePath, e);
        }
    }

    private UiTextRegistry() {}
}
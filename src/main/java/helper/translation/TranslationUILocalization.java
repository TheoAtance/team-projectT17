package helper.translation;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides localized UI strings for the TranslationView / TranslationPanel: - Title ("Translated
 * Reviews") - "Target language:" - "Translate" button - "Original:" label - "Translated:" label -
 * "Back" button
 * <p>
 * All lookups are done by target language code (e.g., "FR", "JA", "ZH-HANS").
 */
public final class TranslationUILocalization {

  // Map: langCode -> UILabels
  private static final Map<String, UILabels> LABEL_MAP = new HashMap<>();

  static {
    // English (default)
    LABEL_MAP.put("EN-US", new UILabels(
        "Translated Reviews",
        "Target language:",
        "Translate",
        "Original:",
        "Translated:",
        "Close"
    ));
    LABEL_MAP.put("EN-GB", LABEL_MAP.get("EN-US"));

    // French
    LABEL_MAP.put("FR", new UILabels(
        "Avis traduits",
        "Langue cible :",
        "Traduire",
        "Original :",
        "Traduction :",
        "Fermer"
    ));

    // Spanish (Spain / Latin America)
    LABEL_MAP.put("ES", new UILabels(
        "Reseñas traducidas",
        "Idioma de destino:",
        "Traducir",
        "Original:",
        "Traducido:",
        "Cerrar"
    ));
    LABEL_MAP.put("ES-419", LABEL_MAP.get("ES"));

    // German
    LABEL_MAP.put("DE", new UILabels(
        "Übersetzte Bewertungen",
        "Zielsprache:",
        "Übersetzen",
        "Original:",
        "Übersetzung:",
        "Schließen"
    ));

    // Italian
    LABEL_MAP.put("IT", new UILabels(
        "Recensioni tradotte",
        "Lingua di destinazione:",
        "Traduci",
        "Originale:",
        "Tradotto:",
        "Chiudi"
    ));

    // Portuguese (PT / BR)
    LABEL_MAP.put("PT-PT", new UILabels(
        "Críticas traduzidas",
        "Idioma de destino:",
        "Traduzir",
        "Original:",
        "Traduzido:",
        "Fechar"
    ));
    LABEL_MAP.put("PT-BR", new UILabels(
        "Avaliações traduzidas",
        "Idioma de destino:",
        "Traduzir",
        "Original:",
        "Traduzido:",
        "Fechar"
    ));

    // Dutch
    LABEL_MAP.put("NL", new UILabels(
        "Vertaalde beoordelingen",
        "Doeltaal:",
        "Vertalen",
        "Origineel:",
        "Vertaald:",
        "Sluiten"
    ));

    // Nordic languages
    LABEL_MAP.put("SV", new UILabels(
        "Översatta recensioner",
        "Målspråk:",
        "Översätt",
        "Original:",
        "Översättning:",
        "Stäng"
    ));
    LABEL_MAP.put("DA", new UILabels(
        "Oversatte anmeldelser",
        "Målsprog:",
        "Oversæt",
        "Original:",
        "Oversættelse:",
        "Luk"
    ));
    LABEL_MAP.put("FI", new UILabels(
        "Käännetyt arvostelut",
        "Kohdekieli:",
        "Käännä",
        "Alkuperäinen:",
        "Käännös:",
        "Sulje"
    ));
    LABEL_MAP.put("NB", new UILabels(
        "Oversatte anmeldelser",
        "Målspråk:",
        "Oversett",
        "Original:",
        "Oversettelse:",
        "Lukk"
    ));

    // Central / Eastern Europe
    LABEL_MAP.put("PL", new UILabels(
        "Przetłumaczone recenzje",
        "Język docelowy:",
        "Tłumacz",
        "Oryginał:",
        "Tłumaczenie:",
        "Zamknij"
    ));
    LABEL_MAP.put("CS", new UILabels(
        "Přeložené recenze",
        "Cílový jazyk:",
        "Přeložit",
        "Originál:",
        "Překlad:",
        "Zavřít"
    ));
    LABEL_MAP.put("SK", new UILabels(
        "Preložené recenzie",
        "Cieľový jazyk:",
        "Preložiť",
        "Originál:",
        "Preklad:",
        "Zavrieť"
    ));
    LABEL_MAP.put("SL", new UILabels(
        "Prevedeni pregledi",
        "Ciljni jezik:",
        "Prevedi",
        "Izvirnik:",
        "Prevod:",
        "Zapri"
    ));
    LABEL_MAP.put("RO", new UILabels(
        "Recenzii traduse",
        "Limba țintă:",
        "Tradu",
        "Original:",
        "Tradus:",
        "Închide"
    ));
    LABEL_MAP.put("HU", new UILabels(
        "Lefordított értékelések",
        "Célnyelv:",
        "Fordítás",
        "Eredeti:",
        "Fordítás:",
        "Bezár"
    ));
    LABEL_MAP.put("BG", new UILabels(
        "Преведени отзиви",
        "Целеви език:",
        "Преведи",
        "Оригинал:",
        "Превод:",
        "Затвори"
    ));
    LABEL_MAP.put("EL", new UILabels(
        "Μεταφρασμένες κριτικές",
        "Γλώσσα στόχος:",
        "Μετάφραση",
        "Πρωτότυπο:",
        "Μετάφραση:",
        "Κλείσιμο"
    ));
    LABEL_MAP.put("ET", new UILabels(
        "Tõlgitud arvustused",
        "Sihtkeel:",
        "Tõlgi",
        "Originaal:",
        "Tõlge:",
        "Sulge"
    ));
    LABEL_MAP.put("LV", new UILabels(
        "Tulkotās atsauksmes",
        "Mērķa valoda:",
        "Tulkot",
        "Oriģināls:",
        "Tulkojums:",
        "Aizvērt"
    ));
    LABEL_MAP.put("LT", new UILabels(
        "Išverstos apžvalgos",
        "Tikslo kalba:",
        "Versti",
        "Originalas:",
        "Vertimas:",
        "Uždaryti"
    ));

    // Others
    LABEL_MAP.put("RU", new UILabels(
        "Переведённые отзывы",
        "Целевой язык:",
        "Перевести",
        "Оригинал:",
        "Перевод:",
        "Закрыть"
    ));
    LABEL_MAP.put("TR", new UILabels(
        "Çevrilmiş yorumlar",
        "Hedef dil:",
        "Çevir",
        "Orijinal:",
        "Çevrilmiş:",
        "Kapat"
    ));
    LABEL_MAP.put("AR", new UILabels(
        "المراجعات المترجمة",
        "اللغة المستهدفة:",
        "ترجمة",
        "النص الأصلي:",
        "الترجمة:",
        "إغلاق"
    ));
    LABEL_MAP.put("HE", new UILabels(
        "ביקורות מתורגמות",
        "שפת יעד:",
        "תרגם",
        "מקור:",
        "מתורגם:",
        "סגור"
    ));
    LABEL_MAP.put("TH", new UILabels(
        "รีวิวที่แปลแล้ว",
        "ภาษาปลายทาง:",
        "แปล",
        "ต้นฉบับ:",
        "ฉบับแปล:",
        "ปิด"
    ));
    LABEL_MAP.put("VI", new UILabels(
        "Đánh giá đã dịch",
        "Ngôn ngữ đích:",
        "Dịch",
        "Bản gốc:",
        "Bản dịch:",
        "Đóng"
    ));
    LABEL_MAP.put("ID", new UILabels(
        "Ulasan yang diterjemahkan",
        "Bahasa tujuan:",
        "Terjemahkan",
        "Asli:",
        "Terjemahan:",
        "Tutup"
    ));
    LABEL_MAP.put("UK", new UILabels(
        "Перекладені відгуки",
        "Мова перекладу:",
        "Перекласти",
        "Оригінал:",
        "Переклад:",
        "Закрити"
    ));

    // East Asian
    LABEL_MAP.put("JA", new UILabels(
        "翻訳されたレビュー",
        "対象言語:",
        "翻訳",
        "原文:",
        "翻訳文:",
        "閉じる"
    ));
    LABEL_MAP.put("KO", new UILabels(
        "번역된 리뷰",
        "대상 언어:",
        "번역",
        "원문:",
        "번역본:",
        "닫기"
    ));
    LABEL_MAP.put("ZH-HANS", new UILabels(
        "已翻译的点评",
        "目标语言：",
        "翻译",
        "原文：",
        "译文：",
        "关闭"
    ));
    LABEL_MAP.put("ZH-HANT", new UILabels(
        "已翻譯的評論",
        "目標語言：",
        "翻譯",
        "原文：",
        "譯文：",
        "關閉"
    ));
  }

  private TranslationUILocalization() {
    // utility class
  }

  /**
   * Returns localized UI labels for the given language code. Falls back to English if the language
   * is unknown.
   */
  public static UILabels forLanguage(String langCode) {
    if (langCode == null) {
      return LABEL_MAP.get("EN-US");
    }
    String upper = langCode.toUpperCase();
    UILabels labels = LABEL_MAP.get(upper);
    if (labels != null) {
      return labels;
    }
    // some services return "EN" / "ES" without region
    if (upper.startsWith("EN")) {
      return LABEL_MAP.get("EN-US");
    }
    if (upper.startsWith("ES")) {
      return LABEL_MAP.get("ES");
    }
    return LABEL_MAP.get("EN-US");
  }

  public static final class UILabels {

    private final String title;
    private final String targetLabel;
    private final String translateLabel;
    private final String originalLabel;
    private final String translatedLabel;
    private final String backLabel;

    public UILabels(String title,
        String targetLabel,
        String translateLabel,
        String originalLabel,
        String translatedLabel,
        String backLabel) {
      this.title = title;
      this.targetLabel = targetLabel;
      this.translateLabel = translateLabel;
      this.originalLabel = originalLabel;
      this.translatedLabel = translatedLabel;
      this.backLabel = backLabel;
    }

    public String getTitle() {
      return title;
    }

    public String getTargetLabel() {
      return targetLabel;
    }

    public String getTranslateLabel() {
      return translateLabel;
    }

    public String getOriginalLabel() {
      return originalLabel;
    }

    public String getTranslatedLabel() {
      return translatedLabel;
    }

    public String getBackLabel() {
      return backLabel;
    }
  }
}

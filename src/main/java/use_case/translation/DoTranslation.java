package use_case.translation;

import entity.Review;

import java.util.List;
import java.util.stream.Collectors;

/** Utilities for translating content */
public final class DoTranslation {
    private DoTranslation() {}

    /**
     * Translate the content of reviews into targetLang.
     * Returns translated strings in the SAME order as the input list.
     * ........................................................................
     * Language Supported
     * Source languages (auto-detectable):
     * AR — Arabic, BG — Bulgarian, CS — Czech, DA — Danish, DE — German,
     * EL — Greek, EN — English (all variants), ES — Spanish (all variants),
     * ET — Estonian, FI — Finnish, FR — French, HE — Hebrew (next-gen only),
     * HU — Hungarian, ID — Indonesian, IT — Italian, JA — Japanese, KO — Korean,
     * LT — Lithuanian, LV — Latvian, NB — Norwegian Bokmål, NL — Dutch,
     * PL — Polish, PT — Portuguese (all variants), RO — Romanian, RU — Russian,
     * SK — Slovak, SL — Slovenian, SV — Swedish, TH — Thai (next-gen only),
     * TR — Turkish, UK — Ukrainian, VI — Vietnamese (next-gen only),
     * ZH — Chinese (all variants).
     * ........................................................................
     * Target languages (must be specified explicitly):
     * AR — Arabic, BG — Bulgarian, CS — Czech, DA — Danish, DE — German,
     * EL — Greek, EN — English (unspecified; prefer EN-GB or EN-US),
     * EN-GB — English (British), EN-US — English (American),
     * ES — Spanish, ES-419 — Spanish (Latin American),
     * ET — Estonian, FI — Finnish, FR — French, HE — Hebrew (next-gen only),
     * HU — Hungarian, ID — Indonesian, IT — Italian, JA — Japanese, KO — Korean,
     * LT — Lithuanian, LV — Latvian, NB — Norwegian Bokmål, NL — Dutch,
     * PL — Polish, PT — Portuguese (unspecified; prefer PT-BR or PT-PT),
     * PT-BR — Portuguese (Brazilian), PT-PT — Portuguese (European),
     * RO — Romanian, RU — Russian, SK — Slovak, SL — Slovenian, SV — Swedish,
     * TH — Thai (next-gen only), TR — Turkish, UK — Ukrainian, VI — Vietnamese (next-gen only),
     * ZH — Chinese (unspecified; prefer ZH-HANS or ZH-HANT),
     * ZH-HANS — Chinese (Simplified), ZH-HANT — Chinese (Traditional).
     * from DeepL API Documentation
     *
     */
    public static List<String> translateContents(
            List<Review> reviews,
            String targetLanguage,
            TranslationService tls) throws Exception {

        List<String> texts = reviews.stream()
                .map(Review::getContent)
                .collect(Collectors.toList());

        List<TranslationService.TranslatedText> out =
                tls.translateTexts(texts, targetLanguage);

        return out.stream()
                .map(TranslationService.TranslatedText::translated)
                .collect(Collectors.toList());
    }
}

// for example:
// TranslationService tls = new DeeplTranslationService(System.getenv("DEEPL_API_KEY"), false);
// String target = user.getLanguage();
// List<String> translated = ReviewTranslation.translateContents(reviews, user.getLanguage(), tls);

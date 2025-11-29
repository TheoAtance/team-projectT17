package view;

import entity.Review;
import helper.translation.TranslationTargetLanguages;
import helper.translation.TranslationUILocalization;

import interface_adapter.ViewManagerModel;
import interface_adapter.translation.TranslationController;
import interface_adapter.translation.TranslationState;
import interface_adapter.translation.TranslationViewModel;
import view.panel_makers.TranslationPanel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Dedicated page for translating and viewing translated reviews.
 *
 * Thin wrapper around TranslationPanel:
 * - Knows about controllers / view models / navigation.
 * - TranslationPanel handles the actual UI look & feel.
 */
public class TranslationView extends JPanel implements PropertyChangeListener {

    public static final String VIEW_NAME = "translation";

    private final TranslationViewModel translationViewModel;
    private final ViewManagerModel viewManagerModel;
    private final String previousViewName;

    private TranslationController translationController;
    private List<Review> currentReviews;               // reviews to translate

    // dropdown label -> language code
    private final Map<String, String> languageCodes;

    // UI
    private final TranslationPanel translationPanel;
    private final JButton backButton;

    public TranslationView(TranslationViewModel translationViewModel,
                           ViewManagerModel viewManagerModel,
                           String previousViewName) {
        this.translationViewModel = translationViewModel;
        this.viewManagerModel = viewManagerModel;
        this.previousViewName = previousViewName;

        this.translationViewModel.addPropertyChangeListener(this);

        // language setup from helper
        this.languageCodes =
                new LinkedHashMap<>(TranslationTargetLanguages.getLanguageCodes());

        // Find the label whose code is EN-US so we can select it by default
        String defaultLabel = null;
        for (Map.Entry<String, String> entry : languageCodes.entrySet()) {
            if ("EN-US".equalsIgnoreCase(entry.getValue())) {
                defaultLabel = entry.getKey();
                break;
            }
        }

        // ====== Build UI ======
        setLayout(new BorderLayout());
        setOpaque(true);
        setBackground(new Color(249, 250, 251)); // same light gray as other pages

        // Translation card-style panel (matches Restaurant/Review panels)
        String[] languageOptions = languageCodes.keySet().toArray(new String[0]);
        translationPanel = new TranslationPanel(languageOptions, defaultLabel);

        // Wire panel "Translate" button to controller
        translationPanel.addTranslateActionListener(e -> {
            if (translationController != null
                    && currentReviews != null
                    && !currentReviews.isEmpty()) {

                String selectedLabel = translationPanel.getSelectedLanguageLabel();
                String code = languageCodes.getOrDefault(selectedLabel, "EN-US");
                translationController.execute(currentReviews, code);
            }
        });

        // Put the panel in a simple center container
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));
        center.add(translationPanel);
        center.add(Box.createVerticalGlue());

        // Back button row at the bottom
        backButton = new JButton("Close");
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel bottom = new JPanel();
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.X_AXIS));
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        bottom.setOpaque(false);
        bottom.add(Box.createHorizontalGlue());
        bottom.add(backButton);
        bottom.add(Box.createHorizontalGlue());

        // Back button(close this window for translation):
        backButton.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(TranslationView.this);
            if (window != null) {
                window.dispose();
            }
        });


        add(center, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        // Initial placeholder text
        translationPanel.setTranslatedContents(List.of());

        // Initial localization (default EN-US)
        applyLocalization("EN-US");
    }

    /**
     * Called from AppBuilder to hook up controller.
     */
    public void setTranslationController(TranslationController controller) {
        this.translationController = controller;
    }

    /**
     * Called when you open this page, so we know which reviews to translate.
     */
    public void setCurrentReviews(List<Review> reviews) {
        this.currentReviews = reviews;

        // Push originals into the panel
        if (reviews != null && !reviews.isEmpty()) {
            java.util.List<String> originals = new java.util.ArrayList<>();
            for (Review r : reviews) {
                // adjust if your getter name is different
                originals.add(r.getContent());
            }
            translationPanel.setOriginalContents(originals);
        } else {
            translationPanel.setOriginalContents(List.of());
        }
    }

    public String getViewName() {
        return VIEW_NAME;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        TranslationState state = (TranslationState) evt.getNewValue();
        updateView(state);
    }

    private void updateView(TranslationState state) {
        if (state == null) {
            return;
        }

        // Update language label + orientation / fonts inside the panel
        translationPanel.setTargetLanguageCode(state.getTargetLanguage());

        // Localize static UI text for this target language
        applyLocalization(state.getTargetLanguage());

        // Handle errors
        if (state.getErrorMessage() != null && !state.getErrorMessage().isEmpty()) {
            translationPanel.setErrorMessage(state.getErrorMessage());
            translationPanel.setTranslatedContents(List.of());
            return;
        } else {
            translationPanel.setErrorMessage("");
        }

        // Show translated texts
        List<String> texts = state.getTranslatedContents();
        translationPanel.setTranslatedContents(texts);
    }

    /**
     * Apply localized UI labels (title, "Target language:", Back)
     * for the given language code.
     */
    private void applyLocalization(String targetLangCode) {
        TranslationUILocalization.UILabels labels =
                TranslationUILocalization.forLanguage(targetLangCode);

        // --- text inside TranslationPanel ---
        translationPanel.setTitleText(labels.getTitle());
        translationPanel.setTargetLabelText(labels.getTargetLabel());
        translationPanel.setTranslateButtonText(labels.getTranslateLabel());
        translationPanel.setOriginalLabelText(labels.getOriginalLabel());
        translationPanel.setTranslatedLabelText(labels.getTranslatedLabel());

        // --- back button at the bottom ---
        backButton.setText(labels.getBackLabel());
        translationPanel.applyUIFontForText(backButton, labels.getBackLabel());
    }

}
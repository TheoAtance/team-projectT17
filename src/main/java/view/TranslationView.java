package view;

import entity.Review;
import helper.TranslationTargetLanguages;

import interface_adapter.ViewManagerModel;
import interface_adapter.translation.TranslationController;
import interface_adapter.translation.TranslationState;
import interface_adapter.translation.TranslationViewModel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Dedicated page for translating and viewing translated reviews.
 */
public class TranslationView extends JPanel implements PropertyChangeListener {

    public static final String VIEW_NAME = "translation";

    private final TranslationViewModel translationViewModel;
    private final ViewManagerModel viewManagerModel;
    private final String previousViewName;

    private TranslationController translationController;
    private List<Review> currentReviews;               // reviews to translate

    // UI components
    private final JLabel titleLabel;
    private final JLabel languageLabel;
    private final JLabel errorLabel;
    private final JTextArea translatedArea;
    private final JButton translateButton;
    private final JButton backButton;
    private final JComboBox<String> languageCombo;

    // dropdown label -> language code
    private final Map<String, String> languageCodes;

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

        // layout settings
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        titleLabel = new JLabel("Translated Reviews");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        // Language label
        languageLabel = new JLabel("Language: EN-US");
        languageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        languageLabel.setFont(new Font("Arial", Font.ITALIC, 14));

        // Error label
        errorLabel = new JLabel();
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        errorLabel.setForeground(Color.RED);

        // Text area for translated content
        translatedArea = new JTextArea(15, 50);
        translatedArea.setEditable(false);
        translatedArea.setLineWrap(true);
        translatedArea.setWrapStyleWord(true);

        // Need a font that can display Chinese and other Unicode text
        Font base = UIManager.getFont("TextArea.font");
        if (base == null) {
            base = new JTextArea().getFont();
        }
        // Try a common CJK-friendly font on Windows
        Font cjkCandidate = new Font("Microsoft YaHei", Font.PLAIN, base.getSize());
        if (cjkCandidate.canDisplay('\u4E2D')) { // '中'
            translatedArea.setFont(cjkCandidate);
        } else {
            translatedArea.setFont(base);
        }

        JScrollPane scrollPane = new JScrollPane(translatedArea);

        // Translate button row
        languageCombo = new JComboBox<>(languageCodes.keySet().toArray(new String[0]));
        languageCombo.setSelectedItem("English (US)");   // default

        translateButton = new JButton("Translate");
        translateButton.addActionListener(e -> {
            if (translationController != null && currentReviews != null && !currentReviews.isEmpty()) {
                String selectedLabel = (String) languageCombo.getSelectedItem();
                String code = languageCodes.getOrDefault(selectedLabel, "EN-US");
                translationController.execute(currentReviews, code);
            }
        });

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        topBar.add(new JLabel("Target language:"));
        topBar.add(languageCombo);
        topBar.add(translateButton);

        // Back button – just closes the window that contains this view
        backButton = new JButton("Back");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(TranslationView.this);
            if (window != null) {
                window.dispose();      // close this window only
            }
        });

        // Assemble layout
        add(titleLabel);
        add(Box.createVerticalStrut(10));
        add(languageLabel);
        add(Box.createVerticalStrut(5));
        add(errorLabel);
        add(Box.createVerticalStrut(10));
        add(topBar);
        add(Box.createVerticalStrut(10));
        add(scrollPane);
        add(Box.createVerticalStrut(15));
        add(backButton);

        translatedArea.setText("(No translated reviews to show.)");
    }

    /** Called from AppBuilder to hook up controller. */
    public void setTranslationController(TranslationController controller) {
        this.translationController = controller;
    }

    /** Called when you open this page, so we know which reviews to translate. */
    public void setCurrentReviews(List<Review> reviews) {
        this.currentReviews = reviews;
    }

    public String getViewName() {
        return VIEW_NAME;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // Same pattern as your other views (e.g. FilterView):
        TranslationState state = (TranslationState) evt.getNewValue();
        updateView(state);
    }

    private void updateView(TranslationState state) {
        if (state == null) {
            return;
        }

        // Update language label if provided
        if (state.getTargetLanguage() != null && !state.getTargetLanguage().isEmpty()) {
            languageLabel.setText("Language: " + state.getTargetLanguage());
        }

        // Handle errors
        if (state.getErrorMessage() != null && !state.getErrorMessage().isEmpty()) {
            errorLabel.setText(state.getErrorMessage());
            translatedArea.setText("");
            return;
        } else {
            errorLabel.setText("");
        }

        // Show translated texts
        List<String> texts = state.getTranslatedContents();
        if (texts == null || texts.isEmpty()) {
            translatedArea.setText("(No translated reviews to show.)");
        } else {
            String joined = String.join(
                    "\n\n-------------------------\n\n", texts);
            translatedArea.setText(joined);
            translatedArea.setCaretPosition(0); // scroll to top
        }
    }
}

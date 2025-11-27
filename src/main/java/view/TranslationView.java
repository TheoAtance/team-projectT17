package view;

import entity.Review;
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
    private final String previousViewName;  // where the Back button goes

    private TranslationController translationController;
    private List<Review> currentReviews;    // reviews to re-translate if language changes

    private final JLabel titleLabel;
    private final JTextArea translatedArea;
    private final JLabel languageLabel;
    private final JLabel errorLabel;
    private final JButton backButton;
    private final JButton translateButton;
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

        // --- language setup ---
        languageCodes = new LinkedHashMap<>();
        languageCodes.put("English", "EN");
        languageCodes.put("French", "FR");
        languageCodes.put("Spanish", "ES");
        languageCodes.put("Chinese (Simplified)", "ZH");

        // --- layout + components ---
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        titleLabel = new JLabel("Translated Reviews");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        // initialise to English
        languageLabel = new JLabel("Language: EN");
        languageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        languageLabel.setFont(new Font("Arial", Font.ITALIC, 14));

        errorLabel = new JLabel();
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        errorLabel.setForeground(Color.RED);

        translatedArea = new JTextArea(15, 50);
        translatedArea.setEditable(false);
        translatedArea.setLineWrap(true);
        translatedArea.setWrapStyleWord(true);
        translatedArea.setFont(new Font("Arial", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(translatedArea);

        // dropdown + translate button
        languageCombo = new JComboBox<>(languageCodes.keySet().toArray(new String[0]));
        languageCombo.setSelectedItem("English");   // default

        translateButton = new JButton("Translate");
        translateButton.addActionListener(e -> {
            if (translationController != null && currentReviews != null) {
                String selectedLabel = (String) languageCombo.getSelectedItem();
                String code = languageCodes.getOrDefault(selectedLabel, "EN");
                translationController.execute(currentReviews, code);
            }
        });

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        topBar.add(new JLabel("Target language:"));
        topBar.add(languageCombo);
        topBar.add(translateButton);

        backButton = new JButton("Back");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(e -> {
            viewManagerModel.setState(previousViewName);
        });

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
    }

    // called from AppBuilder to hook up controller
    public void setTranslationController(TranslationController controller) {
        this.translationController = controller;
    }

    // called from wherever you open this page, so we can re-translate the same reviews
    public void setCurrentReviews(List<Review> reviews) {
        this.currentReviews = reviews;
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
        // update language label based on use-case output ("EN", "FR", ...)
        if (state.getTargetLanguage() != null) {
            languageLabel.setText("Language: " + state.getTargetLanguage());
        }

        // error vs translated text
        if (state.getErrorMessage() != null && !state.getErrorMessage().isEmpty()) {
            errorLabel.setText(state.getErrorMessage());
            translatedArea.setText("");
        } else {
            errorLabel.setText("");
            List<String> texts = state.getTranslatedContents();
            if (texts != null && !texts.isEmpty()) {
                String joined = String.join(
                        "\n\n-------------------------\n\n", texts);
                translatedArea.setText(joined);
            } else {
                translatedArea.setText("(No translated reviews to show.)");
            }
        }
    }
}

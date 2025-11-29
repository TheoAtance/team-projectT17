package view.panel_makers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Card-style panel for showing translated reviews.
 * Designed to match ReviewPanel / RestaurantPanel visual style.
 */
public class TranslationPanel extends JPanel {

    // Inner card & margin (same pattern as ReviewPanel)
    private final JPanel main;
    private final JPanel margin;

    // Header
    private final JLabel titleLabel;
    private final JLabel languageLabel;
    private final JLabel errorLabel;

    // Controls
    private final JComboBox<String> languageCombo;
    private final JButton translateButton;

    // Content
    private final JTextArea translatedArea;

    // Fonts for different scripts
    private final Font defaultFont;
    private final Font thaiFont;
    private final Font koreanFont;
    private final Font cjkFont;   // Chinese + Japanese (and can also handle Korean)

    /**
     * @param languageOptions labels for dropdown (e.g. "English (US)", "French", ...)
     * @param defaultLanguageLabel which label should be initially selected
     */
    public TranslationPanel(String[] languageOptions, String defaultLanguageLabel) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        setOpaque(true);

        // ==== main card & margin (similar to ReviewPanel) ====
        main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBackground(Color.WHITE);

        // Inner padding + subtle rounded border like a card
        main.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235), 1, true),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

        margin = new JPanel();
        margin.setLayout(new BoxLayout(margin, BoxLayout.Y_AXIS));
        margin.setOpaque(false);
        margin.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));
        margin.add(main);

        add(margin);
        // =====================================================

        // ===== header =====
        titleLabel = new JLabel("Translated Reviews");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));

        languageLabel = new JLabel("Language: EN-US");
        languageLabel.setFont(languageLabel.getFont().deriveFont(Font.PLAIN, 12f));
        languageLabel.setForeground(new Color(107, 114, 128)); // gray-500

        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        headerRow.add(titleLabel, BorderLayout.WEST);
        headerRow.add(languageLabel, BorderLayout.EAST);

        errorLabel = new JLabel();
        errorLabel.setForeground(new Color(239, 68, 68)); // red-500
        errorLabel.setFont(errorLabel.getFont().deriveFont(Font.PLAIN, 11f));
        errorLabel.setVisible(false);
        // ===================

        // ===== controls row =====
        languageCombo = new JComboBox<>(languageOptions);
        languageCombo.setMaximumSize(new Dimension(250,
                languageCombo.getPreferredSize().height));
        if (defaultLanguageLabel != null) {
            languageCombo.setSelectedItem(defaultLanguageLabel);
        }

        translateButton = new JButton("Translate");
        translateButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        translateButton.setBackground(new Color(236, 72, 153)); // pink-500
        translateButton.setForeground(Color.WHITE);
        translateButton.setFocusPainted(false);
        translateButton.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));

        JPanel controlsRow = new JPanel();
        controlsRow.setOpaque(false);
        controlsRow.setLayout(new BoxLayout(controlsRow, BoxLayout.X_AXIS));

        JLabel targetLabel = new JLabel("Target language:");
        targetLabel.setFont(targetLabel.getFont().deriveFont(Font.BOLD, 12f));

        controlsRow.add(targetLabel);
        controlsRow.add(Box.createHorizontalStrut(8));
        controlsRow.add(languageCombo);
        controlsRow.add(Box.createHorizontalStrut(12));
        controlsRow.add(translateButton);
        controlsRow.add(Box.createHorizontalGlue());
        // ========================

        // ===== text area =====
        translatedArea = new JTextArea(10, 40);
        translatedArea.setEditable(false);
        translatedArea.setLineWrap(true);
        translatedArea.setWrapStyleWord(true);
        translatedArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        // soft pink background echoing RestaurantPanel header
        translatedArea.setBackground(new Color(255, 247, 250));

        JScrollPane scrollPane = new JScrollPane(translatedArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(new Color(255, 247, 250));
        // ======================

        // ===== font setup for languages =====
        Font base = UIManager.getFont("TextArea.font");
        if (base == null) {
            base = new JTextArea().getFont();
        }
        int size = base.getSize();

        defaultFont = new Font("Segoe UI", Font.PLAIN, size);
        thaiFont = new Font("Leelawadee UI", Font.PLAIN, size);
        koreanFont = new Font("Malgun Gothic", Font.PLAIN, size);

        Font cjkCandidate = new Font("Microsoft YaHei", Font.PLAIN, size);
        if (cjkCandidate.canDisplay('中') && cjkCandidate.canDisplay('日')) {
            cjkFont = cjkCandidate;
        } else {
            cjkFont = defaultFont;
        }
        translatedArea.setFont(defaultFont);
        // ======================

        // assemble main card
        main.add(headerRow);
        main.add(Box.createVerticalStrut(4));
        main.add(errorLabel);
        main.add(Box.createVerticalStrut(6));
        main.add(controlsRow);
        main.add(Box.createVerticalStrut(10));
        main.add(scrollPane);

        // initial placeholder
        setTranslatedContents(List.of());
    }

    /* ---------- public API used by TranslationView ---------- */

    /** Connects the "Translate" button to the controller. */
    public void addTranslateActionListener(ActionListener listener) {
        translateButton.addActionListener(listener);
    }

    /** Returns the currently selected language label from the dropdown. */
    public String getSelectedLanguageLabel() {
        return (String) languageCombo.getSelectedItem();
    }

    /** Update the language code shown under the title and adjust font / orientation. */
    public void setTargetLanguageCode(String code) {
        if (code == null || code.isEmpty()) {
            languageLabel.setText("Language: —");
        } else {
            languageLabel.setText("Language: " + code);
        }
        applyLanguageStyling(code);
    }

    /** Show / hide error message in red under the header. */
    public void setErrorMessage(String message) {
        if (message == null || message.isEmpty()) {
            errorLabel.setText("");
            errorLabel.setVisible(false);
        } else {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
        }
        revalidate();
        repaint();
    }

    /** Replace the translated text area contents. */
    public void setTranslatedContents(List<String> texts) {
        if (texts == null || texts.isEmpty()) {
            translatedArea.setText("(No translated reviews to show.)");
        } else {
            String joined = String.join(
                    "\n\n-------------------------\n\n", texts);
            translatedArea.setText(joined);
        }
        translatedArea.setCaretPosition(0);
    }


    /* ---------- internal: language-dependent styling ---------- */

    private void applyLanguageStyling(String langCode) {
        String upper = (langCode == null) ? "" : langCode.toUpperCase();

        // RTL for Arabic / Hebrew
        if ("AR".equals(upper) || "HE".equals(upper) || "IW".equals(upper)) {
            translatedArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        } else {
            translatedArea.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        }

        if ("TH".equals(upper)) {
            translatedArea.setFont(thaiFont);
        } else if ("KO".equals(upper)) {
            translatedArea.setFont(koreanFont);
        } else if ("JA".equals(upper)
                || "ZH".equals(upper)
                || "ZH-HANS".equals(upper)
                || "ZH-HANT".equals(upper)) {
            translatedArea.setFont(cjkFont);
        } else {
            translatedArea.setFont(defaultFont);
        }
    }
}

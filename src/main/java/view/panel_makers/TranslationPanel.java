package view.panel_makers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Card-style panel for showing original and translated reviews.
 * Designed to match ReviewPanel/RestaurantPanel ui style.
 */
public class TranslationPanel extends JPanel {

    // Inner card & margin
    private final JPanel main;
    private final JPanel margin;

    // Header
    private final JLabel titleLabel;
    private final JLabel languageLabel;
    private final JLabel errorLabel;

    // Controls
    private final JLabel targetLabel;
    private final JComboBox<String> languageCombo;
    private final JButton translateButton;

    // Section labels
    private final JLabel originalLabel;
    private final JLabel translatedLabel;

    // Content areas
    private final JTextArea originalArea;
    private final JTextArea translatedArea;

    // Fonts for different scripts
    private final Font defaultFont;
    private final Font thaiFont;
    private final Font koreanFont;
    private final Font cjkFont;   // Chinese + Japanese (also ok for Korean)


    /**
     * @param languageOptions      labels for dropdown (e.g. "English (US)", "Français", "日本語"...)
     * @param defaultLanguageLabel which label should be initially selected
     */
    public TranslationPanel(String[] languageOptions, String defaultLanguageLabel) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        setOpaque(true);

        main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBackground(Color.WHITE);
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

        // header
        titleLabel = new JLabel("Translated Reviews");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        languageLabel = new JLabel("Language: EN-US");
        languageLabel.setFont(languageLabel.getFont().deriveFont(Font.PLAIN, 12f));
        languageLabel.setForeground(new Color(107, 114, 128)); // gray-500

        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        headerRow.add(titleLabel, BorderLayout.WEST);
        headerRow.add(languageLabel, BorderLayout.EAST);
        headerRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        errorLabel = new JLabel();
        errorLabel.setForeground(new Color(239, 68, 68)); // red-500
        errorLabel.setFont(errorLabel.getFont().deriveFont(Font.PLAIN, 11f));
        errorLabel.setVisible(false);
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // font setup for language-dependent components
        Font base = UIManager.getFont("TextArea.font");
        if (base == null) {
            base = new JTextArea().getFont();
        }
        int size = base.getSize();

        defaultFont = new Font("Segoe UI", Font.PLAIN, size);
        thaiFont    = new Font("Leelawadee UI", Font.PLAIN, size);   // Thai UI font
        koreanFont  = new Font("Malgun Gothic", Font.PLAIN, size);   // Korean UI font

        Font cjkCandidate = new Font("Microsoft YaHei", Font.PLAIN, size); // CJK
        if (cjkCandidate.canDisplay('中') && cjkCandidate.canDisplay('日')) {
            cjkFont = cjkCandidate;
        } else {
            cjkFont = defaultFont;
        }

        // controls row
        languageCombo = new JComboBox<>(languageOptions);
        languageCombo.setMaximumSize(new Dimension(
                250, languageCombo.getPreferredSize().height));
        languageCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (defaultLanguageLabel != null) {
            languageCombo.setSelectedItem(defaultLanguageLabel);
        }

        // Choose font for each item & selected value for displaying text correctly
        languageCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {

                JLabel lbl = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);

                String text = (value == null) ? "" : value.toString();
                Font best = pickFontForText(text);
                Font current = lbl.getFont();
                lbl.setFont(best.deriveFont(current.getStyle(), current.getSize2D()));
                return lbl;
            }
        });

        // Update combo font when selection changes
        languageCombo.addItemListener(e -> {
            if (e.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
                String text = (String) e.getItem();
                Font best = pickFontForText(text);
                Font comboBase = languageCombo.getFont();
                languageCombo.setFont(best.deriveFont(
                        comboBase.getStyle(), comboBase.getSize2D()));
            }
        });

        // Initial font for combo
        String initial = (String) languageCombo.getSelectedItem();
        if (initial != null) {
            Font best = pickFontForText(initial);
            Font comboBase = languageCombo.getFont();
            languageCombo.setFont(best.deriveFont(
                    comboBase.getStyle(), comboBase.getSize2D()));
        }

        translateButton = new JButton("Translate");
        translateButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        translateButton.setBackground(new Color(236, 72, 153)); // pink-500
        translateButton.setForeground(Color.WHITE);
        translateButton.setFocusPainted(false);
        translateButton.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        translateButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        targetLabel = new JLabel("Target language:");
        targetLabel.setFont(targetLabel.getFont().deriveFont(Font.BOLD, 12f));

        JPanel controlsRow = new JPanel();
        controlsRow.setOpaque(false);
        controlsRow.setLayout(new BoxLayout(controlsRow, BoxLayout.X_AXIS));
        controlsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        controlsRow.add(targetLabel);
        controlsRow.add(Box.createHorizontalStrut(8));
        controlsRow.add(languageCombo);
        controlsRow.add(Box.createHorizontalStrut(12));
        controlsRow.add(translateButton);
        controlsRow.add(Box.createHorizontalGlue());

        // ===== original + translated areas =====
        originalLabel = new JLabel("Original:");
        originalLabel.setFont(originalLabel.getFont().deriveFont(Font.BOLD, 12f));
        originalLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        originalArea = new JTextArea(6, 40);
        originalArea.setEditable(false);
        originalArea.setLineWrap(true);
        originalArea.setWrapStyleWord(true);
        originalArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        originalArea.setBackground(Color.WHITE);
        originalArea.setFont(defaultFont);

        JScrollPane originalScroll = new JScrollPane(originalArea);
        originalScroll.setBorder(BorderFactory.createEmptyBorder());
        originalScroll.getViewport().setBackground(Color.WHITE);
        originalScroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        translatedLabel = new JLabel("Translated:");
        translatedLabel.setFont(translatedLabel.getFont().deriveFont(Font.BOLD, 12f));
        translatedLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        translatedArea = new JTextArea(8, 40);
        translatedArea.setEditable(false);
        translatedArea.setLineWrap(true);
        translatedArea.setWrapStyleWord(true);
        translatedArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        translatedArea.setBackground(new Color(255, 247, 250));
        translatedArea.setFont(defaultFont);

        JScrollPane translatedScroll = new JScrollPane(translatedArea);
        translatedScroll.setBorder(BorderFactory.createEmptyBorder());
        translatedScroll.getViewport().setBackground(new Color(255, 247, 250));
        translatedScroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Assemble main card
        main.add(headerRow);
        main.add(Box.createVerticalStrut(4));
        main.add(errorLabel);
        main.add(Box.createVerticalStrut(6));
        main.add(controlsRow);
        main.add(Box.createVerticalStrut(12));

        main.add(originalLabel);
        main.add(Box.createVerticalStrut(4));
        main.add(originalScroll);
        main.add(Box.createVerticalStrut(12));

        main.add(translatedLabel);
        main.add(Box.createVerticalStrut(4));
        main.add(translatedScroll);

        // initial placeholders
        setOriginalContents(List.of());
        setTranslatedContents(List.of());
    }


    // API
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

    /** Allow TranslationView to localize the panel title. */
    public void setTitleText(String text) {
        titleLabel.setText(text);
        Font base = titleLabel.getFont();
        Font best = pickFontForText(text);
        titleLabel.setFont(best.deriveFont(base.getStyle(), base.getSize2D()));
    }

    /** Allow TranslationView to localize the "Target language:" label. */
    public void setTargetLabelText(String text) {
        targetLabel.setText(text);
        Font base = targetLabel.getFont();
        Font best = pickFontForText(text);
        targetLabel.setFont(best.deriveFont(base.getStyle(), base.getSize2D()));
    }

    /** Localize the Translate button text. */
    public void setTranslateButtonText(String text) {
        translateButton.setText(text);
        Font base = translateButton.getFont();
        Font best = pickFontForText(text);
        translateButton.setFont(best.deriveFont(base.getStyle(), base.getSize2D()));
    }

    /** Localize the "Original:" label. */
    public void setOriginalLabelText(String text) {
        originalLabel.setText(text);
        Font base = originalLabel.getFont();
        Font best = pickFontForText(text);
        originalLabel.setFont(best.deriveFont(base.getStyle(), base.getSize2D()));
    }

    /** Localize the "Translated:" label. */
    public void setTranslatedLabelText(String text) {
        translatedLabel.setText(text);
        Font base = translatedLabel.getFont();
        Font best = pickFontForText(text);
        translatedLabel.setFont(best.deriveFont(base.getStyle(), base.getSize2D()));
    }

    /**
     * Utility so TranslationView can style external components
     * (e.g. the Back button) with the same font logic.
     */
    public void applyUIFontForText(JComponent comp, String text) {
        Font base = comp.getFont();
        Font best = pickFontForText(text);
        comp.setFont(best.deriveFont(base.getStyle(), base.getSize2D()));
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

    /** Replace the original text area contents. */
    public void setOriginalContents(List<String> texts) {
        if (texts == null || texts.isEmpty()) {
            originalArea.setText("(No reviews to show.)");
        } else {
            String joined = String.join(
                    "\n\n-------------------------\n\n", texts);
            originalArea.setText(joined);
        }
        originalArea.setCaretPosition(0);
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


    // Helpers
    /** Pick a font based on the characters in the given label text. */
    private Font pickFontForText(String text) {
        if (text == null || text.isEmpty()) {
            return defaultFont;
        }
        if (containsThai(text)) {
            return thaiFont;
        }
        if (containsHangul(text)) {
            return koreanFont;
        }
        if (containsCJK(text)) {
            return cjkFont;
        }
        // Arabic + Hebrew: Segoe UI usually has them; default is fine
        return defaultFont;
    }

    private void applyLanguageStyling(String langCode) {
        String upper = (langCode == null) ? "" : langCode.toUpperCase();

        // RTL for Arabic / Hebrew
        if ("AR".equals(upper) || "HE".equals(upper) || "IW".equals(upper)) {
            translatedArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        } else {
            translatedArea.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        }

        // Choose font for translated text block itself
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

    private boolean containsThai(String s) {
        for (char ch : s.toCharArray()) {
            if (ch >= 0x0E00 && ch <= 0x0E7F) {
                return true;
            }
        }
        return false;
    }

    private boolean containsHangul(String s) {
        for (char ch : s.toCharArray()) {
            // Hangul syllables + Jamo ranges
            if ((ch >= 0xAC00 && ch <= 0xD7AF) ||
                    (ch >= 0x1100 && ch <= 0x11FF) ||
                    (ch >= 0x3130 && ch <= 0x318F)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsCJK(String s) {
        for (char ch : s.toCharArray()) {
            if (ch >= 0x4E00 && ch <= 0x9FFF) {  // basic CJK
                return true;
            }
        }
        return false;
    }
}

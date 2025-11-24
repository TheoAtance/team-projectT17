package interface_adapter.translation;

import use_case.translation.TranslationOutputBoundary;
import use_case.translation.TranslationOutputData;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TranslationPresenter implements TranslationOutputBoundary {

    private final Component parent;

    public TranslationPresenter(Component parent) {
        this.parent = parent; // e.g., your RestaurantView
    }

    @Override
    public void present(TranslationOutputData outputData) {
        if (outputData.isError()) {
            JOptionPane.showMessageDialog(
                    parent,
                    outputData.getErrorMessage(),
                    "Translation error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        List<String> translated = outputData.getTranslatedContents();
        String text;

        if (translated == null || translated.isEmpty()) {
            text = "(No translated text)";
        } else {
            text = translated.get(0); // one review at a time
        }

        JOptionPane.showMessageDialog(
                parent,
                text,
                "Translated comment (" + outputData.getTargetLanguage() + ")",
                JOptionPane.PLAIN_MESSAGE
        );
    }
}
package use_case.translation;

public interface TranslationInputBoundary {
    /**
     * Execute the translation use case.
     */
    void execute(TranslationInputData inputData);
}
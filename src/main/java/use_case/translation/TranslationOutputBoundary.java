package use_case.translation;

public interface TranslationOutputBoundary {
    /**
     * Present the result of the translation use case.
     */
    void present(TranslationOutputData outputData);
}

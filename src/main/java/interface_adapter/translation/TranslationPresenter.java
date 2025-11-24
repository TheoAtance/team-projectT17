package interface_adapter.translation;

import interface_adapter.ViewManagerModel;
import use_case.translation.TranslationOutputBoundary;
import use_case.translation.TranslationOutputData;

/**
 * Presenter for the translation use case.
 * Converts TranslationOutputData into TranslationState and updates the view model.
 */
public class TranslationPresenter implements TranslationOutputBoundary {

    private final ViewManagerModel viewManagerModel;
    private final TranslationViewModel translationViewModel;

    public TranslationPresenter(ViewManagerModel viewManagerModel,
                                TranslationViewModel translationViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.translationViewModel = translationViewModel;
    }

    @Override
    public void present(TranslationOutputData outputData) {
        TranslationState state = translationViewModel.getState();

        if (outputData.isError()) {
            state.setTranslatedContents(null);
            state.setTargetLanguage(outputData.getTargetLanguage());
            state.setErrorMessage(outputData.getErrorMessage());
        } else {
            state.setTranslatedContents(outputData.getTranslatedContents());
            state.setTargetLanguage(outputData.getTargetLanguage());
            state.setErrorMessage(null);
        }

        translationViewModel.setState(state);
        translationViewModel.firePropertyChanged();
    }
}

package interface_adapter.translation;

import interface_adapter.ViewManagerModel;
import use_case.translation.TranslationOutputBoundary;
import use_case.translation.TranslationOutputData;

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
    // Update view-model state
    TranslationState state = translationViewModel.getState();
    state.setTargetLanguage(outputData.getTargetLanguage());
    state.setTranslatedContents(outputData.getTranslatedContents());

    if (outputData.isError()) {
      state.setErrorMessage(outputData.getErrorMessage());
    } else {
      state.setErrorMessage(null);
    }

    translationViewModel.setState(state);
    translationViewModel.firePropertyChanged();

    // Switch to the translation view page
    viewManagerModel.setState(translationViewModel.getViewName());
    viewManagerModel.firePropertyChange();
  }
}

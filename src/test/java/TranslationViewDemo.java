import entity.Review;
import interface_adapter.ViewManagerModel;
import interface_adapter.translation.TranslationController;
import interface_adapter.translation.TranslationPresenter;
import interface_adapter.translation.TranslationViewModel;
import use_case.translation.DeeplTranslationService;
import use_case.translation.TranslationInputBoundary;
import use_case.translation.TranslationInteractor;
import use_case.translation.TranslationService;
import view.TranslationView;

import javax.swing.*;
import java.util.List;

public class TranslationViewDemo {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 1. Get DeepL API key
            String key = System.getenv("DEEPL_API_KEY");
            if (key == null || key.isBlank()) {
                JOptionPane.showMessageDialog(null,
                        "DEEPL_API_KEY is not set.\n" +
                                "Set it in your Run Configuration or terminal to see real translations.",
                        "Missing API key",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 2. Set up clean-architecture stack for translation

            // View models
            ViewManagerModel viewManagerModel = new ViewManagerModel();
            TranslationViewModel translationViewModel = new TranslationViewModel();

            // Presenter
            TranslationPresenter presenter =
                    new TranslationPresenter(viewManagerModel, translationViewModel);

            // Use-case interactor + service
            TranslationService translationService =
                    new DeeplTranslationService(key, false);  // htmlMode = false
            TranslationInputBoundary interactor =
                    new TranslationInteractor(translationService, presenter);

            // Controller
            TranslationController controller = new TranslationController(interactor);

            // 3. Build the TranslationView and hook controller
            String previousViewName = "previous-demo-view";
            TranslationView translationView =
                    new TranslationView(translationViewModel, viewManagerModel, previousViewName);
            translationView.setTranslationController(controller);

            // 4. Give it some example reviews to translate
            Review r1 = new Review("user1", "rest1",
                    "The food here is amazing!", 5);
            Review r2 = new Review("user2", "rest1",
                    "Service is a bit slow, but the taste is great.", 4);

            translationView.setCurrentReviews(List.of(r1, r2));

            // Optionally: trigger an initial translation to French so it's not empty:
            controller.execute(List.of(r1, r2), "FR");

            // 5. Show the view in a frame
            JFrame frame = new JFrame("TranslationView Real Demo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(translationView);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}


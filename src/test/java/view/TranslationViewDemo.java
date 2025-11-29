package view;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import entity.Review;
import interface_adapter.ViewManagerModel;
import interface_adapter.translation.TranslationController;
import interface_adapter.translation.TranslationPresenter;
import interface_adapter.translation.TranslationViewModel;
import use_case.translation.DeeplTranslationService;
import use_case.translation.TranslationInputBoundary;
import use_case.translation.TranslationInteractor;
import use_case.translation.TranslationService;

import javax.swing.*;
import java.io.BufferedReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TranslationViewDemo {

    /** DTO matching the JSON structure in src/main/java/data/reviews.json */
    private static class ReviewDto {
        String creationDate;
        String restaurantId;
        String authorId;
        String reviewId;
        String content;
        int likes;
    }

    /** Load reviews from src/main/java/data/reviews.json and build Review entities. */
    private static List<Review> loadReviewsFromJson() {
        try {
            Path path = Path.of("src/main/java/data/reviews.json");
            try (BufferedReader reader =
                         Files.newBufferedReader(path, StandardCharsets.UTF_8)) {

                Gson gson = new Gson();
                Type listType = new TypeToken<List<ReviewDto>>() {}.getType();
                List<ReviewDto> dtos = gson.fromJson(reader, listType);

                List<Review> reviews = new ArrayList<>();
                for (ReviewDto dto : dtos) {
                    // Constructor order: (reviewId, authorId, restaurantId, content, creationDate, likes)
                    reviews.add(new Review(
                            dto.reviewId,
                            dto.authorId,
                            dto.restaurantId,
                            dto.content,
                            dto.creationDate,
                            dto.likes
                    ));
                }
                return reviews;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load src/main/java/data/reviews.json", e);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Get DeepL API key
            String key = System.getenv("DEEPL_API_KEY");
            if (key == null || key.isBlank()) {
                JOptionPane.showMessageDialog(null,
                        "DEEPL_API_KEY is not set.\n" +
                                "Set it in your Run Configuration or terminal to see real translations.",
                        "Missing API key",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

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

            // Build the TranslationView
            String previousViewName = "previous-demo-view";
            TranslationView translationView =
                    new TranslationView(translationViewModel, viewManagerModel, previousViewName);
            translationView.setTranslationController(controller);

            // Load some example reviews from JSON
            List<Review> allReviews = loadReviewsFromJson();
            // For the demo, just use the first 2 (or all if you like)
            List<Review> demoReviews = allReviews.size() > 2
                    ? allReviews.subList(0, 2)
                    : allReviews;

            translationView.setCurrentReviews(demoReviews);

            // Initial translation to English so it's not empty:
            controller.execute(demoReviews, "en-US");  // or "EN-US" if you prefer

            // Show in a frame
            JFrame frame = new JFrame("TranslationView Real Demo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(translationView);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}

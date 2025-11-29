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
import view.panel_makers.ReviewPanel;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Demo:
 * - Reads src/main/java/data/reviews.json.
 * - Builds Review entities.
 * - Uses the FIRST 8 reviews as example cards.
 * - Each card's "Translate" button opens a TranslationView window
 *   that calls the real DeepL-based translation use case.
 */
public class ReviewPanelDemo {

    /** DTO matching the JSON structure of reviews.json. */
    private static class ReviewDto {
        String creationDate;
        String restaurantId;
        String authorId;
        String reviewId;
        String content;
        int likes;
    }

    /** Load ALL reviews from reviews.json into Review entities. */
    private static List<Review> loadAllReviewsFromJson() {
        try {
            Path path = Path.of("src/main/java/data/reviews.json");
            try (BufferedReader reader =
                         Files.newBufferedReader(path, StandardCharsets.UTF_8)) {

                Gson gson = new Gson();
                Type listType = new TypeToken<List<ReviewDto>>() {}.getType();
                List<ReviewDto> dtos = gson.fromJson(reader, listType);

                List<Review> reviews = new ArrayList<>();
                for (ReviewDto dto : dtos) {
                    // Match your Review constructor:
                    // Review(String reviewId, String userId,
                    //        String restaurantId, String content,
                    //        String creationDate, int likes)
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
            throw new RuntimeException(
                    "Failed to load src/main/java/data/reviews.json", e);
        }
    }

    /** Panel listing ReviewPanel cards for the given reviews. */
    private static class ReviewListPanel extends JPanel {

        public ReviewListPanel(List<Review> reviews,
                               TranslationViewModel translationViewModel,
                               TranslationController translationController,
                               ViewManagerModel viewManagerModel) {

            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBackground(new Color(249, 250, 251));

            for (Review review : reviews) {
                String authorId = review.getAuthorId();
                // Simple display name: "User CqHem5..."
                String authorDisplayName =
                        (authorId != null && authorId.length() > 6)
                                ? "User " + authorId.substring(0, 6) + "..."
                                : "User " + authorId;

                String date = review.getCreationDate();
                String content = review.getContent();

                ReviewPanel reviewPanel =
                        new ReviewPanel(authorDisplayName, date, content);

                // Translate button → open TranslationView window for THIS review
                reviewPanel.addTranslateButtonListener(e -> {
                    TranslationView translationView =
                            new TranslationView(translationViewModel, viewManagerModel, null);
                    translationView.setTranslationController(translationController);
                    translationView.setCurrentReviews(List.of(review)); // pre-fill Original

                    JFrame frame = new JFrame("Translate review");
                    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                    frame.setContentPane(translationView);
                    frame.pack();
                    frame.setLocationRelativeTo(ReviewListPanel.this);
                    frame.setVisible(true);
                    // User clicks Translate inside that window to hit DeepL.
                });

                Dimension preferredSize = reviewPanel.getPreferredSize();
                reviewPanel.setMaximumSize(
                        new Dimension(Integer.MAX_VALUE, preferredSize.height)
                );

                add(reviewPanel);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 1) DeepL API key
            String key = System.getenv("DEEPL_API_KEY");
            if (key == null || key.isBlank()) {
                JOptionPane.showMessageDialog(
                        null,
                        "DEEPL_API_KEY is not set.\n" +
                                "Set it in your Run Configuration or terminal " +
                                "to see real translations.",
                        "Missing API key",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // 2) Translation stack (same as real app)
            ViewManagerModel viewManagerModel = new ViewManagerModel();
            TranslationViewModel translationViewModel = new TranslationViewModel();
            TranslationPresenter presenter =
                    new TranslationPresenter(viewManagerModel, translationViewModel);
            TranslationService translationService =
                    new DeeplTranslationService(key, false);
            TranslationInputBoundary interactor =
                    new TranslationInteractor(translationService, presenter);
            TranslationController controller = new TranslationController(interactor);

            // 3) Load ALL reviews, then take the first 8 for the demo
            List<Review> allReviews = loadAllReviewsFromJson();
            List<Review> demoReviews;
            if (allReviews.size() <= 8) {
                demoReviews = allReviews;
            } else {
                demoReviews = new ArrayList<>(allReviews.subList(0, 8));
            }

            if (demoReviews.isEmpty()) {
                JOptionPane.showMessageDialog(
                        null,
                        "No reviews found in reviews.json.",
                        "No reviews",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }

            // 4) Build frame with ReviewPanels
            JFrame frame = new JFrame("ReviewPanel → TranslationView Demo (first 8 reviews)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            ReviewListPanel reviewListPanel =
                    new ReviewListPanel(demoReviews, translationViewModel, controller, viewManagerModel);

            JScrollPane scrollPane = new JScrollPane(reviewListPanel);
            scrollPane.setBorder(null);

            frame.setContentPane(scrollPane);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}

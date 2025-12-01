package view;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import entity.Review;
import interface_adapter.ViewManagerModel;
import interface_adapter.translation.TranslationController;
import interface_adapter.translation.TranslationPresenter;
import interface_adapter.translation.TranslationViewModel;
import java.awt.Color;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import use_case.translation.DeeplTranslationService;
import use_case.translation.TranslationInputBoundary;
import use_case.translation.TranslationInteractor;
import use_case.translation.TranslationService;
import view.panel_makers.ReviewPanel;

/**
 * Demo: - Reads src/main/java/data/reviews.json. - Builds Review entities. - Uses the FIRST 8
 * reviews as example cards. - Each card's "Translate" button opens an independent TranslationView
 * window that calls the real DeepL-based translation use case.
 */
public class ReviewPanelAndTranslationDemo {

  /**
   * Load ALL reviews from reviews.json into Review entities.
   */
  private static List<Review> loadAllReviewsFromJson() {
    try {
      Path path = Path.of("src/main/java/data/reviews.json");
      try (BufferedReader reader =
          Files.newBufferedReader(path, StandardCharsets.UTF_8)) {

        Gson gson = new Gson();
        Type listType = new TypeToken<List<ReviewDto>>() {
        }.getType();
        List<ReviewDto> dtos = gson.fromJson(reader, listType);

        List<Review> reviews = new ArrayList<>();
        for (ReviewDto dto : dtos) {
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

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      // DeepL API key
      String key = System.getenv("DEEPL_API_KEY");
      if (key == null || key.isBlank()) {
        JOptionPane.showMessageDialog(
            null,
            "DEEPL_API_KEY is not set.",
            "Missing API key",
            JOptionPane.ERROR_MESSAGE
        );
        return;
      }

      // Load ALL reviews, then take the first 8 for the demo
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

      // Build frame with ReviewPanels
      JFrame frame = new JFrame("ReviewPanel → TranslationView Demo (first 8 reviews)");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      ReviewListPanel reviewListPanel =
          new ReviewListPanel(demoReviews, key);

      JScrollPane scrollPane = new JScrollPane(reviewListPanel);
      scrollPane.setBorder(null);

      frame.setContentPane(scrollPane);
      frame.setSize(800, 600);
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);
    });
  }

  /**
   * DTO matching the JSON structure of reviews.json.
   */
  private static class ReviewDto {

    String creationDate;
    String restaurantId;
    String authorId;
    String reviewId;
    String content;
    int likes;
  }

  /**
   * Panel listing ReviewPanel cards for the given reviews.
   */
  private static class ReviewListPanel extends JPanel {

    private final String apiKey;

    public ReviewListPanel(List<Review> reviews, String apiKey) {
      this.apiKey = apiKey;

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

        // Translate button → open an INDEPENDENT TranslationView window for THIS review
        reviewPanel.addTranslateButtonListener(e -> openTranslationWindowFor(review));

        Dimension preferredSize = reviewPanel.getPreferredSize();
        reviewPanel.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, preferredSize.height)
        );

        add(reviewPanel);
      }
    }

    /**
     * Build a completely independent translation stack for a single popup window.
     */
    private void openTranslationWindowFor(Review review) {
      // per-window translation to prevent conflict when there are several translation windows

      // Local view manager
      ViewManagerModel popupViewManager = new ViewManagerModel();

      // View model unique to this window
      TranslationViewModel popupViewModel = new TranslationViewModel();

      // Presenter bound only to this view model
      TranslationPresenter presenter =
          new TranslationPresenter(popupViewManager, popupViewModel);

      // Service + interactor for this window
      TranslationService translationService =
          new DeeplTranslationService(apiKey, false);
      TranslationInputBoundary interactor =
          new TranslationInteractor(translationService, presenter);

      // Controller for this window
      TranslationController popupController =
          new TranslationController(interactor);

      // Build TranslationView
      TranslationView translationView =
          new TranslationView(popupViewModel, popupViewManager, null);
      translationView.setTranslationController(popupController);
      translationView.setCurrentReviews(List.of(review)); // pre-fill "Original"

      // Show popup window
      JFrame frame = new JFrame("Translate review");
      frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      frame.setContentPane(translationView);
      frame.pack();
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);
    }
  }
}

package view;

import view.panel_makers.ReviewPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Simple demo window for ReviewPanel.
 * Shows a scrollable list of review cards.
 */
public class ReviewPanelDemo {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ReviewPanelDemo::createAndShow);
    }

    private static void createAndShow() {
        JFrame frame = new JFrame("ReviewPanel Demo");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // App-style background
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(243, 244, 246)); // light gray

        // Column of review cards
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        // Example reviews
        addReviewCard(listPanel,
                "Alice Chen",
                "2025-04-01",
                "Cozy cafe with great latte art. Staff were super friendly and " +
                        "there are lots of plugs for studying. The WiFi was stable and fast."
        );

        addReviewCard(listPanel,
                "Mohammed Khan",
                "2025-04-03",
                "Amazing place for a quick lunch. The chicken shawarma wrap was fresh " +
                        "and the garlic sauce was incredible. Would definitely come back!"
        );

        addReviewCard(listPanel,
                "Sarah Lee",
                "2025-04-05",
                "Came here with friends on a Friday night. It was busy but the service " +
                        "was still pretty fast. Portion sizes are huge, so come hungry."
        );

        listPanel.add(Box.createVerticalStrut(20)); // bottom padding

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        root.add(scrollPane, BorderLayout.CENTER);
        frame.setContentPane(root);
        frame.setSize(600, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void addReviewCard(JPanel listPanel,
                                      String author,
                                      String date,
                                      String content) {
        ReviewPanel card = new ReviewPanel(author, date, content);

        // Wrap in a margin panel so it matches the spacing style
        JPanel margin = new JPanel();
        margin.setLayout(new BoxLayout(margin, BoxLayout.Y_AXIS));
        margin.setOpaque(false);
        margin.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 20));
        margin.add(card);

        listPanel.add(margin);
    }
}

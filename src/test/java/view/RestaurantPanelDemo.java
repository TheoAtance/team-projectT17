package view;

import javax.swing.*;
import java.awt.*;

/**
 * Simple standalone demo for RestaurantPanel.
 * Shows a vertical list of cards and lets you click the heart icon.
 */
public class RestaurantPanelDemo {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RestaurantPanelDemo::createAndShow);
    }

    private static void createAndShow() {
        JFrame frame = new JFrame("RestaurantPanel Demo");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Soft grey app background, like your other views
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(243, 244, 246)); // Tailwind gray-100 style

        // Column that holds cards
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        // Some example data
        RestaurantPanel.RestaurantDisplayData r1 =
                new RestaurantPanel.RestaurantDisplayData(
                        "r1",
                        "Café Mocha on College",
                        "Cafe · Dessert · Coffee",
                        4.7,
                        true,
                        0.20
                );

        RestaurantPanel.RestaurantDisplayData r2 =
                new RestaurantPanel.RestaurantDisplayData(
                        "r2",
                        "Sushi Time",
                        "Japanese · Sushi · Ramen",
                        4.5,
                        false,
                        0.0
                );

        RestaurantPanel.RestaurantDisplayData r3 =
                new RestaurantPanel.RestaurantDisplayData(
                        "r3",
                        "Late Night Pizza & Wings",
                        "Pizza · Wings · Fast food",
                        4.2,
                        true,
                        0.30
                );

        // Helper to add a card with some margin
        addCard(listPanel, r1);
        addCard(listPanel, r2);
        addCard(listPanel, r3);

        listPanel.add(Box.createVerticalStrut(20)); // bottom padding

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        root.add(scrollPane, BorderLayout.CENTER);
        frame.setContentPane(root);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void addCard(JPanel listPanel,
                                RestaurantPanel.RestaurantDisplayData data) {
        RestaurantPanel card = new RestaurantPanel(data);

        // Listen to heart clicks and log them
        card.setHeartClickListener((restaurantId, newState) -> {
            System.out.println("Heart clicked for " + restaurantId +
                    " -> favorite = " + newState);
        });

        // Outer margin panel so it matches ReviewPanel style
        JPanel margin = new JPanel();
        margin.setLayout(new BoxLayout(margin, BoxLayout.Y_AXIS));
        margin.setOpaque(false);
        margin.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 20));
        margin.add(card);

        listPanel.add(margin);
    }
}

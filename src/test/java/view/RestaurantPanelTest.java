package view;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class RestaurantPanelTest {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Restaurant Panel Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Create sample data
            RestaurantPanel.RestaurantDisplayData data =
                    new RestaurantPanel.RestaurantDisplayData(
                            "1",
                            "Test Restaurant",
                            "Italian",
                            4.5,
                            true,
                            0.2
                    );

            // Try to load a placeholder image (optional)
            BufferedImage testImage = null;
            try {
                testImage = ImageIO.read(Objects.requireNonNull(
                        RestaurantPanelTest.class.getResource("/images/placeholder.png")
                ));
            } catch (IOException | NullPointerException e) {
                System.out.println("No placeholder image found, using gradient background");
            }

            // Create panel with or without image
            RestaurantPanel panel;
            if (testImage != null) {
                panel = new RestaurantPanel(data, testImage);
            } else {
                panel = new RestaurantPanel(data);
            }

            // Heart click listener
            panel.setHeartClickListener((restaurantId, isFavorite) -> {
                System.out.println("Heart clicked: " + restaurantId + " - Favorite: " + isFavorite);
            });

            // Restaurant click listener
            panel.setRestaurantClickListener((restaurantId, displayData) -> {
                System.out.println("Restaurant clicked: " + displayData.getName() + " (ID: " + restaurantId + ")");
                JOptionPane.showMessageDialog(frame,
                        "You clicked on: " + displayData.getName() + "\nRating: " + displayData.getRating(),
                        "Restaurant Details",
                        JOptionPane.INFORMATION_MESSAGE);
            });

            frame.add(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            System.out.println("Click the heart icon to toggle favorite");
            System.out.println("Click anywhere else on the panel to view restaurant details");
        });
    }
}
package view;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

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

      RestaurantPanel panel = new RestaurantPanel(data);
      panel.setHeartClickListener((restaurantId, isFavorite) -> {
        System.out.println("Heart clicked: " + restaurantId + " - Favorite: " + isFavorite);
      });

      frame.add(panel);
      frame.pack();
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);
    });
  }
}
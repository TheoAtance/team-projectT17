package ui.components;

import entity.Restaurant;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * A scrollable panel that displays a list of RestaurantPanel cards.
 * Only depends on Restaurant entity, no DAO or JSON access.
 */
public class RestaurantListView extends JPanel {

    private final JPanel contentPanel;
    private final JScrollPane scrollPane;

    public interface HeartClickListener {
        void onHeartClicked(Restaurant restaurant, boolean newFavoriteState);
    }

    public RestaurantListView(List<Restaurant> restaurants, HeartClickListener heartListener) {
        setLayout(new BorderLayout());
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);

        scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane, BorderLayout.CENTER);

        populateRestaurants(restaurants, heartListener);
    }

    private void populateRestaurants(List<Restaurant> restaurants, HeartClickListener heartListener) {
        contentPanel.removeAll();

        for (Restaurant restaurant : restaurants) {
            RestaurantPanel panel = new RestaurantPanel(restaurant);
            if (heartListener != null) {
                panel.setHeartClickListener((r, newState) -> heartListener.onHeartClicked(r, newState));
            }
            panel.setAlignmentX(Component.LEFT_ALIGNMENT);
            contentPanel.add(panel);
            contentPanel.add(Box.createVerticalStrut(15)); // spacing between cards
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    /**
     * Allows dynamic updating of the list.
     */
    public void updateRestaurants(List<Restaurant> restaurants, HeartClickListener heartListener) {
        populateRestaurants(restaurants, heartListener);
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }
}
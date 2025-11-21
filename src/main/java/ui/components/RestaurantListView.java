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

        // Main content panel with GridLayout for 3-column grid
        contentPanel = new JPanel();
        contentPanel.setOpaque(false); // let wrapper background show through
        contentPanel.setLayout(new GridLayout(0, 3, 15, 15)); // dynamic rows, 3 columns, 15px spacing

        // Wrapper panel to center grid and provide margin & background
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapper.setBackground(Color.WHITE); // match the list search background
        int margin = 15;
        wrapper.setBorder(BorderFactory.createEmptyBorder(margin, margin, margin, margin));
        wrapper.add(contentPanel);

        // Scroll pane
        scrollPane = new JScrollPane(wrapper);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane, BorderLayout.CENTER);

        populateRestaurants(restaurants, heartListener);
    }

    private void populateRestaurants(List<Restaurant> restaurants, HeartClickListener heartListener) {
        contentPanel.removeAll();

        for (Restaurant restaurant : restaurants) {
            RestaurantPanel panel = new RestaurantPanel(restaurant);

            // Optional padding inside each card
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            if (heartListener != null) {
                panel.setHeartClickListener((r, newState) -> heartListener.onHeartClicked(r, newState));
            }

            contentPanel.add(panel);
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

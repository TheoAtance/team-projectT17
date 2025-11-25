package ui.components;

import entity.Restaurant;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * A scrollable panel that displays a grid of RestaurantPanel cards.
 * Supports responsive resizing – the number of columns adjusts based on window width.
 */
public class RestaurantListView extends JPanel {

    private final JPanel contentPanel;
    private final JScrollPane scrollPane;

    // adjustable column settings
    private int preferredColumns = 5;      // default column count
    private int cardWidth = 250;           // estimated width of a card including padding

    public interface HeartClickListener {
        void onHeartClicked(Restaurant restaurant, boolean newFavoriteState);
    }

    public RestaurantListView(List<Restaurant> restaurants, HeartClickListener heartListener) {
        setLayout(new BorderLayout());

        contentPanel = new JPanel();
        contentPanel.setOpaque(false);

        // initial layout (will get overridden by resizing)
        contentPanel.setLayout(new GridLayout(0, preferredColumns, 15, 15));

        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapper.setBackground(Color.WHITE);

        int margin = 15;
        wrapper.setBorder(BorderFactory.createEmptyBorder(margin, margin, margin, margin));
        wrapper.add(contentPanel);

        scrollPane = new JScrollPane(wrapper);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane, BorderLayout.CENTER);

        // responsive behavior
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                adjustColumns();
            }
        });

        populateRestaurants(restaurants, heartListener);
    }

    /**
     * Dynamically adjusts the number of columns based on window width.
     */
    private void adjustColumns() {
        int width = getWidth();
        if (width <= 0) return;

        // estimate number of columns based on available space
        int calculatedColumns = Math.max(1, width / cardWidth);

        if (calculatedColumns != preferredColumns) {
            preferredColumns = calculatedColumns;
            contentPanel.setLayout(new GridLayout(0, preferredColumns, 15, 15));
            revalidate();
            repaint();
        }
    }

    private void populateRestaurants(List<Restaurant> restaurants, HeartClickListener heartListener) {
        contentPanel.removeAll();

        for (Restaurant restaurant : restaurants) {
            RestaurantPanel panel = new RestaurantPanel(restaurant);
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
     * Allows dynamic updating of the grid.
     */
    public void updateRestaurants(List<Restaurant> restaurants, HeartClickListener heartListener) {
        populateRestaurants(restaurants, heartListener);
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }
}

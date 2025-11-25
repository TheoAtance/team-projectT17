package view; // Ensure this is in the 'view' package

import view.RestaurantPanel; // Import the new RestaurantPanel

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

/**
 * A scrollable panel that displays a grid of RestaurantPanel cards.
 * Supports responsive resizing – the number of columns adjusts based on window width.
 * This component is now purely for display and rendering, it does not hold the list of restaurants internally.
 */
public class RestaurantListView extends JPanel {

    private final JPanel contentPanel;
    private final JScrollPane scrollPane;
    private final RestaurantPanel.HeartClickListener heartListener;

    // adjustable column settings
    private int preferredColumns = 5;      // default column count
    // FIXED: Declare cardWidth as a local constant matching RestaurantPanel's CARD_WIDTH
    private static final int CARD_WIDTH_FIXED = 280; // Use a distinct name to avoid confusion

    public RestaurantListView(List<RestaurantPanel.RestaurantDisplayData> displayDataList, RestaurantPanel.HeartClickListener heartListener) {
        setLayout(new BorderLayout());
        this.heartListener = heartListener; // Store the listener

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

        // Populate initially (if any restaurants are passed)
        populateRestaurants(displayDataList);
    }

    public RestaurantListView(RestaurantPanel.HeartClickListener heartListener) {
        this(new ArrayList<>(), heartListener);
    }


    /**
     * Dynamically adjusts the number of columns based on window width.
     */
    private void adjustColumns() {
        int width = getWidth();
        if (width <= 0) return;

        // FIXED: Use the local CARD_WIDTH_FIXED constant
        int calculatedColumns = Math.max(1, width / CARD_WIDTH_FIXED);

        if (calculatedColumns != preferredColumns) {
            preferredColumns = calculatedColumns;
            contentPanel.setLayout(new GridLayout(0, preferredColumns, 15, 15));
            revalidate();
            repaint();
        }
    }

    private void populateRestaurants(List<RestaurantPanel.RestaurantDisplayData> displayDataList) {
        contentPanel.removeAll();

        for (RestaurantPanel.RestaurantDisplayData displayData : displayDataList) {
            RestaurantPanel panel = new RestaurantPanel(displayData);
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            if (this.heartListener != null) {
                panel.setHeartClickListener(this.heartListener);
            }

            contentPanel.add(panel);
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public void updateRestaurants(List<RestaurantPanel.RestaurantDisplayData> displayDataList, RestaurantPanel.HeartClickListener heartListener) {
        populateRestaurants(displayDataList);
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }
}
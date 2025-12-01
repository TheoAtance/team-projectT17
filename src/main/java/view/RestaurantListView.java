package view;

import data_access.UserDataAccessInterface;
import entity.User;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * A scrollable panel that displays a grid of RestaurantPanel cards. Supports responsive resizing –
 * the number of columns adjusts based on window width.
 */
public class RestaurantListView extends JPanel {

    private static final int CARD_WIDTH_FIXED = 280;
    private final JPanel contentPanel;
    private final JScrollPane scrollPane;
    private RestaurantPanel.HeartClickListener heartListener;

    // For checking favorite status
    private UserDataAccessInterface userDataAccess;
    private String userId;

    private int preferredColumns = 5;

    public RestaurantListView(List<RestaurantPanel.RestaurantDisplayData> displayDataList,
                              RestaurantPanel.HeartClickListener heartListener) {
        setLayout(new BorderLayout());
        this.heartListener = heartListener;

        contentPanel = new JPanel();
        contentPanel.setOpaque(false);

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

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                adjustColumns();
            }
        });

        populateRestaurants(displayDataList);
    }

    public RestaurantListView(RestaurantPanel.HeartClickListener heartListener) {
        this(new ArrayList<>(), heartListener);
    }

    private void adjustColumns() {
        int width = getWidth();
        if (width <= 0) {
            return;
        }

        int calculatedColumns = Math.max(1, width / CARD_WIDTH_FIXED);

        if (calculatedColumns != preferredColumns) {
            preferredColumns = calculatedColumns;
            contentPanel.setLayout(new GridLayout(0, preferredColumns, 15, 15));
            revalidate();
            repaint();
        }
    }

    /**
     * Checks if a restaurant is in the user's favorites.
     */
    private boolean isRestaurantFavorited(String restaurantId) {
        if (userDataAccess == null || userId == null || userId.isEmpty()) {
            return false;
        }
        try {
            User user = userDataAccess.getUser(userId);
            if (user != null) {
                List<String> favorites = user.getFavoriteRestaurantIds();
                // Check for exact match or if the restaurant ID is contained in any favorite
                for (String favId : favorites) {
                    if (favId.equals(restaurantId) || restaurantId.equals(favId)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error checking favorite status: " + e.getMessage());
        }
        return false;
    }

    private void populateRestaurants(List<RestaurantPanel.RestaurantDisplayData> displayDataList) {
        contentPanel.removeAll();

        for (RestaurantPanel.RestaurantDisplayData displayData : displayDataList) {
            RestaurantPanel panel = new RestaurantPanel(displayData);
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Check if this restaurant is favorited and set the heart state
            boolean isFavorited = isRestaurantFavorited(displayData.getId());
            panel.setFavorite(isFavorited);

            System.out.println("DEBUG RestaurantListView: " + displayData.getName() +
                    " (ID: " + displayData.getId() + ") - Favorited: " + isFavorited);

            if (this.heartListener != null) {
                panel.setHeartClickListener(this.heartListener);
            }

            contentPanel.add(panel);
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    /**
     * Updates the restaurant list with a new list and heart listener.
     */
    public void updateRestaurants(List<RestaurantPanel.RestaurantDisplayData> displayDataList,
                                  RestaurantPanel.HeartClickListener heartListener) {
        // FIXED: Actually use the passed heartListener
        this.heartListener = heartListener;
        populateRestaurants(displayDataList);
    }

    /**
     * Sets the user data access for checking favorite status.
     */
    public void setUserDataAccess(UserDataAccessInterface userDataAccess) {
        this.userDataAccess = userDataAccess;
    }

    /**
     * Sets the current user ID for checking favorite status.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }
}
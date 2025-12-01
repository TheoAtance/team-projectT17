package view;

import data_access.JsonRestaurantDataAccessObject;
import entity.Restaurant;
import interface_adapter.ImageDataAccessInterface;
import interface_adapter.ViewManagerModel;
import interface_adapter.filter.FilterController;
import interface_adapter.filter.FilterState;
import interface_adapter.filter.FilterViewModel;
import interface_adapter.view_restaurant.ViewRestaurantController;
import interface_adapter.view_restaurant.ViewRestaurantViewModel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.List;

/**
 * View for filtering restaurants by type.
 * Displays filtered restaurants using RestaurantPanel cards with images.
 */
public class FilterView extends JPanel implements PropertyChangeListener {
    public static final String VIEW_NAME = "filter";

    private final FilterViewModel filterViewModel;
    private FilterController filterController;
    private ViewManagerModel viewManagerModel;
    private ViewRestaurantController viewRestaurantController;
    private ViewRestaurantViewModel viewRestaurantViewModel;

    // Data access for images and restaurant info
    private ImageDataAccessInterface imageDataAccess;
    private JsonRestaurantDataAccessObject restaurantDataAccess;
    private String apiKey;

    // Map restaurant names to their CIDs for navigation
    private java.util.Map<String, String> nameToCidMap = new java.util.HashMap<>();

    private final JLabel titleLabel;
    private final JButton backButton;
    private final JPanel buttonPanel;
    private final JPanel restaurantCardsPanel;
    private final JScrollPane scrollPane;
    private final JLabel filterTypeLabel;

    public FilterView(FilterViewModel filterViewModel) {
        this.filterViewModel = filterViewModel;
        this.filterViewModel.addPropertyChangeListener(this);

        // Get API key from environment
        this.apiKey = System.getenv("PLACES_API_TOKEN");

        // Set layout for the main panel
        setLayout(new BorderLayout());
        setBackground(new Color(249, 250, 251));

        // Top panel with back button, title, and filter buttons
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header section (back button, title, filter type label)
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(Color.WHITE);

        // Back button
        backButton = new JButton("← Back");
        backButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(evt -> {
            if (viewManagerModel != null) {
                viewManagerModel.setState("logged in");
                viewManagerModel.firePropertyChange();
            } else {
                JOptionPane.showMessageDialog(this, "View Manager not initialized.");
            }
        });

        // Title
        titleLabel = new JLabel(FilterViewModel.TITLE_LABEL);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Filter type label
        filterTypeLabel = new JLabel("Select a restaurant type");
        filterTypeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        filterTypeLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        filterTypeLabel.setForeground(new Color(107, 114, 128));

        headerPanel.add(backButton);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(filterTypeLabel);
        headerPanel.add(Box.createVerticalStrut(15));

        // Button panel for filter type buttons
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        topPanel.add(headerPanel, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.CENTER);

        // Restaurant cards panel with FlowLayout for proper wrapping and scrolling
        restaurantCardsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20)) {
            @Override
            public Dimension getPreferredSize() {
                return getWrappedPreferredSize(this);
            }
        };
        restaurantCardsPanel.setBackground(new Color(249, 250, 251));
        restaurantCardsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Scroll pane for restaurant cards
        scrollPane = new JScrollPane(restaurantCardsPanel);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Force the panel to resize when viewport changes
        scrollPane.getViewport().addChangeListener(e -> {
            restaurantCardsPanel.revalidate();
        });

        // Add components to main panel
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Calculates the preferred size for a FlowLayout panel that wraps properly in a scroll pane.
     */
    private Dimension getWrappedPreferredSize(JPanel panel) {
        int width = panel.getParent() != null ? panel.getParent().getWidth() : panel.getWidth();
        if (width == 0) {
            width = 800; // Default width
        }

        LayoutManager layout = panel.getLayout();
        if (!(layout instanceof FlowLayout)) {
            return panel.getPreferredSize();
        }

        FlowLayout flowLayout = (FlowLayout) layout;
        int hgap = flowLayout.getHgap();
        int vgap = flowLayout.getVgap();

        Insets insets = panel.getInsets();
        int maxWidth = width - insets.left - insets.right;

        int x = 0;
        int y = insets.top + vgap;
        int rowHeight = 0;

        for (Component comp : panel.getComponents()) {
            Dimension d = comp.getPreferredSize();

            if (x + d.width > maxWidth && x > 0) {
                // Wrap to next row
                x = 0;
                y += rowHeight + vgap;
                rowHeight = 0;
            }

            x += d.width + hgap;
            rowHeight = Math.max(rowHeight, d.height);
        }

        y += rowHeight + insets.bottom + vgap;

        return new Dimension(width, y);
    }

    /**
     * Fetches an image for a restaurant.
     */
    private BufferedImage fetchRestaurantImage(Restaurant restaurant) {
        if (imageDataAccess == null || apiKey == null || apiKey.isEmpty()) {
            return getPlaceholderImage();
        }

        try {
            if (restaurant != null && restaurant.getPhotoIds() != null && !restaurant.getPhotoIds().isEmpty()) {
                String photoId = restaurant.getPhotoIds().get(0);
                BufferedImage image = imageDataAccess.fetchRestaurantImage(photoId, apiKey);
                if (image != null) {
                    return image;
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching image for " + restaurant.getName() + ": " + e.getMessage());
        }

        return getPlaceholderImage();
    }

    /**
     * Gets a placeholder image.
     */
    private BufferedImage getPlaceholderImage() {
        try {
            return ImageIO.read(getClass().getResource("/images/placeholder.png"));
        } catch (Exception e) {
            // Return a simple colored placeholder
            BufferedImage placeholder = new BufferedImage(280, 140, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = placeholder.createGraphics();
            g2d.setColor(new Color(255, 220, 220));
            g2d.fillRect(0, 0, 280, 140);
            g2d.dispose();
            return placeholder;
        }
    }

    /**
     * Initialize filter buttons based on available restaurant types.
     * This should be called after the controller is set.
     */
    public void initializeFilterButtons() {
        if (filterController == null) {
            return;
        }

        buttonPanel.removeAll();
        String[] types = filterController.getAvailableTypes();

        if (types.length == 0) {
            JLabel noTypesLabel = new JLabel("No restaurant types available");
            noTypesLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            buttonPanel.add(noTypesLabel);
        } else {
            for (String type : types) {
                JButton typeButton = createFilterButton(type);
                typeButton.addActionListener(e -> filterController.execute(type));
                buttonPanel.add(typeButton);
            }
        }

        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    private JButton createFilterButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(239, 68, 68));
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setOpaque(true); // IMPORTANT for custom background colors
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(180, 40));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(220, 50, 50));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(239, 68, 68));
            }
        });

        return button;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        FilterState state = (FilterState) evt.getNewValue();
        updateView(state);
    }

    private void updateView(FilterState state) {
        if (state.getErrorMessage() != null && !state.getErrorMessage().isEmpty()) {
            JOptionPane.showMessageDialog(this, state.getErrorMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Update filter type label
        if (!state.getCurrentFilterType().isEmpty()) {
            filterTypeLabel.setText("Showing: " + state.getCurrentFilterType());
        }

        // Clear previous restaurant cards
        restaurantCardsPanel.removeAll();

        // Reset to FlowLayout in case it was changed
        if (!(restaurantCardsPanel.getLayout() instanceof FlowLayout)) {
            restaurantCardsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));
        }

        // Get restaurant names from state
        List<String> restaurantNames = state.getRestaurantNames();

        if (restaurantNames.isEmpty()) {
            JLabel emptyLabel = new JLabel("No restaurants found for this type.");
            emptyLabel.setFont(new Font("Arial", Font.ITALIC, 16));
            emptyLabel.setForeground(new Color(107, 114, 128));
            restaurantCardsPanel.add(emptyLabel);
        } else {
            // Create RestaurantPanel for each restaurant
            for (String restaurantName : restaurantNames) {
                // Try to get real restaurant data
                Restaurant restaurant = findRestaurantByName(restaurantName);

                RestaurantPanel.RestaurantDisplayData displayData;
                BufferedImage image = null;
                String restaurantId;

                if (restaurant != null) {
                    // Use real data
                    restaurantId = getRestaurantId(restaurant);
                    displayData = new RestaurantPanel.RestaurantDisplayData(
                            restaurantId,
                            restaurant.getName(),
                            restaurant.getType(),
                            restaurant.getRating(),
                            false,
                            0.0
                    );
                    image = fetchRestaurantImage(restaurant);
                } else {
                    // Fallback to placeholder data
                    restaurantId = restaurantName;
                    displayData = new RestaurantPanel.RestaurantDisplayData(
                            restaurantName,
                            restaurantName,
                            state.getCurrentFilterType(),
                            4.5,
                            false,
                            0.0
                    );
                }

                RestaurantPanel restaurantPanel = new RestaurantPanel(displayData, image);

                // Set up click listener to navigate to RestaurantView
                final String finalRestaurantId = restaurantId;
                restaurantPanel.setRestaurantClickListener((id, data) -> {
                    navigateToRestaurantView(finalRestaurantId);
                });

                restaurantCardsPanel.add(restaurantPanel);
            }
        }

        restaurantCardsPanel.revalidate();
        restaurantCardsPanel.repaint();
        scrollPane.revalidate();
        scrollPane.repaint();
    }

    /**
     * Finds a restaurant by name from the data access.
     */
    private Restaurant findRestaurantByName(String name) {
        if (restaurantDataAccess == null) {
            return null;
        }

        List<Restaurant> allRestaurants = restaurantDataAccess.getAllRestaurants();
        for (Restaurant restaurant : allRestaurants) {
            if (restaurant.getName().equals(name)) {
                return restaurant;
            }
        }
        return null;
    }

    /**
     * Gets the restaurant ID (CID) from a restaurant by searching the data access.
     */
    private String getRestaurantId(Restaurant restaurant) {
        if (restaurantDataAccess == null) {
            return restaurant.getName();
        }

        // Use the getCidByName method to get the CID
        String cid = restaurantDataAccess.getCidByName(restaurant.getName());
        if (cid != null) {
            return cid;
        }

        // Fallback to name - the DAO's normalizeId should handle it
        return restaurant.getName();
    }

    /**
     * Navigates to the RestaurantView for the given restaurant.
     */
    private void navigateToRestaurantView(String restaurantId) {
        if (viewRestaurantController != null && viewManagerModel != null && viewRestaurantViewModel != null) {
            try {
                viewRestaurantController.execute(restaurantId);
                viewManagerModel.setState(viewRestaurantViewModel.getViewName());
                viewManagerModel.firePropertyChange();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                        "Failed to load restaurant details: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "View Restaurant Controller not initialized.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public String getViewName() {
        return VIEW_NAME;
    }

    public void setFilterController(FilterController filterController) {
        this.filterController = filterController;
        initializeFilterButtons();
    }

    public void setViewManagerModel(ViewManagerModel viewManagerModel) {
        this.viewManagerModel = viewManagerModel;
    }

    public void setViewRestaurantController(ViewRestaurantController viewRestaurantController) {
        this.viewRestaurantController = viewRestaurantController;
    }

    public void setViewRestaurantViewModel(ViewRestaurantViewModel viewRestaurantViewModel) {
        this.viewRestaurantViewModel = viewRestaurantViewModel;
    }

    public void setImageDataAccess(ImageDataAccessInterface imageDataAccess) {
        this.imageDataAccess = imageDataAccess;
    }

    public void setRestaurantDataAccess(JsonRestaurantDataAccessObject restaurantDataAccess) {
        this.restaurantDataAccess = restaurantDataAccess;

        // Build name-to-CID map for navigation
        // We need to iterate through all restaurants and find their CIDs
        if (restaurantDataAccess != null) {
            nameToCidMap.clear();
            List<Restaurant> allRestaurants = restaurantDataAccess.getAllRestaurants();
            for (Restaurant restaurant : allRestaurants) {
                // Try to find this restaurant's CID by checking if we can look it up
                // The restaurant name becomes the key, and we'll try to find the actual ID
                String name = restaurant.getName();
                // For now, just store name -> name mapping
                // The actual CID lookup will happen in getRestaurantId
                nameToCidMap.put(name, name);
            }
        }
    }
}
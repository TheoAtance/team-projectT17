package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.filter.FilterController;
import interface_adapter.filter.FilterState;
import interface_adapter.filter.FilterViewModel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

/**
 * View for filtering restaurants by type.
 * Displays filtered restaurants using RestaurantPanel cards.
 */
public class FilterView extends JPanel implements PropertyChangeListener {
    public static final String VIEW_NAME = "filter";

    private final FilterViewModel filterViewModel;
    private FilterController filterController;
    private ViewManagerModel viewManagerModel;

    private final JLabel titleLabel;
    private final JButton backButton;
    private final JPanel buttonPanel;
    private final JPanel restaurantCardsPanel;
    private final JScrollPane scrollPane;
    private final JLabel filterTypeLabel;

    public FilterView(FilterViewModel filterViewModel) {
        this.filterViewModel = filterViewModel;
        this.filterViewModel.addPropertyChangeListener(this);

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
        buttonPanel.setBorder(BorderFactory.createLineBorder(Color.RED, 2)); // Debug border

        topPanel.add(headerPanel, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.CENTER);

        // Restaurant cards panel with grid layout
        restaurantCardsPanel = new JPanel();
        restaurantCardsPanel.setLayout(new GridLayout(0, 3, 20, 20)); // 3 columns, auto rows
        restaurantCardsPanel.setBackground(new Color(249, 250, 251));
        restaurantCardsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Scroll pane for restaurant cards
        scrollPane = new JScrollPane(restaurantCardsPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Add components to main panel
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Initialize filter buttons based on available restaurant types.
     * This should be called after the controller is set.
     */
    public void initializeFilterButtons() {
        System.out.println("initializeFilterButtons() called");

        if (filterController == null) {
            System.out.println("FilterController is null!");
            return;
        }

        buttonPanel.removeAll();
        String[] types = filterController.getAvailableTypes();

        System.out.println("Available types in FilterView: " + types.length);
        for (String type : types) {
            System.out.println("  - Creating button for: " + type);
        }

        if (types.length == 0) {
            JLabel noTypesLabel = new JLabel("No restaurant types available");
            noTypesLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            buttonPanel.add(noTypesLabel);
        } else {
            for (String type : types) {
                JButton typeButton = createFilterButton(type);
                typeButton.addActionListener(e -> filterController.execute(type));
                buttonPanel.add(typeButton);
                System.out.println("  ✓ Button added to panel: " + type);
            }
        }

        System.out.println("Button panel component count: " + buttonPanel.getComponentCount());
        buttonPanel.revalidate();
        buttonPanel.repaint();
        System.out.println("Button panel revalidated and repainted");
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

        System.out.println("Created button: " + text + " with size: " + button.getPreferredSize());

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
                // Create display data for the restaurant panel
                // Note: We only have the name from the state, so we'll use placeholder data
                RestaurantPanel.RestaurantDisplayData displayData =
                        new RestaurantPanel.RestaurantDisplayData(
                                restaurantName,           // id (using name as placeholder)
                                restaurantName,           // name
                                state.getCurrentFilterType(), // type
                                4.5,                      // rating (placeholder)
                                false,                    // hasDiscount (placeholder)
                                0.0                       // discountValue
                        );

                RestaurantPanel restaurantPanel = new RestaurantPanel(displayData);
                restaurantCardsPanel.add(restaurantPanel);
            }
        }

        restaurantCardsPanel.revalidate();
        restaurantCardsPanel.repaint();
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
}
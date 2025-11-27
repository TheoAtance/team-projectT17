package view;

import interface_adapter.filter.FilterController;
import interface_adapter.filter.FilterState;
import interface_adapter.filter.FilterViewModel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * View for filtering restaurants by type.
 */
public class FilterView extends JPanel implements PropertyChangeListener {
    public static final String VIEW_NAME = "filter";

    private final FilterViewModel filterViewModel;
    private FilterController filterController;

    private final JLabel titleLabel;
    private final JPanel buttonPanel;
    private final JPanel resultPanel;
    private final JLabel filterTypeLabel;
    private final JTextArea restaurantListArea;
    private final JScrollPane scrollPane;

    public FilterView(FilterViewModel filterViewModel) {
        this.filterViewModel = filterViewModel;
        this.filterViewModel.addPropertyChangeListener(this);

        // Title
        titleLabel = new JLabel(FilterViewModel.TITLE_LABEL);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        // Button panel for filter buttons
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        // Result panel
        resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));

        filterTypeLabel = new JLabel("Select a restaurant type");
        filterTypeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        filterTypeLabel.setFont(new Font("Arial", Font.ITALIC, 14));

        restaurantListArea = new JTextArea(15, 40);
        restaurantListArea.setEditable(false);
        restaurantListArea.setFont(new Font("Arial", Font.PLAIN, 12));
        scrollPane = new JScrollPane(restaurantListArea);

        resultPanel.add(filterTypeLabel);
        resultPanel.add(Box.createVerticalStrut(10));
        resultPanel.add(scrollPane);

        // Main layout
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(Box.createVerticalStrut(20));
        this.add(titleLabel);
        this.add(Box.createVerticalStrut(20));
        this.add(buttonPanel);
        this.add(Box.createVerticalStrut(20));
        this.add(resultPanel);
        this.add(Box.createVerticalStrut(20));
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

        for (String type : types) {
            JButton typeButton = new JButton(type);
            typeButton.addActionListener(e -> filterController.execute(type));
            buttonPanel.add(typeButton);
        }

        buttonPanel.revalidate();
        buttonPanel.repaint();
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

        // Update restaurant list
        StringBuilder sb = new StringBuilder();
        java.util.List<String> names = state.getRestaurantNames();

        if (names.isEmpty()) {
            sb.append("No restaurants found for this type.");
        } else {
            for (int i = 0; i < names.size(); i++) {
                sb.append((i + 1)).append(". ").append(names.get(i)).append("\n");
            }
        }

        restaurantListArea.setText(sb.toString());
    }

    public String getViewName() {
        return VIEW_NAME;
    }

    public void setFilterController(FilterController filterController) {
        this.filterController = filterController;
        initializeFilterButtons();
    }
}
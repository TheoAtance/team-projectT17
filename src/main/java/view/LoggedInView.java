package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.logged_in.LoggedInState;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.logout.LogoutController;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * The View displayed after successful login.
 * Shows user information and provides logout functionality.
 */
public class LoggedInView extends JPanel implements PropertyChangeListener {
    public static final String VIEW_NAME = "logged in";
    private final LoggedInViewModel loggedInViewModel;
    private final JLabel welcomeLabel;
    private final JLabel uidLabel;
    private final JButton logoutButton;
    private final JButton filterViewButton;

    private LogoutController logoutController;
    private ViewManagerModel viewManagerModel;

    public LoggedInView(LoggedInViewModel loggedInViewModel) {
        this.loggedInViewModel = loggedInViewModel;
        this.loggedInViewModel.addPropertyChangeListener(this);

        final JLabel title = new JLabel(LoggedInViewModel.TITLE_LABEL);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Arial", Font.BOLD, 24));

        welcomeLabel = new JLabel();
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 18));

        uidLabel = new JLabel();
        uidLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        uidLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        uidLabel.setForeground(Color.GRAY);

        logoutButton = new JButton("Logout");
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.addActionListener(evt -> {
            if (logoutController != null) {
                logoutController.execute();
            } else {
                JOptionPane.showMessageDialog(this, "Logout Controller not initialized.");
            }
        });

        filterViewButton = new JButton("Filter Restaurants");
        filterViewButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        filterViewButton.addActionListener(evt -> {
            if (viewManagerModel != null) {
                viewManagerModel.setState("filter");
                viewManagerModel.firePropertyChange();
            } else {
                JOptionPane.showMessageDialog(this, "View Manager not initialized.");
            }
        });

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(Box.createVerticalStrut(50));
        this.add(title);
        this.add(Box.createVerticalStrut(20));
        this.add(welcomeLabel);
        this.add(Box.createVerticalStrut(10));
        this.add(uidLabel);
        this.add(Box.createVerticalStrut(30));
        this.add(filterViewButton);
        this.add(Box.createVerticalStrut(10));
        this.add(logoutButton);

        // Initialize with current state
        updateView(loggedInViewModel.getState());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        final LoggedInState state = (LoggedInState) evt.getNewValue();
        updateView(state);
    }

    private void updateView(LoggedInState state) {
        if (state.getNickname() != null && !state.getNickname().isEmpty()) {
            welcomeLabel.setText("Welcome, " + state.getNickname() + "!");
        } else {
            welcomeLabel.setText("Welcome!");
        }

        if (state.getUid() != null && !state.getUid().isEmpty()) {
            uidLabel.setText("User ID: " + state.getUid());
        } else {
            uidLabel.setText("");
        }
    }

    public String getViewName() {
        return VIEW_NAME;
    }

    public void setLogoutController(LogoutController logoutController) {
        this.logoutController = logoutController;
    }

    public void setViewManagerModel(ViewManagerModel viewManagerModel) {
        this.viewManagerModel = viewManagerModel;
    }
}
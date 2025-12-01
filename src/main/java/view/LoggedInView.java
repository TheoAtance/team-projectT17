package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.list_search.ListSearchController;
import interface_adapter.list_search.ListSearchState;
import interface_adapter.list_search.ListSearchViewModel;
import interface_adapter.logged_in.LoggedInState;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.logout.LogoutController;
import interface_adapter.random_restauarant.RandomRestaurantController;
import interface_adapter.view_restaurant.ViewRestaurantController;
import interface_adapter.view_restaurant.ViewRestaurantViewModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * The View displayed after successful login. Shows user information, provides logout functionality,
 * and displays searchable restaurant list.
 */
public class LoggedInView extends JPanel implements PropertyChangeListener {

  public static final String VIEW_NAME = "logged in";

  // ViewModels for observing changes
  private final LoggedInViewModel loggedInViewModel;
  // UI Components for LoggedInView specific elements
  private final JLabel welcomeLabel;
  private final JLabel uidLabel;
  private final JButton logoutButton;
  private final JButton filterViewButton;
  private final JButton randomRestaurantButton;
  // UI Components for the search/restaurant list
  private final JTextField searchField;
  private final RestaurantListView restaurantListView;
  private ListSearchViewModel listSearchViewModel;
  // Controllers for actions
  private LogoutController logoutController;
  private ViewRestaurantController viewRestaurantController;
  private RandomRestaurantController randomRestaurantController;
  private ViewManagerModel viewManagerModel;
  private ViewRestaurantViewModel viewRestaurantViewModel;
  private ListSearchController searchController;
  private RestaurantPanel.HeartClickListener heartListener;

  private String filterViewName;

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
      if (viewManagerModel != null && filterViewName != null) {
        viewManagerModel.setState(this.filterViewName);
        viewManagerModel.firePropertyChange();
      } else {
        JOptionPane.showMessageDialog(this, "View Manager not initialized.");
      }
    });

    randomRestaurantButton = new JButton("Random Restaurant");
    randomRestaurantButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    randomRestaurantButton.addActionListener(ect -> {
      if (viewRestaurantController != null) {
        try {
          randomRestaurantController.execute();
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
      if (viewManagerModel != null) {
        System.out.println(
            "Changing viewManager state to: " + viewRestaurantViewModel.getViewName());
        viewManagerModel.setState(viewRestaurantViewModel.getViewName());
        viewManagerModel.firePropertyChange();
      } else {
        JOptionPane.showMessageDialog(this, "ViewManager not initialized");
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
    this.add(randomRestaurantButton);
    this.add(Box.createVerticalStrut(10));
    this.add(logoutButton);

    JPanel restaurantSection = new JPanel();
    restaurantSection.setLayout(new BoxLayout(restaurantSection, BoxLayout.Y_AXIS));
    restaurantSection.setAlignmentX(Component.CENTER_ALIGNMENT);
    restaurantSection.setMaximumSize(new Dimension(Integer.MAX_VALUE, 500));

    JPanel searchPanel = new JPanel();
    searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
    searchPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
    searchPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

    JLabel searchLabel = new JLabel("Search:");
    searchLabel.setFont(new Font("Arial", Font.PLAIN, 14));
    searchPanel.add(searchLabel);

    searchField = new JTextField();
    int maxFieldWidth = 400;
    searchField.setPreferredSize(new Dimension(maxFieldWidth, 24));
    searchField.setMaximumSize(new Dimension(maxFieldWidth, 24));
    searchPanel.add(searchField);

    searchField.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
        triggerSearch();
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        triggerSearch();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        triggerSearch();
      }

      private void triggerSearch() {
        if (LoggedInView.this.searchController == null) {
          return;
        }
        String query = searchField.getText().trim();
        LoggedInView.this.searchController.search(query);
      }
    });

    restaurantSection.add(searchPanel);
    restaurantSection.add(Box.createVerticalStrut(10));

    restaurantListView = new RestaurantListView(new ArrayList<>(), null);
    JScrollPane restaurantScrollPane = restaurantListView.getScrollPane();
    restaurantScrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
    restaurantSection.add(restaurantScrollPane, BorderLayout.CENTER);

    this.add(Box.createVerticalStrut(30));
    this.add(restaurantSection);

    updateLoggedInView(loggedInViewModel.getState());
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    if (evt.getSource() == loggedInViewModel) {
      LoggedInState state = (LoggedInState) evt.getNewValue();
      updateLoggedInView(state);
    } else if (evt.getSource() == listSearchViewModel) {
      ListSearchState state = (ListSearchState) evt.getNewValue();
      updateListSearchView(state);
    }
  }

  private void updateLoggedInView(LoggedInState state) {
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

  private void updateListSearchView(ListSearchState state) {
    if (state.getErrorMessage() != null && !state.getErrorMessage().isEmpty()) {
      JOptionPane.showMessageDialog(this, state.getErrorMessage());
      state.setErrorMessage(null);
    }

    if (restaurantListView != null && heartListener != null) {
      restaurantListView.updateRestaurants(state.getFilteredRestaurants(), this.heartListener);
    }
  }

  public String getViewName() {
    return VIEW_NAME;
  }

  public RestaurantListView getRestaurantListView() {
    return restaurantListView;
  }

  public void setLogoutController(LogoutController logoutController) {
    this.logoutController = logoutController;
  }

  public void setRandomRestaurantController(RandomRestaurantController randomRestaurantController) {
    this.randomRestaurantController = randomRestaurantController;
  }

  public ViewRestaurantController getViewRestaurantController() {
    return viewRestaurantController;
  }

  public void setViewRestaurantController(ViewRestaurantController viewRestaurantController) {
    this.viewRestaurantController = viewRestaurantController;
  }

  public void setViewManagerModel(ViewManagerModel viewManagerModel) {
    this.viewManagerModel = viewManagerModel;
  }

  public void setViewRestaurantViewModel(ViewRestaurantViewModel viewRestaurantViewModel) {
    this.viewRestaurantViewModel = viewRestaurantViewModel;
  }

  public void setSearchController(ListSearchController searchController) {
    this.searchController = searchController;
  }

  public void setListSearchViewModel(ListSearchViewModel listSearchViewModel) {
    this.listSearchViewModel = listSearchViewModel;
    if (this.listSearchViewModel != null) {
      this.listSearchViewModel.addPropertyChangeListener(this);
      updateListSearchView(listSearchViewModel.getState());
    }
  }

  public void setHeartListener(RestaurantPanel.HeartClickListener heartListener) {
    this.heartListener = heartListener;
    if (restaurantListView != null && this.heartListener != null) {
      restaurantListView.updateRestaurants(
          listSearchViewModel != null ? listSearchViewModel.getState().getFilteredRestaurants()
              : new ArrayList<>(),
          this.heartListener
      );
    }
  }

  public void setFilterViewName(String filterViewName) {
    this.filterViewName = filterViewName;
  }

  public void setLoadingCursor() {
    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
  }
}
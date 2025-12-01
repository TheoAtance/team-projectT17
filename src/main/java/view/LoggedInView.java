package view;

import data_access.JsonRestaurantDataAccessObject;
import data_access.UserDataAccessInterface;
import entity.Restaurant;
import entity.User;
import interface_adapter.ImageDataAccessInterface;
import interface_adapter.ViewManagerModel;
import interface_adapter.favorites.AddFavoriteController;
import interface_adapter.favorites.FavoritesViewModel;
import interface_adapter.favorites.GetFavoritesController;
import interface_adapter.favorites.RemoveFavoriteController;
import interface_adapter.list_search.ListSearchController;
import interface_adapter.list_search.ListSearchState;
import interface_adapter.list_search.ListSearchViewModel;
import interface_adapter.logged_in.LoggedInState;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.logout.LogoutController;
import interface_adapter.random_restauarant.RandomRestaurantController;
import interface_adapter.view_restaurant.ViewRestaurantController;
import interface_adapter.view_restaurant.ViewRestaurantViewModel;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * The View displayed after successful login. Shows user information, provides logout functionality,
 * and displays searchable restaurant list with images and favorites support.
 */
public class LoggedInView extends JPanel implements PropertyChangeListener {

    public static final String VIEW_NAME = "logged in";

    // ViewModels
    private final LoggedInViewModel loggedInViewModel;

    // UI Components
    private final JLabel welcomeLabel;
    private final JLabel uidLabel;
    private final JButton logoutButton;
    private final JButton filterViewButton;
    private final JButton randomRestaurantButton;
    private final JButton favoritesButton;
    private final JButton searchButton;
    private String searchHolder = "";
    private final JTextField searchField;
    private final JPanel restaurantGridPanel;
    private final JScrollPane restaurantScrollPane;

    private ListSearchViewModel listSearchViewModel;

    // Controllers
    private LogoutController logoutController;
    private ViewRestaurantController viewRestaurantController;
    private RandomRestaurantController randomRestaurantController;
    private ViewManagerModel viewManagerModel;
    private ViewRestaurantViewModel viewRestaurantViewModel;
    private ListSearchController searchController;

    // Favorites
    private FavoritesViewModel favoritesViewModel;
    private GetFavoritesController getFavoritesController;
    private AddFavoriteController addFavoriteController;
    private RemoveFavoriteController removeFavoriteController;
    private UserDataAccessInterface userDataAccess;

    // Image and restaurant data loading
    private ImageDataAccessInterface imageDataAccess;
    private JsonRestaurantDataAccessObject restaurantDataAccess;
    private String apiKey;

    private String filterViewName;

    public LoggedInView(LoggedInViewModel loggedInViewModel) {
        this.loggedInViewModel = loggedInViewModel;
        this.loggedInViewModel.addPropertyChangeListener(this);

        // Get API key from environment
        this.apiKey = System.getenv("PLACES_API_TOKEN");
        if (this.apiKey == null || this.apiKey.isEmpty()) {
            System.err.println("WARNING: PLACES_API_TOKEN environment variable not set!");
        } else {
            System.out.println("DEBUG: API key loaded successfully");
        }

        setLayout(new BorderLayout());

        // Top panel with welcome info and buttons
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

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
        randomRestaurantButton.addActionListener(evt -> {
            if (randomRestaurantController != null) {
                try {
                    randomRestaurantController.execute();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (viewManagerModel != null && viewRestaurantViewModel != null) {
                viewManagerModel.setState(viewRestaurantViewModel.getViewName());
                viewManagerModel.firePropertyChange();
            } else {
                JOptionPane.showMessageDialog(this, "ViewManager not initialized");
            }
        });

        // Favorites button
        favoritesButton = new JButton("♥ My Favorites");
        favoritesButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        favoritesButton.setFont(new Font("Arial", Font.BOLD, 14));
        favoritesButton.setForeground(new Color(236, 72, 153));
        favoritesButton.addActionListener(evt -> {
            if (viewManagerModel != null && getFavoritesController != null) {
                String userId = loggedInViewModel.getState().getUid();
                if (userId != null && !userId.isEmpty()) {
                    if (favoritesViewModel != null) {
                        favoritesViewModel.getState().setUserId(userId);
                        favoritesViewModel.getState().setUsername(loggedInViewModel.getState().getNickname());
                    }
                    getFavoritesController.execute(userId);
                    viewManagerModel.setState(FavoritesView.VIEW_NAME);
                    viewManagerModel.firePropertyChange();
                } else {
                    JOptionPane.showMessageDialog(this, "Please log in to view favorites.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Favorites not initialized.");
            }
        });

        searchButton = new JButton("\uD83D\uDD0D");
        searchButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
        searchButton.addActionListener(evt -> {
          if(searchHolder != null && searchController != null) {
            searchController.search(searchHolder);
          }
          else {
            JOptionPane.showMessageDialog(this, "Search bar is empty.");
          }
        });

        // Search panel
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        searchPanel.setBackground(Color.WHITE);

        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(400, 28));
        searchPanel.add(searchField);
        searchPanel.add(searchButton, BorderLayout.EAST);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
              searchHolder = searchField.getText();

            }
            @Override
            public void removeUpdate(DocumentEvent e) {
              searchHolder = searchField.getText();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
              searchHolder = searchField.getText();
            }

            private void triggerSearch() {
                if (searchController != null) {
                    searchController.search(searchField.getText().trim());
                }
            }
        });

        // Add components to top panel
        topPanel.add(title);
        topPanel.add(Box.createVerticalStrut(15));
        topPanel.add(welcomeLabel);
        topPanel.add(Box.createVerticalStrut(5));
        topPanel.add(uidLabel);
        topPanel.add(Box.createVerticalStrut(20));
        topPanel.add(filterViewButton);
        topPanel.add(Box.createVerticalStrut(8));
        topPanel.add(randomRestaurantButton);
        topPanel.add(Box.createVerticalStrut(8));
        topPanel.add(favoritesButton);
        topPanel.add(Box.createVerticalStrut(8));
        topPanel.add(logoutButton);
        topPanel.add(Box.createVerticalStrut(15));
        topPanel.add(searchPanel);

        // Restaurant grid with custom panel that wraps and scrolls properly
        restaurantGridPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20)) {
            @Override
            public Dimension getPreferredSize() {
                return getWrappedPreferredSize(this);
            }
        };
        restaurantGridPanel.setBackground(new Color(249, 250, 251));
        restaurantGridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        restaurantScrollPane = new JScrollPane(restaurantGridPanel);
        restaurantScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        restaurantScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        restaurantScrollPane.setBorder(null);
        restaurantScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Force the panel to resize when viewport changes
        restaurantScrollPane.getViewport().addChangeListener(e -> {
            restaurantGridPanel.revalidate();
        });

        // Add to main panel
        add(topPanel, BorderLayout.NORTH);
        add(restaurantScrollPane, BorderLayout.CENTER);

        updateLoggedInView(loggedInViewModel.getState());
    }

    /**
     * Creates a heart click listener that adds/removes favorites.
     */
    private RestaurantPanel.HeartClickListener createFavoritesHeartListener() {
        return (restaurantId, newFavoriteState) -> {
            String userId = loggedInViewModel.getState().getUid();
            if (userId == null || userId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please log in to manage favorites.");
                return;
            }

            System.out.println("DEBUG LoggedInView: Heart clicked - ID: " + restaurantId + ", newState: " + newFavoriteState);

            if (newFavoriteState) {
                if (addFavoriteController != null) {
                    addFavoriteController.execute(userId, restaurantId);
                    System.out.println("DEBUG LoggedInView: Called addFavoriteController");
                } else {
                    System.err.println("ERROR: AddFavoriteController is null!");
                }
            } else {
                if (removeFavoriteController != null) {
                    removeFavoriteController.execute(userId, restaurantId);
                    System.out.println("DEBUG LoggedInView: Called removeFavoriteController");
                } else {
                    System.err.println("ERROR: RemoveFavoriteController is null!");
                }
            }
        };
    }

    /**
     * Checks if a restaurant is in the user's favorites.
     */
    private boolean isRestaurantFavorited(String restaurantId) {
        if (userDataAccess == null) {
            return false;
        }
        String userId = loggedInViewModel.getState().getUid();
        if (userId == null || userId.isEmpty()) {
            return false;
        }
        try {
            User user = userDataAccess.getUser(userId);
            if (user != null) {
                List<String> favorites = user.getFavoriteRestaurantIds();
                for (String favId : favorites) {
                    if (favId.equals(restaurantId)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error checking favorite: " + e.getMessage());
        }
        return false;
    }

    /**
     * Fetches an image for a restaurant using the restaurant data access.
     */
    private BufferedImage fetchRestaurantImage(RestaurantPanel.RestaurantDisplayData displayData) {
        // Check if we have necessary dependencies
        if (imageDataAccess == null) {
            System.out.println("DEBUG: imageDataAccess is null");
            return getPlaceholderImage();
        }

        if (apiKey == null || apiKey.isEmpty()) {
            System.out.println("DEBUG: API key is null or empty");
            return getPlaceholderImage();
        }

        if (restaurantDataAccess == null) {
            System.out.println("DEBUG: restaurantDataAccess is null");
            return getPlaceholderImage();
        }

        try {
            // Use restaurantDataAccess to get restaurant data including photo IDs
            Restaurant restaurant = restaurantDataAccess.get(displayData.getId());
            if (restaurant != null && restaurant.getPhotoIds() != null && !restaurant.getPhotoIds().isEmpty()) {
                String photoId = restaurant.getPhotoIds().get(0);
                System.out.println("DEBUG: Fetching image for " + displayData.getName() + " with photoId: " + photoId);
                BufferedImage image = imageDataAccess.fetchRestaurantImage(photoId, apiKey);
                if (image != null) {
                    return image;
                }
            } else {
                System.out.println("DEBUG: No photo IDs for restaurant: " + displayData.getName());
            }
        } catch (Exception e) {
            System.err.println("Error fetching image for " + displayData.getName() + ": " + e.getMessage());
        }

        return getPlaceholderImage();
    }

    /**
     * Gets a placeholder image.
     */
    private BufferedImage getPlaceholderImage() {
        try {
            return ImageIO.read(Objects.requireNonNull(
                    getClass().getResource("/images/placeholder.png")
            ));
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
     * Populates the restaurant grid with panels.
     */
    private void populateRestaurantGrid(List<RestaurantPanel.RestaurantDisplayData> restaurants) {
        restaurantGridPanel.removeAll();

        RestaurantPanel.HeartClickListener heartListener = createFavoritesHeartListener();

        for (RestaurantPanel.RestaurantDisplayData displayData : restaurants) {
            // Fetch image for this restaurant
            BufferedImage image = fetchRestaurantImage(displayData);

            // Create panel with image
            RestaurantPanel panel = new RestaurantPanel(displayData, image);

            // Check if favorited and set heart state
            boolean isFavorited = isRestaurantFavorited(displayData.getId());
            panel.setFavorite(isFavorited);

            // Set heart click listener
            panel.setHeartClickListener(heartListener);

            // Set restaurant click listener to navigate to restaurant view
            panel.setRestaurantClickListener((restaurantId, data) -> {
                if (viewRestaurantController != null && viewManagerModel != null && viewRestaurantViewModel != null) {
                    try {
                        viewRestaurantController.execute(restaurantId);
                        viewManagerModel.setState(viewRestaurantViewModel.getViewName());
                        viewManagerModel.firePropertyChange();
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(this, "Failed to load restaurant: " + e.getMessage());
                    }
                }
            });

            restaurantGridPanel.add(panel);
        }

        restaurantGridPanel.revalidate();
        restaurantGridPanel.repaint();

        // Force scroll pane to update
        restaurantScrollPane.revalidate();
        restaurantScrollPane.repaint();
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

        populateRestaurantGrid(state.getFilteredRestaurants());
    }

    public String getViewName() {
        return VIEW_NAME;
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
        // Not used anymore - we create our own listener
    }

    public void setFilterViewName(String filterViewName) {
        this.filterViewName = filterViewName;
    }

    public void setLoadingCursor() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    // Favorites setters
    public void setFavoritesViewModel(FavoritesViewModel favoritesViewModel) {
        this.favoritesViewModel = favoritesViewModel;
    }

    public void setGetFavoritesController(GetFavoritesController getFavoritesController) {
        this.getFavoritesController = getFavoritesController;
    }

    public void setAddFavoriteController(AddFavoriteController addFavoriteController) {
        this.addFavoriteController = addFavoriteController;
    }

    public void setRemoveFavoriteController(RemoveFavoriteController removeFavoriteController) {
        this.removeFavoriteController = removeFavoriteController;
    }

    public void setUserDataAccess(UserDataAccessInterface userDataAccess) {
        this.userDataAccess = userDataAccess;
    }

    public void setImageDataAccess(ImageDataAccessInterface imageDataAccess) {
        this.imageDataAccess = imageDataAccess;
    }

    public void setRestaurantDataAccess(JsonRestaurantDataAccessObject restaurantDataAccess) {
        this.restaurantDataAccess = restaurantDataAccess;
    }

    /**
     * Calculates the preferred size for a FlowLayout panel that wraps properly in a scroll pane.
     */
    private Dimension getWrappedPreferredSize(JPanel panel) {
        int width = panel.getParent() != null ? panel.getParent().getWidth() : panel.getWidth();
        if (width == 0) {
            width = 800; // Default width
        }

        FlowLayout layout = (FlowLayout) panel.getLayout();
        int hgap = layout.getHgap();
        int vgap = layout.getVgap();

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
}
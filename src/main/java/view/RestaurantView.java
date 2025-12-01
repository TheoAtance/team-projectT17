package view;
import interface_adapter.ViewManagerModel;
import interface_adapter.add_review.AddReviewController;
import interface_adapter.add_review.AddReviewViewModel;
import interface_adapter.display_reviews.DisplayReviewsController;
import interface_adapter.display_reviews.DisplayReviewsStateList;
import interface_adapter.display_reviews.DisplayReviewsViewModel;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.view_restaurant.ViewRestaurantController;
import interface_adapter.view_restaurant.ViewRestaurantState;
import interface_adapter.view_restaurant.ViewRestaurantViewModel;
import interface_adapter.favorites.AddFavoriteController;
import interface_adapter.favorites.RemoveFavoriteController;
import data_access.UserDataAccessInterface;
import entity.User;
import view.panel_makers.PillIconTextPanel;
import view.panel_makers.RestaurantTitlePanel;
import view.panel_makers.ReviewPanel;
import view.panel_makers.RoundedPanel;

import interface_adapter.display_reviews.DisplayReviewsState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;


public class RestaurantView extends JPanel implements ActionListener, PropertyChangeListener{
    private final String viewName = "restaurant info";
    private ViewRestaurantViewModel viewRestaurantViewModel;
    private ViewRestaurantController viewRestaurantController;
    private AddReviewViewModel addReviewViewModel;
    private AddReviewController addReviewController;

    // Favorite controllers
    private AddFavoriteController addFavoriteController;
    private RemoveFavoriteController removeFavoriteController;
    private UserDataAccessInterface userDataAccess;

    private final RestaurantTitlePanel titlePanel;

    private final JPanel imageAndInfoPanel = new JPanel();
    private JPanel infoPanel = new JPanel();
    private BufferedImage image;
    private JLabel imageLabel = new JLabel();
    private JPanel addReviewPanel = new JPanel();

    // Favorite button
    private JButton favoriteButton;
    private boolean isFavorite = false;

    private final JPanel leftPanel = new JPanel();
    private final JPanel rightPanel = new JPanel();
    private final JPanel mainPanel = new JPanel();

    private final PillIconTextPanel address = new PillIconTextPanel("\uD83D\uDCCD","test");
    private final PillIconTextPanel phone = new PillIconTextPanel("\uD83D\uDCDE","test");
    private final PillIconTextPanel submitReview = new PillIconTextPanel("Submit");

    private RoundedPanel hoursCard = new RoundedPanel(30);
    private final JPanel hoursInfo = new JPanel();

    private final JLabel title = new JLabel("\uD83D\uDD52 Opening Hours:");
    private final JLabel addReviewTitle = new JLabel("Leave a review!");

    private final ArrayList<String> openingHours = new ArrayList<>();

    private final JScrollPane leftScroll;
    private final JScrollPane rightScroll;

    private final JScrollPane reviewBox;
    private final JTextArea multiLineBox = new JTextArea(7, 35);

    private final JPanel reviewsContainer = new JPanel();

    private ViewManagerModel viewManagerModel;
    private LoggedInViewModel loggedInViewModel;
    private DisplayReviewsViewModel displayReviewsViewModel = new DisplayReviewsViewModel();
    private DisplayReviewsController displayReviewsController;


    public RestaurantView(ViewRestaurantViewModel viewRestaurantViewModel){
        this.viewRestaurantViewModel = viewRestaurantViewModel;
        ViewRestaurantState state = viewRestaurantViewModel.getState();
        DisplayReviewsStateList displayReviewStates = displayReviewsViewModel.getState();

        viewRestaurantViewModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout());
        leftPanel.setLayout(new BorderLayout());
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        imageAndInfoPanel.setLayout(new BoxLayout(imageAndInfoPanel, BoxLayout.Y_AXIS));

        rightPanel.setLayout(new BorderLayout());
        addReviewPanel.setLayout(new BoxLayout(addReviewPanel, BoxLayout.Y_AXIS));
        reviewsContainer.setLayout(new BoxLayout(reviewsContainer, BoxLayout.Y_AXIS));

        addReviewTitle.setFont(addReviewTitle.getFont().deriveFont(Font.BOLD, 20f));

        titlePanel = new RestaurantTitlePanel(state.getName(), state.getType(), state.getRating(), state.getRatingCount());

        // Create favorite button and add to title panel
        createFavoriteButton();
        titlePanel.add(favoriteButton);

        // align content to the left
        imageAndInfoPanel.add(createImageLabel(state));
        imageAndInfoPanel.add(Box.createVerticalStrut(8));
        imageAndInfoPanel.add(createInfoPanel());
        imageAndInfoPanel.add(Box.createVerticalGlue());

        // 1. Create the text area
        multiLineBox.setLineWrap(true);
        multiLineBox.setWrapStyleWord(true);

        // 2. Wrap the text area in a JScrollPane to add scroll bars
        reviewBox = new JScrollPane(multiLineBox);
        reviewBox.setBorder(null);
        reviewBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        submitReview.setCursor(new Cursor(Cursor.HAND_CURSOR));
        Box buttonContainer = Box.createHorizontalBox();
        buttonContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonContainer.add(Box.createHorizontalGlue());
        buttonContainer.add(submitReview);
        buttonContainer.add(Box.createHorizontalGlue());

        addReviewPanel.add(Box.createVerticalStrut(12));
        addReviewPanel.add(addReviewTitle);
        addReviewPanel.add(Box.createVerticalStrut(12));
        addReviewPanel.add(reviewBox);
        addReviewPanel.add(Box.createVerticalStrut(16));
        addReviewPanel.add(buttonContainer);
        addReviewPanel.add(Box.createVerticalStrut(16));

        addReviewPanel.setBorder(BorderFactory.createEmptyBorder( 0, 20, 0, 20));

        // add everything into a scrollable container
        leftScroll = new JScrollPane(imageAndInfoPanel);
        rightScroll = new JScrollPane(reviewsContainer);

        rightPanel.add(rightScroll, BorderLayout.CENTER);
        rightPanel.add(addReviewPanel, BorderLayout.SOUTH);

        leftScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        leftScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        leftScroll.setBorder(null);

        rightScroll.setBorder(null);

        add(titlePanel, BorderLayout.NORTH);
        add(leftScroll, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
    }

    private void createFavoriteButton() {
        favoriteButton = new JButton("♡");
        favoriteButton.setFont(new Font("Arial", Font.PLAIN, 32));
        favoriteButton.setForeground(new Color(236, 72, 153)); // Pink color
        favoriteButton.setContentAreaFilled(false);
        favoriteButton.setBorderPainted(false);
        favoriteButton.setFocusPainted(false);
        favoriteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        favoriteButton.setToolTipText("Add to favorites");

        favoriteButton.addActionListener(e -> toggleFavorite());
    }

    private void toggleFavorite() {
        // Check if user is logged in
        if (loggedInViewModel == null || loggedInViewModel.getState().getUid().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please log in to add favorites",
                    "Login Required",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String userId = loggedInViewModel.getState().getUid();
        String restaurantId = viewRestaurantViewModel.getState().getId();

        // DEBUG STATEMENTS
        System.out.println("======== TOGGLE FAVORITE ========");
        System.out.println("Restaurant ID: " + restaurantId);
        System.out.println("Current isFavorite state: " + isFavorite);

        if (isFavorite) {
            System.out.println("Action: REMOVING from favorites");
            removeFavoriteController.execute(userId, restaurantId);
            setFavoriteState(false);
            System.out.println("UI state set to: NOT FAVORITE");
        } else {
            System.out.println("Action: ADDING to favorites");
            addFavoriteController.execute(userId, restaurantId);
            setFavoriteState(true);
            System.out.println("UI state set to: FAVORITE");
        }
        System.out.println("=================================");
    }

    private void setFavoriteState(boolean favorite) {
        isFavorite = favorite;
        if (favorite) {
            favoriteButton.setText("♥"); // Filled heart
            favoriteButton.setToolTipText("Remove from favorites");
        } else {
            favoriteButton.setText("♡"); // Empty heart
            favoriteButton.setToolTipText("Add to favorites");
        }
    }

    private void checkIfFavorite() {
        // Check if the current restaurant is in user's favorites
        if (loggedInViewModel == null || loggedInViewModel.getState().getUid().isEmpty()) {
            setFavoriteState(false);
            return;
        }

        if (userDataAccess == null) {
            System.err.println("UserDataAccess not set in RestaurantView");
            setFavoriteState(false);
            return;
        }

        String userId = loggedInViewModel.getState().getUid();
        String restaurantId = viewRestaurantViewModel.getState().getId();

        try {
            User user = userDataAccess.getUser(userId);
            if (user != null && user.getFavoriteRestaurantIds().contains(restaurantId)) {
                setFavoriteState(true);
            } else {
                setFavoriteState(false);
            }
        } catch (Exception e) {
            System.err.println("Error checking favorite status: " + e.getMessage());
            setFavoriteState(false);
        }
    }

    private JPanel createInfoPanel() {
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // add everything into their respective panels
        infoPanel.add(Box.createVerticalStrut(8));

        infoPanel.add(address);
        infoPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        infoPanel.add(Box.createVerticalStrut(20));
        infoPanel.add(phone);
        infoPanel.add(Box.createVerticalStrut(20));
        infoPanel.add(createHoursPanel());
        infoPanel.add(Box.createVerticalStrut(40));

        return infoPanel;
    }

    private RoundedPanel createHoursPanel() {
        hoursCard.setLayout(new BoxLayout(hoursCard, BoxLayout.Y_AXIS));
        hoursCard.setBackground(Color.WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));

        hoursInfo.setOpaque(false);
        hoursInfo.setLayout(new BoxLayout(hoursInfo, BoxLayout.Y_AXIS));

        for(String d : openingHours){
            JLabel dayLabel = new JLabel(d);
            dayLabel.setFont(dayLabel.getFont().deriveFont(Font.BOLD, 14f));
            dayLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            hoursInfo.add(dayLabel);
            hoursInfo.add(Box.createVerticalStrut(4));
        }

        hoursInfo.setAlignmentX(Component.LEFT_ALIGNMENT);

        hoursCard.add(title);
        hoursCard.add(Box.createVerticalStrut(10));
        hoursCard.add(hoursInfo);

        hoursCard.setBorder(
                BorderFactory.createEmptyBorder(12, 18, 16, 18));

        return hoursCard;
    }

    private JLabel createImageLabel(ViewRestaurantState state) {
        if(state.getPhotos() != null && !state.getPhotos().isEmpty()) {
            ImageIcon shortestIcon = new ImageIcon(state.getPhotos().get(0));

            for (BufferedImage image : state.getPhotos()) {
                Image scaled = image.getScaledInstance(800, -1, Image.SCALE_SMOOTH);
                ImageIcon curIcon  = new ImageIcon(scaled);

                if (curIcon.getIconHeight() < shortestIcon.getIconHeight()){
                    shortestIcon = curIcon;
                }

                if (curIcon.getIconHeight() < 700) {
                    break;
                }
            }

            imageLabel.setIcon(shortestIcon);
        }

        imageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        imageLabel.setHorizontalAlignment(JLabel.LEFT);
        imageLabel.setVerticalAlignment(JLabel.TOP);
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 35, true));

        return imageLabel;
    }

    private void addReviewListener(){
        submitReview.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String reviewText = multiLineBox.getText();

                try {
                    addReviewController.execute(viewRestaurantViewModel.getState().getId(), reviewText);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                multiLineBox.setText("");
            }
        });
    }

    private void addExitListener(){
        titlePanel.getExitPill().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (viewManagerModel != null) {
                    viewManagerModel.setState(loggedInViewModel.getViewName());
                    viewManagerModel.firePropertyChange();
                } else {
                    System.out.println("View manager for restaurant view not initiated");
                }
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        System.out.println("RestaurantView.propertyChange fired: " + evt.getPropertyName());

        addReviewListener();
        addExitListener();

        if ("restaurant info".equals(evt.getPropertyName()) && evt.getPropertyName() != null) {
            ViewRestaurantState state = viewRestaurantViewModel.getState();

            displayReviewsController.execute(state.getId());

            // Check if this restaurant is favorited
            checkIfFavorite();

            // Now update title panel
            titlePanel.setRestaurantName(state.getName());
            titlePanel.setRating(state.getRating(), state.getRatingCount());
            titlePanel.setType(state.getType());

            // update image
            imageLabel = createImageLabel(state);

            // update address and phone number
            address.setText(state.getAddress());
            phone.setText(state.getPhoneNumber());

            // update opening hours
            openingHours.clear();
            openingHours.addAll(state.getOpeningHours());

            hoursInfo.removeAll();
            for (String d : openingHours) {
                JLabel dayLabel = new JLabel(d);
                dayLabel.setFont(dayLabel.getFont().deriveFont(Font.PLAIN, 14f));
                dayLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                hoursInfo.add(dayLabel);
                hoursInfo.add(Box.createVerticalStrut(4));
            }

            hoursCard.setMaximumSize(new Dimension(350, 350));
            title.setText("\uD83D\uDD52 Opening Hours:");
        }

        if ("display reviews".equals(evt.getPropertyName()) && evt.getPropertyName() != null) {
            DisplayReviewsStateList state = displayReviewsViewModel.getState();
            reviewsContainer.removeAll();

            for (DisplayReviewsState reviewState : state.getDisplayReviewsStateList()) {
                ReviewPanel reviewPanel = new ReviewPanel(
                        reviewState.getAuthorDisplayName(),
                        reviewState.getCreationDate(),
                        reviewState.getContent()
                );

                reviewsContainer.add(reviewPanel);

                Dimension preferredSize = reviewPanel.getPreferredSize();
                reviewPanel.setMaximumSize(
                        new Dimension(Integer.MAX_VALUE, preferredSize.height)
                );
            }
        }

        if ("review status".equals(evt.getPropertyName()) && evt.getPropertyName() != null) {
            ViewRestaurantState state = viewRestaurantViewModel.getState();
            displayReviewsController.execute(state.getId());
        }

        hoursInfo.revalidate();
        hoursInfo.repaint();

        title.validate();
        title.repaint();

        address.revalidate();
        address.repaint();

        phone.revalidate();
        phone.repaint();

        imageAndInfoPanel.revalidate();
        imageAndInfoPanel.repaint();

        revalidate();
        repaint();
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewRestaurantController(ViewRestaurantController viewRestaurantController) {
        this.viewRestaurantController = viewRestaurantController;
    }

    public ViewRestaurantController getViewRestaurantController(){
        return viewRestaurantController;
    }

    public void setAddReviewController(AddReviewController addReviewController){
        this.addReviewController = addReviewController;
    }

    public AddReviewController getAddReviewController(){
        return addReviewController;
    }

    public void setViewManagerModel(ViewManagerModel viewManagerModel) {
        this.viewManagerModel = viewManagerModel;
    }

    public void setAddReviewViewModel(AddReviewViewModel addReviewViewModel){
        this.addReviewViewModel = addReviewViewModel;
        this.addReviewViewModel.addPropertyChangeListener(this);
    }

    public void setLoggedInViewModel(LoggedInViewModel loggedInViewModel){
        this.loggedInViewModel = loggedInViewModel;
    }

    public void setDisplayReviewController(DisplayReviewsController displayReviewsController) {
        this.displayReviewsController = displayReviewsController;
    }

    public DisplayReviewsController getDisplayReviewController() {
        return displayReviewsController;
    }

    public void setDisplayReviewViewModel(DisplayReviewsViewModel displayReviewsViewModel) {
        this.displayReviewsViewModel = displayReviewsViewModel;
        this.displayReviewsViewModel.addPropertyChangeListener(this);
    }

    // NEW SETTERS for favorite functionality
    public void setAddFavoriteController(AddFavoriteController addFavoriteController) {
        this.addFavoriteController = addFavoriteController;
    }

    public void setRemoveFavoriteController(RemoveFavoriteController removeFavoriteController) {
        this.removeFavoriteController = removeFavoriteController;
    }

    public void setUserDataAccess(UserDataAccessInterface userDataAccess) {
        this.userDataAccess = userDataAccess;
    }
}
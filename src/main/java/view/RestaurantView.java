package view;
import interface_adapter.ViewManagerModel;
import interface_adapter.add_review.AddReviewController;
import interface_adapter.add_review.AddReviewViewModel;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.view_restaurant.ViewRestaurantController;
import interface_adapter.view_restaurant.ViewRestaurantState;
import interface_adapter.view_restaurant.ViewRestaurantViewModel;
import use_case.add_review.AddReviewInputBoundary;
import use_case.add_review.AddReviewOutputBoundary;
import view.panel_makers.PillIconTextPanel;
import view.panel_makers.RestaurantTitlePanel;
import view.panel_makers.RoundedPanel;

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




    private final RestaurantTitlePanel titlePanel;

    private final JPanel imageAndInfoPanel = new JPanel();
    private JPanel infoPanel = new JPanel();
    private BufferedImage image;
    private JLabel imageLabel = new JLabel();
    private JPanel addReviewPanel = new JPanel();



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
    private final JTextArea multiLineBox = new JTextArea(5, 25);
    private ViewManagerModel viewManagerModel;
    private LoggedInViewModel loggedInViewModel;


    public RestaurantView(ViewRestaurantViewModel viewRestaurantViewModel, AddReviewViewModel addReviewViewModel){


        this.viewRestaurantViewModel = viewRestaurantViewModel;
        this.addReviewViewModel = addReviewViewModel;
        ViewRestaurantState state = viewRestaurantViewModel.getState();
        viewRestaurantViewModel.addPropertyChangeListener(this);



        setLayout(new BorderLayout());
        leftPanel.setLayout(new BorderLayout());
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        imageAndInfoPanel.setLayout(new BoxLayout(imageAndInfoPanel, BoxLayout.Y_AXIS));
        addReviewPanel.setLayout(new BoxLayout(addReviewPanel, BoxLayout.Y_AXIS));


        addReviewTitle.setFont(addReviewTitle.getFont().deriveFont(Font.BOLD, 20f));

        titlePanel = new RestaurantTitlePanel(state.getName(), state.getType(), state.getRating(), state.getRatingCount());
        //titlePanel.setPreferredSize(new Dimension(500, 80));
       // titlePanel.setPreferredSize(new Dimension(400, 100));

        // set layout of each panel
        // set background colour for testing purposes
//        leftPanel.setBackground(Color.blue);
//        imageAndInfoPanel.setBackground(Color.black);
//        titlePanel.setBackground(Color.green);
//        infoPanel.setBackground(Color.black);



        // align content to the left


        imageAndInfoPanel.add(createImageLabel(state));
        imageAndInfoPanel.add(Box.createVerticalStrut(8));
        imageAndInfoPanel.add(createInfoPanel());
        imageAndInfoPanel.add(Box.createVerticalGlue());


        // 1. Create the text area
         // 5 rows high, 30 columns wide
        multiLineBox.setLineWrap(true);               // Wraps text to the next line
        multiLineBox.setWrapStyleWord(true);          // Wraps at whole words

        // 2. Wrap the text area in a JScrollPane to add scroll bars
        reviewBox = new JScrollPane(multiLineBox);
        reviewBox.setBorder(null);

        reviewBox.setAlignmentX(Component.LEFT_ALIGNMENT);


        submitReview.setCursor(new Cursor(Cursor.HAND_CURSOR));
        Box buttonContainer = Box.createHorizontalBox();
        buttonContainer.setAlignmentX(Component.LEFT_ALIGNMENT); // <--- MUST BE LEFT to match the Title!
        buttonContainer.add(Box.createHorizontalGlue());         // Pushes button from left
        buttonContainer.add(submitReview);                       // The actual button
        buttonContainer.add(Box.createHorizontalGlue());         // Pushes button from right

        //addReviewTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        addReviewPanel.add(addReviewTitle);
        addReviewPanel.add(Box.createVerticalStrut(12));
        addReviewPanel.add(reviewBox);
        addReviewPanel.add(Box.createVerticalStrut(16));
        addReviewPanel.add(buttonContainer);
        addReviewPanel.add(Box.createVerticalStrut(16));

        addReviewPanel.setBorder(BorderFactory.createEmptyBorder( 0, 10, 0, 10));



        // add everything into a scrollable container
        leftScroll = new JScrollPane(imageAndInfoPanel);
        rightScroll = new JScrollPane(addReviewPanel);

        leftScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        leftScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        leftScroll.setBorder(null);


//        leftPanel.add(leftScroll, BorderLayout.NORTH);
//        leftPanel.add(titlePanel, BorderLayout.CENTER);

        add(titlePanel, BorderLayout.NORTH);
        add(leftScroll, BorderLayout.WEST);
        add(rightScroll, BorderLayout.CENTER);
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
        // ========= make a rounded card to display restaurant operation hours (should be extracted into a panel maker
        // to increase readability, but I got lazy so maybe later... :) =========

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
        // get the image and save it in a label


        if(state.getPhotos() != null) {
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

                // Example: viewRestaurantController.executeAddReview(reviewText);
            }
        });
    }

    private void addExitListener(){
        titlePanel.getExitPill().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("Button clicked");
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

        // Optionally filter by property name if you use multiple
        if (!"restaurant info".equals(evt.getPropertyName()) && evt.getPropertyName() != null) {
            return;
        }

        ViewRestaurantState state = viewRestaurantViewModel.getState();

        // Now update title panel
        titlePanel.setRestaurantName(state.getName());
        titlePanel.setRating(state.getRating(), state.getRatingCount());
        titlePanel.setType(state.getType());

        addReviewListener();
        addExitListener();
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
        hoursInfo.revalidate();
        hoursInfo.repaint();

        title.setText("\uD83D\uDD52 Opening Hours:");

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

    public void setLoggedInViewModel(LoggedInViewModel loggedInViewModel){
        this.loggedInViewModel = loggedInViewModel;
    }
}

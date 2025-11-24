package view;
import interface_adapter.view_restaurant.ViewRestaurantController;
import interface_adapter.view_restaurant.ViewRestaurantState;
import interface_adapter.view_restaurant.ViewRestaurantViewModel;
import kotlin.text.UStringsKt;
import view.panel_makers.PillIconTextPanel;
import view.panel_makers.RestaurantTitlePanel;
import view.panel_makers.ImageBackgroundPanel;
import view.panel_makers.RoundedPanel;

import javax.imageio.stream.ImageInputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Array;
import java.util.ArrayList;


public class RestaurantView extends JPanel implements ActionListener, PropertyChangeListener{
    private final String viewName = "restaurant info";
    private ViewRestaurantViewModel viewRestaurantViewModel;
    private final RestaurantTitlePanel titlePanel;

    private final JPanel imageAndInfoPanel = new JPanel();
    private final JPanel infoPanel = new JPanel();
    private BufferedImage image;
    private JLabel imageLabel = new JLabel();



    private final JPanel leftPanel = new JPanel();
    private final JPanel rightPanel = new JPanel();

    private final PillIconTextPanel address = new PillIconTextPanel("\uD83D\uDCCD","test");
    private final PillIconTextPanel phone = new PillIconTextPanel("\uD83D\uDCDE","test");
    private final RoundedPanel hoursCard = new RoundedPanel(30);
    private final JPanel hoursInfo = new JPanel();
    private final JLabel title = new JLabel("\uD83D\uDD52 Opening Hours:");


    private final ArrayList<String> openingHours = new ArrayList<>();

    private final JScrollPane leftScroll;

    private ViewRestaurantController viewRestaurantController = null;

    public RestaurantView(ViewRestaurantViewModel viewRestaurantViewModel){
        this.viewRestaurantViewModel = viewRestaurantViewModel;
        ViewRestaurantState state = viewRestaurantViewModel.getState();

        viewRestaurantViewModel.addPropertyChangeListener(this);

        titlePanel = new RestaurantTitlePanel(state.getName(), state.getType(), state.getRating(), state.getRatingCount());
        titlePanel.setPreferredSize(new Dimension(500, 80));


        // get the image and save it in a label
        if(state.getPhotos() != null){
            image = state.getPhotos().get(0);
            ImageIcon icon = new ImageIcon(image);
            imageLabel = new JLabel(icon);
        }

        imageLabel.setHorizontalAlignment(JLabel.LEFT);
        imageLabel.setVerticalAlignment(JLabel.TOP);
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 25));

        // set layout of each panel
        setLayout(new BorderLayout());
        leftPanel.setLayout(new BorderLayout());
        imageAndInfoPanel.setLayout(new BoxLayout(imageAndInfoPanel, BoxLayout.Y_AXIS));
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));



        // set background colour for testing purposes
//        leftPanel.setBackground(Color.blue);
//        imageAndInfoPanel.setBackground(Color.black);
//        titlePanel.setBackground(Color.green);
//        infoPanel.setBackground(Color.black);

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


        // align content to the left
        imageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);


        hoursCard.setBorder(
                BorderFactory.createEmptyBorder(12, 18, 16, 18));

        // add everything into their respective panels
        infoPanel.add(Box.createVerticalStrut(8));

        infoPanel.add(address);
        infoPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        infoPanel.add(Box.createVerticalStrut(20));
        infoPanel.add(phone);
        infoPanel.add(Box.createVerticalStrut(20));
        infoPanel.add(hoursCard);
        infoPanel.add(Box.createVerticalStrut(40));

        imageAndInfoPanel.add(imageLabel);
        imageAndInfoPanel.add(Box.createVerticalStrut(8));
        imageAndInfoPanel.add(infoPanel);
        imageAndInfoPanel.add(Box.createVerticalGlue());

        leftPanel.add(titlePanel, BorderLayout.NORTH);
        leftPanel.add(imageAndInfoPanel, BorderLayout.CENTER);


        // add everything into a scrollable container
        leftScroll = new JScrollPane(leftPanel);

        leftScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        leftScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(leftScroll);

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

        // update image
        image = state.getPhotos().get(0);
        ImageIcon icon = new ImageIcon(image);
        imageLabel.setIcon(icon);

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

    public void setController(ViewRestaurantController viewRestaurantController) {
        this.viewRestaurantController = viewRestaurantController;
    }

    public ViewRestaurantController getController(){
        return viewRestaurantController;
    }
}

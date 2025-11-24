package view.panel_makers;

import javax.naming.NamingEnumeration;
import javax.swing.*;
import java.awt.*;

public class RestaurantTitlePanel extends JPanel{
    private final JLabel nameLabel;
    private final PillIconTextPanel ratingPill;
    private final PillIconTextPanel typePill;

    public RestaurantTitlePanel(String name, String type, double rating, int ratingCount){
        setLayout(new BorderLayout());

        nameLabel = new JLabel(name);
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, 22f));

        // padding for the content inside.
        nameLabel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        typePill = new PillIconTextPanel(type);
        ratingPill = new PillIconTextPanel("★", rating + " (" + ratingCount + ")");


        // add an invisible wrapper panel that aligns the pill so that it hugs the right wall
        JPanel rightWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT,16, 8));
        rightWrapper.setOpaque(false);
        rightWrapper.add(typePill);
        rightWrapper.add(ratingPill);

        // A container for name and pill so we can make it stick to the top with BorderLayout.NORTH
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.add(nameLabel, BorderLayout.WEST);
        topRow.add(rightWrapper, BorderLayout.EAST);
        topRow.setBorder(BorderFactory.createEmptyBorder(12, 0, 8, 0));


        add(topRow, BorderLayout.NORTH);
    }



    public void setRestaurantName(String name){
        nameLabel.setText(name);
    }

    public void setRating(double rating, int ratingCount){
        ratingPill.setText(rating + " (" + ratingCount + ")");
    }

    public void setType(String type){
        typePill.setText(type);
        revalidate();
        repaint();
    }
}

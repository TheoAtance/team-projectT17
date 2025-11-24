package view;

import javax.swing.*;
import java.awt.*;

public class RestaurantTitlePanel extends JPanel{
    private final JLabel nameLabel;
    private final PillIconTextPanel ratingPill;

    public RestaurantTitlePanel(String name, double rating, int ratingCount){
        setLayout(new BorderLayout());

        nameLabel = new JLabel(name);
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, 18f));

        // padding for the content inside.
        nameLabel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        add(nameLabel, BorderLayout.WEST);

        ratingPill = new PillIconTextPanel("★", rating + "(" + ratingCount + ")");


        // add an invisible wrapper panel that aligns the pill so that it hugs the right wall
        JPanel rightWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT,16, 8));
        rightWrapper.setOpaque(false);
        rightWrapper.add(ratingPill);

        add(rightWrapper, BorderLayout.EAST);
    }



    public void setRestaurantName(String name){
        nameLabel.setText(name);
    }

    public void setRating(double rating, int ratingCount){
        ratingPill.setText(rating + "(" + ratingCount + ")");
    }
}

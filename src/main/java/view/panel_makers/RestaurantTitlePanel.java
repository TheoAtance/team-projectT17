package view.panel_makers;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.*;

public class RestaurantTitlePanel extends JPanel {

  private final JLabel nameLabel;
  private final PillIconTextPanel ratingPill;
  private final PillIconTextPanel typePill;
  private final PillIconTextPanel exitPill;
  private JPanel leftWrapper;

  public RestaurantTitlePanel(String name, String type, double rating, int ratingCount) {
    setLayout(new BorderLayout());

    nameLabel = new JLabel(name);
    nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, 22f));

    // padding for the content inside.
    nameLabel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

    typePill = new PillIconTextPanel(type);
    ratingPill = new PillIconTextPanel("★", rating + " (" + ratingCount + ")");
    exitPill = new PillIconTextPanel("x");

    exitPill.getTextLabel().setFont(exitPill.getTextLabel().getFont().deriveFont(Font.BOLD, 16f));
    exitPill.setCursor(new Cursor(Cursor.HAND_CURSOR));

    // add an invisible wrapper panel that aligns the pill so that it hugs the right wall
    JPanel rightWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 8));
    leftWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 8));

    leftWrapper.setOpaque(false);
    rightWrapper.setOpaque(false);

    leftWrapper.add(nameLabel);
    leftWrapper.add(typePill);

    leftWrapper.add(ratingPill);
    rightWrapper.add(exitPill);

    // A container for name and pill so we can make it stick to the top with BorderLayout.NORTH
    JPanel topRow = new JPanel(new BorderLayout());
    topRow.setOpaque(false);
    topRow.add(leftWrapper, BorderLayout.WEST);
    topRow.add(rightWrapper, BorderLayout.EAST);
    topRow.setBorder(BorderFactory.createEmptyBorder(12, 0, 8, 0));

    add(topRow, BorderLayout.NORTH);
  }


  public void setRestaurantName(String name) {
    nameLabel.setText(name);
  }

  public void setRating(double rating, int ratingCount) {
    ratingPill.setText(rating + " (" + ratingCount + ")");
  }

  public void setType(String type) {
    typePill.setText(type);
    revalidate();
    repaint();
  }

  public PillIconTextPanel getExitPill() {
    return exitPill;
  }

  public void addLeftButton(JButton newButton){
    leftWrapper.add(newButton);
  }
}


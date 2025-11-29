package view.panel_makers;

import view.panel_makers.RoundedPanel;

import javax.swing.*;
import java.awt.*;

public class PillIconTextPanel extends RoundedPanel {

    private final JLabel iconLabel;
    private final JLabel textLabel;
    private final int radius = -1;

    public PillIconTextPanel(String icon, String text) {
        super(30);

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBackground(Color.WHITE);
        setAlignmentX(Component.LEFT_ALIGNMENT);


        iconLabel = new JLabel(icon);
        textLabel = new JLabel(text);
        textLabel.setFont(textLabel.getFont().deriveFont(Font.PLAIN, 14f));


        add(iconLabel);
        add(Box.createHorizontalStrut(12));
        add(textLabel);


        // padding around content
        int verticalPad = 10;
        int horizontalPad = 16;
        setBorder(BorderFactory.createEmptyBorder(
                verticalPad, horizontalPad, verticalPad, horizontalPad));


    }

    public PillIconTextPanel(String text) {
        super(30);

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBackground(Color.WHITE);
        setAlignmentX(Component.LEFT_ALIGNMENT);

        iconLabel = new JLabel();
        textLabel = new JLabel(text);

        add(textLabel);


        // padding around content
        int verticalPad = 10;
        int horizontalPad = 16;
        setBorder(BorderFactory.createEmptyBorder(
                verticalPad, horizontalPad, verticalPad, horizontalPad));

    }

    public void setText(String text) {
        textLabel.setText(text);
        revalidate();
        repaint();
    }

    public void setIcon(String icon) {
        iconLabel.setText(icon);
        revalidate();
        repaint();
    }

    public JLabel getTextLabel(){
        return textLabel;
    }

    @Override
    public float getAlignmentX() {
        // make BoxLayout always treat this as left-aligned
        return LEFT_ALIGNMENT;
    }

    @Override
    public Dimension getMaximumSize() {
        Dimension pref = getPreferredSize();
        return new Dimension(pref.width, pref.height);
    }

}

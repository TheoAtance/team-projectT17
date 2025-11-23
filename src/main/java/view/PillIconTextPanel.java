package view;

import javax.swing.*;
import java.awt.*;

public class PillIconTextPanel extends RoundedPanel{

    private final JLabel iconLabel;
    private final JLabel textLabel;
    private int borderRadius;

    public PillIconTextPanel(String icon, String text) {
        super(30);

        iconLabel = new JLabel(icon);
        textLabel = new JLabel(text);

        // sets the layout of its content. (alignment, horizontal gap, vertical gap)
        setLayout(new FlowLayout(FlowLayout.CENTER, 8, 4));

        setBackground(Color.WHITE);

        add(iconLabel);
        add(textLabel);

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
}

package view.panel_makers;
import javax.swing.*;
import java.awt.*;

/**
 * A rounded panel that acts as a background for singled-lined information
 */
public class RoundedPanel extends JPanel{
    private final int cornerRadius;

    public RoundedPanel(int cornerRadius){
        this.cornerRadius = cornerRadius;
        setOpaque(false);

        // Essentially, the way swing works, it will draw a default rect with sharp corners.
        // setOpaque(false) prevents it from doing that so we can draw our own rect. (a rounded rect in this case).
    }

    @Override
    protected void paintComponent(Graphics g){

        // g, intuitively, is like a paintbrush provided by swing that lets you draw stuff, and g2 is a fancier version
        // We use graphics 2d instead because it allows use to enable antialiasing making the edges sharper
        Graphics2D g2 = (Graphics2D) g.create();


        // Makes edges sharper on the round panel
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);


        // set colour and fill rect
        g2.setColor(Color.white); // sets the colour of the rect (set in another panel using this panel)
        g2.fillRoundRect(0,0, getWidth(), getHeight(), cornerRadius, cornerRadius); // draws a filled round rect


        // close the g2 object so these attributes won't bleed into other components
        g2.dispose();

        // draw whatever child element that was added to this panel (e.g. JLabel, TextFields, etc).
        super.paintComponent(g);
    }
}

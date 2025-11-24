package view.panel_makers;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageBackgroundPanel extends JPanel {

    private BufferedImage backgroundImage;

    public void setBackgroundImage(BufferedImage image) {
        this.backgroundImage = image;
        revalidate();
        repaint();   // important: triggers re-draw
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backgroundImage != null) {
            int panelWidth = getWidth();

            int imgW = backgroundImage.getWidth();
            int imgH = backgroundImage.getHeight();

            // calculate height based on width (keep aspect ratio)
            double aspect = (double) imgH / imgW;
            int drawHeight = (int) (panelWidth * aspect);

            g.drawImage(backgroundImage, 0, 0, 300, 300, this);
        }
    }

//    @Override
//    public Dimension getPreferredSize() {
//        if (backgroundImage == null) {
//            return new Dimension(0, 0);
//        }
//        return new Dimension(backgroundImage.getWidth(), backgroundImage.getHeight());
//    }

//    @Override
//    public Dimension getMaximumSize(){
//        int panelWidth = getWidth();
//        int imgW = backgroundImage.getWidth();
//        int imgH = backgroundImage.getHeight();
//
//        // calculate height based on width (keep aspect ratio)
//        double aspect = (double) imgH / imgW;
//        int drawHeight = (int) (imgW * aspect);
//
//        return new Dimension(200, 300);
//    }

}
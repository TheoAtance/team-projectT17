package view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

/**
 * A Swing component that displays a restaurant panel with image, name, rating, and discount info.
 * Clean Architecture compliant - does not depend on entity layer.
 */
public class RestaurantPanel extends JPanel {
    private static final int CARD_WIDTH = 280;
    private static final int CARD_HEIGHT = 200;
    private static final int IMAGE_HEIGHT = 140;
    private static final int CORNER_RADIUS = 16;
    private static final int HEART_SIZE = 30;

    private final RestaurantDisplayData displayData;
    private BufferedImage restaurantImage;
    private boolean isFavorite = false;
    private HeartClickListener heartClickListener;

    /**
     * Data class for restaurant display information.
     * No entity dependencies - just display strings and primitives.
     */
    public static class RestaurantDisplayData {
        private final String id;
        private final String name;
        private final String type;
        private final double rating;
        private final boolean hasDiscount;
        private final double discountValue;

        public RestaurantDisplayData(String id, String name, String type, double rating,
                                     boolean hasDiscount, double discountValue) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.rating = rating;
            this.hasDiscount = hasDiscount;
            this.discountValue = discountValue;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public String getType() { return type; }
        public double getRating() { return rating; }
        public boolean hasDiscount() { return hasDiscount; }
        public double getDiscountValue() { return discountValue; }
    }

    public interface HeartClickListener {
        void onHeartClicked(String restaurantId, boolean newFavoriteState);
    }

    public RestaurantPanel(RestaurantDisplayData displayData) {
        this.displayData = displayData;
        setupUI();
    }

    public RestaurantPanel(RestaurantDisplayData displayData, BufferedImage image) {
        this.displayData = displayData;
        this.restaurantImage = image;
        setupUI();
    }

    private void setupUI() {
        setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        setMaximumSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                final int heartX = CARD_WIDTH - 45;
                final int heartY = 15;

                if (e.getX() >= heartX && e.getX() <= heartX + HEART_SIZE &&
                        e.getY() >= heartY && e.getY() <= heartY + HEART_SIZE) {
                    toggleFavorite();
                    if (heartClickListener != null) {
                        heartClickListener.onHeartClicked(displayData.getId(), isFavorite);
                    }
                } else {
                    System.out.println("Clicked on: " + displayData.getName());
                }
            }
        });
    }

    private void toggleFavorite() {
        isFavorite = !isFavorite;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        final Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2.setColor(Color.WHITE);
        g2.fill(new RoundRectangle2D.Double(0, 0, CARD_WIDTH, CARD_HEIGHT, CORNER_RADIUS, CORNER_RADIUS));

        g2.setColor(new Color(0, 0, 0, 20));
        g2.fill(new RoundRectangle2D.Double(2, 2, CARD_WIDTH, CARD_HEIGHT, CORNER_RADIUS, CORNER_RADIUS));

        g2.setClip(new RoundRectangle2D.Double(0, 0, CARD_WIDTH, IMAGE_HEIGHT, CORNER_RADIUS, CORNER_RADIUS));

        if (restaurantImage != null) {
            g2.drawImage(restaurantImage, 0, 0, CARD_WIDTH, IMAGE_HEIGHT, null);
        } else {
            final GradientPaint gradient = new GradientPaint(0, 0, new Color(255, 230, 230),
                    0, IMAGE_HEIGHT, new Color(255, 200, 200));
            g2.setPaint(gradient);
            g2.fillRect(0, 0, CARD_WIDTH, IMAGE_HEIGHT);
        }

        g2.setClip(null);

        int badgeY = 15;
        if (displayData.getType() != null && !displayData.getType().isEmpty()) {
            drawBadge(g2, displayData.getType(), 15, badgeY, new Color(239, 68, 68));
            badgeY += 35;
        }

        if (displayData.hasDiscount()) {
            final String discountText = (int)(displayData.getDiscountValue() * 100) + "% off";
            drawBadge(g2, discountText, 15, badgeY, new Color(236, 72, 153));
        }

        drawHeartIcon(g2, CARD_WIDTH - 45, 15, isFavorite);

        g2.setColor(Color.WHITE);
        g2.fillRect(0, IMAGE_HEIGHT, CARD_WIDTH, CARD_HEIGHT - IMAGE_HEIGHT);

        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        String restaurantName = displayData.getName();
        final FontMetrics nameMetrics = g2.getFontMetrics();
        final int maxNameWidth = CARD_WIDTH - 30;

        if (nameMetrics.stringWidth(restaurantName) > maxNameWidth) {
            restaurantName = truncateText(restaurantName, nameMetrics, maxNameWidth);
        }
        g2.drawString(restaurantName, 15, IMAGE_HEIGHT + 28);

        g2.setColor(new Color(156, 163, 175));
        g2.setFont(new Font("Arial", Font.PLAIN, 14));
        String cuisineType = displayData.getType();
        final FontMetrics cuisineMetrics = g2.getFontMetrics();
        final int maxCuisineWidth = CARD_WIDTH - 30;

        if (cuisineMetrics.stringWidth(cuisineType) > maxCuisineWidth) {
            cuisineType = truncateText(cuisineType, cuisineMetrics, maxCuisineWidth);
        }
        g2.drawString(cuisineType, 15, IMAGE_HEIGHT + 48);

        if (displayData.getRating() > 0) {
            drawStar(g2, CARD_WIDTH - 120, IMAGE_HEIGHT + 20, new Color(251, 191, 36));
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            final String ratingText = String.format("%.1f", displayData.getRating());
            g2.drawString(ratingText, CARD_WIDTH - 98, IMAGE_HEIGHT + 28);

            g2.setColor(new Color(156, 163, 175));
            g2.setFont(new Font("Arial", Font.PLAIN, 12));
            g2.drawString("(100+)", CARD_WIDTH - 65, IMAGE_HEIGHT + 28);
        }

        drawClockIcon(g2, CARD_WIDTH - 105, IMAGE_HEIGHT + 38, new Color(236, 72, 153));
        g2.setColor(new Color(156, 163, 175));
        g2.setFont(new Font("Arial", Font.PLAIN, 14));
        g2.drawString("25 min", CARD_WIDTH - 80, IMAGE_HEIGHT + 48);

        g2.dispose();
    }

    private String truncateText(String text, FontMetrics metrics, int maxWidth) {
        if (metrics.stringWidth(text) <= maxWidth) {
            return text;
        }

        final String ellipsis = "...";
        final int ellipsisWidth = metrics.stringWidth(ellipsis);

        int low = 0;
        int high = text.length();

        while (low < high) {
            final int mid = (low + high) / 2;
            final String candidate = text.substring(0, mid) + ellipsis;

            if (metrics.stringWidth(candidate) < maxWidth) {
                low = mid + 1;
            } else {
                high = mid;
            }
        }

        return text.substring(0, low - 1) + ellipsis;
    }

    private void drawBadge(Graphics2D g2, String text, int x, int y, Color color) {
        g2.setColor(color);
        final FontMetrics fm = g2.getFontMetrics(new Font("Arial", Font.BOLD, 12));
        final int padding = 8;
        final int width = fm.stringWidth(text) + padding * 2;
        final int height = 22;

        g2.fill(new RoundRectangle2D.Double(x, y, width, height, 8, 8));

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.drawString(text, x + padding, y + 16);
    }

    private void drawHeartIcon(Graphics2D g2, int x, int y, boolean filled) {
        if (filled) {
            g2.setColor(new Color(236, 72, 153));
            g2.fillOval(x, y, HEART_SIZE, HEART_SIZE);

            g2.setColor(Color.WHITE);
            drawSimpleHeart(g2, x + HEART_SIZE/2, y + HEART_SIZE/2, HEART_SIZE * 0.4);
        } else {
            g2.setColor(Color.WHITE);
            g2.fillOval(x, y, HEART_SIZE, HEART_SIZE);

            g2.setColor(new Color(200, 200, 200));
            g2.setStroke(new BasicStroke(2));
            g2.drawOval(x, y, HEART_SIZE, HEART_SIZE);

            g2.setColor(new Color(236, 72, 153));
            drawSimpleHeart(g2, x + HEART_SIZE/2, y + HEART_SIZE/2, HEART_SIZE * 0.4);
        }
    }

    private void drawSimpleHeart(Graphics2D g2, double centerX, double centerY, double size) {
        final Path2D heart = new Path2D.Double();

        heart.moveTo(centerX, centerY - size * 0.3);
        heart.curveTo(
                centerX - size * 0.5, centerY - size * 0.8,
                centerX - size, centerY - size * 0.2,
                centerX, centerY + size * 0.4
        );

        heart.curveTo(
                centerX + size, centerY - size * 0.2,
                centerX + size * 0.5, centerY - size * 0.8,
                centerX, centerY - size * 0.3
        );

        heart.closePath();
        g2.fill(heart);
    }

    private void drawStar(Graphics2D g2, int x, int y, Color color) {
        g2.setColor(color);
        final int[] xPoints = {x + 8, x + 10, x + 15, x + 11, x + 13, x + 8, x + 3, x + 5, x + 1, x + 6};
        final int[] yPoints = {y, y + 6, y + 6, y + 10, y + 15, y + 12, y + 15, y + 10, y + 6, y + 6};
        g2.fillPolygon(xPoints, yPoints, 10);
    }

    private void drawClockIcon(Graphics2D g2, int x, int y, Color color) {
        final int size = 16;
        g2.setColor(color);
        g2.fillOval(x, y, size, size);

        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2));
        final int cx = x + size / 2;
        final int cy = y + size / 2;
        g2.drawLine(cx, cy, cx, cy - 4);
        g2.drawLine(cx, cy, cx + 3, cy);
    }

    public void setFavorite(boolean favorite) {
        this.isFavorite = favorite;
        repaint();
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setImage(BufferedImage image) {
        this.restaurantImage = image;
        repaint();
    }

    public RestaurantDisplayData getDisplayData() {
        return displayData;
    }

    public void setHeartClickListener(HeartClickListener listener) {
        this.heartClickListener = listener;
    }
}
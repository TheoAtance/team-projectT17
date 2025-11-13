package ui.components;

import entity.Restaurant;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

/**
 * A Swing component that displays a restaurant panel with image, name, rating, and discount info.
 */
public class RestaurantPanel extends JPanel {
    private static final int CARD_WIDTH = 280;
    private static final int CARD_HEIGHT = 200;
    private static final int IMAGE_HEIGHT = 140;
    private static final int CORNER_RADIUS = 16;

    private final Restaurant restaurant;
    private BufferedImage restaurantImage;
    private boolean isFavorite = false;

    public RestaurantPanel(Restaurant restaurant) {
        this.restaurant = restaurant;
        setupUI();
    }

    public RestaurantPanel(Restaurant restaurant, BufferedImage image) {
        this.restaurant = restaurant;
        this.restaurantImage = image;
        setupUI();
    }

    private void setupUI() {
        setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        setMaximumSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add click listener for the panel
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                // Handle card click - could open details view
                System.out.println("Clicked on: " + restaurant.getName());
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        // Enable anti-aliasing
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Draw panel background with rounded corners
        g2.setColor(Color.WHITE);
        g2.fill(new RoundRectangle2D.Double(0, 0, CARD_WIDTH, CARD_HEIGHT, CORNER_RADIUS, CORNER_RADIUS));

        // Draw shadow
        g2.setColor(new Color(0, 0, 0, 20));
        g2.fill(new RoundRectangle2D.Double(2, 2, CARD_WIDTH, CARD_HEIGHT, CORNER_RADIUS, CORNER_RADIUS));

        // Clip to rounded rectangle for image
        g2.setClip(new RoundRectangle2D.Double(0, 0, CARD_WIDTH, IMAGE_HEIGHT, CORNER_RADIUS, CORNER_RADIUS));

        // Draw restaurant image
        if (restaurantImage != null) {
            g2.drawImage(restaurantImage, 0, 0, CARD_WIDTH, IMAGE_HEIGHT, null);
        } else {
            // Placeholder gradient if no image
            GradientPaint gradient = new GradientPaint(0, 0, new Color(255, 230, 230),
                    0, IMAGE_HEIGHT, new Color(255, 200, 200));
            g2.setPaint(gradient);
            g2.fillRect(0, 0, CARD_WIDTH, IMAGE_HEIGHT);
        }

        // Reset clip
        g2.setClip(null);

        // Draw badges on top-left
        int badgeY = 15;
        if (restaurant.getType() != null && !restaurant.getType().isEmpty()) {
            drawBadge(g2, restaurant.getType(), 15, badgeY, new Color(239, 68, 68));
            badgeY += 35;
        }

        if (restaurant.hasStudentDiscount()) {
            String discountText = (int)(restaurant.getDiscountValue() * 100) + "% off";
            drawBadge(g2, discountText, 15, badgeY, new Color(236, 72, 153));
        }

        // Draw heart icon (favorite button)
        drawHeartIcon(g2, CARD_WIDTH - 45, 15, isFavorite);

        // Draw bottom section with restaurant info
        g2.setColor(Color.WHITE);
        g2.fillRect(0, IMAGE_HEIGHT, CARD_WIDTH, CARD_HEIGHT - IMAGE_HEIGHT);

        // Restaurant name
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.drawString(restaurant.getName(), 15, IMAGE_HEIGHT + 28);

        // Cuisine type
        g2.setColor(new Color(156, 163, 175));
        g2.setFont(new Font("Arial", Font.PLAIN, 14));
        g2.drawString(restaurant.getType(), 15, IMAGE_HEIGHT + 48);

        // Rating star and value
        if (restaurant.getRating() > 0) {
            drawStar(g2, CARD_WIDTH - 120, IMAGE_HEIGHT + 20, new Color(251, 191, 36));
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            String ratingText = String.format("%.1f", restaurant.getRating());
            g2.drawString(ratingText, CARD_WIDTH - 98, IMAGE_HEIGHT + 28);

            // Review count (placeholder)
            g2.setColor(new Color(156, 163, 175));
            g2.setFont(new Font("Arial", Font.PLAIN, 12));
            g2.drawString("(100+)", CARD_WIDTH - 65, IMAGE_HEIGHT + 28);
        }

        // Delivery time icon and text
        drawClockIcon(g2, CARD_WIDTH - 105, IMAGE_HEIGHT + 38, new Color(236, 72, 153));
        g2.setColor(new Color(156, 163, 175));
        g2.setFont(new Font("Arial", Font.PLAIN, 14));
        g2.drawString("25 min", CARD_WIDTH - 80, IMAGE_HEIGHT + 48);

        g2.dispose();
    }

    private void drawBadge(Graphics2D g2, String text, int x, int y, Color color) {
        g2.setColor(color);
        FontMetrics fm = g2.getFontMetrics(new Font("Arial", Font.BOLD, 12));
        int padding = 8;
        int width = fm.stringWidth(text) + padding * 2;
        int height = 22;

        g2.fill(new RoundRectangle2D.Double(x, y, width, height, 8, 8));

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.drawString(text, x + padding, y + 16);
    }

    private void drawHeartIcon(Graphics2D g2, int x, int y, boolean filled) {
        int size = 30;
        g2.setColor(filled ? new Color(236, 72, 153) : Color.WHITE);
        g2.fillOval(x, y, size, size);

        // Draw heart shape
        g2.setColor(filled ? Color.WHITE : new Color(236, 72, 153));
        int hx = x + size / 2;
        int hy = y + size / 2;
        int[] xPoints = {hx, hx - 4, hx - 6, hx - 4, hx, hx + 4, hx + 6, hx + 4};
        int[] yPoints = {hy + 4, hy + 4, hy, hy - 4, hy - 2, hy - 4, hy, hy + 4};
        g2.fillPolygon(xPoints, yPoints, 8);
    }

    private void drawStar(Graphics2D g2, int x, int y, Color color) {
        g2.setColor(color);
        int[] xPoints = {x + 8, x + 10, x + 15, x + 11, x + 13, x + 8, x + 3, x + 5, x + 1, x + 6};
        int[] yPoints = {y, y + 6, y + 6, y + 10, y + 15, y + 12, y + 15, y + 10, y + 6, y + 6};
        g2.fillPolygon(xPoints, yPoints, 10);
    }

    private void drawClockIcon(Graphics2D g2, int x, int y, Color color) {
        int size = 16;
        g2.setColor(color);
        g2.fillOval(x, y, size, size);

        // Draw clock hands
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2));
        int cx = x + size / 2;
        int cy = y + size / 2;
        g2.drawLine(cx, cy, cx, cy - 4);  // Hour hand
        g2.drawLine(cx, cy, cx + 3, cy);  // Minute hand
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

    public Restaurant getRestaurant() {
        return restaurant;
    }

    // Demo main method
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Restaurant Panel Demo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));

            // Create sample restaurants
            Restaurant r1 = new Restaurant("1", "Sweet Tooth Café", "123 Main St",
                    "Desserts", 3.6, true, 0.15);
            Restaurant r2 = new Restaurant("2", "Pizza Paradise", "456 Oak Ave",
                    "Italian", 4.2, true, 0.10);
            Restaurant r3 = new Restaurant("3", "Sushi Master", "789 Pine Rd",
                    "Japanese", 4.8, false, 0);

            frame.add(new RestaurantPanel(r1));
            frame.add(new RestaurantPanel(r2));
            frame.add(new RestaurantPanel(r3));

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
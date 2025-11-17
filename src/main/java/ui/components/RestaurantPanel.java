package ui.components;

import entity.Restaurant;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

public class RestaurantPanel extends JPanel {
    private static final int CARD_WIDTH = 280;
    private static final int CARD_HEIGHT = 200;
    private static final int IMAGE_HEIGHT = 140;
    private static final int CORNER_RADIUS = 16;
    private static final int HEART_SIZE = 30;

    private final Restaurant restaurant;
    private BufferedImage restaurantImage;
    private boolean isFavorite = false;
    private HeartClickListener heartClickListener;

    public interface HeartClickListener {
        void onHeartClicked(Restaurant restaurant, boolean newFavoriteState);
    }

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

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int heartX = CARD_WIDTH - 45;
                int heartY = 15;

                if (e.getX() >= heartX && e.getX() <= heartX + HEART_SIZE &&
                        e.getY() >= heartY && e.getY() <= heartY + HEART_SIZE) {
                    toggleFavorite();
                    if (heartClickListener != null) {
                        heartClickListener.onHeartClicked(restaurant, isFavorite);
                    }
                } else {
                    System.out.println("Clicked on: " + restaurant.getName());
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
        Graphics2D g2 = (Graphics2D) g.create();

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
            GradientPaint gradient = new GradientPaint(0, 0, new Color(255, 230, 230),
                    0, IMAGE_HEIGHT, new Color(255, 200, 200));
            g2.setPaint(gradient);
            g2.fillRect(0, 0, CARD_WIDTH, IMAGE_HEIGHT);
        }

        g2.setClip(null);

        int badgeY = 15;
        if (restaurant.getType() != null && !restaurant.getType().isEmpty()) {
            drawBadge(g2, restaurant.getType(), 15, badgeY, new Color(239, 68, 68));
            badgeY += 35;
        }

        if (restaurant.hasStudentDiscount()) {
            String discountText = (int)(restaurant.getDiscountValue() * 100) + "% off";
            drawBadge(g2, discountText, 15, badgeY, new Color(236, 72, 153));
        }

        drawHeartIcon(g2, CARD_WIDTH - 45, 15, isFavorite);

        g2.setColor(Color.WHITE);
        g2.fillRect(0, IMAGE_HEIGHT, CARD_WIDTH, CARD_HEIGHT - IMAGE_HEIGHT);

        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        String restaurantName = restaurant.getName();
        FontMetrics nameMetrics = g2.getFontMetrics();
        int maxNameWidth = CARD_WIDTH - 30;

        if (nameMetrics.stringWidth(restaurantName) > maxNameWidth) {
            restaurantName = truncateText(restaurantName, nameMetrics, maxNameWidth);
        }
        g2.drawString(restaurantName, 15, IMAGE_HEIGHT + 28);

        g2.setColor(new Color(156, 163, 175));
        g2.setFont(new Font("Arial", Font.PLAIN, 14));
        String cuisineType = restaurant.getType();
        FontMetrics cuisineMetrics = g2.getFontMetrics();
        int maxCuisineWidth = CARD_WIDTH - 30;

        if (cuisineMetrics.stringWidth(cuisineType) > maxCuisineWidth) {
            cuisineType = truncateText(cuisineType, cuisineMetrics, maxCuisineWidth);
        }
        g2.drawString(cuisineType, 15, IMAGE_HEIGHT + 48);

        if (restaurant.getRating() > 0) {
            drawStar(g2, CARD_WIDTH - 120, IMAGE_HEIGHT + 20, new Color(251, 191, 36));
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            String ratingText = String.format("%.1f", restaurant.getRating());
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

        String ellipsis = "...";
        int ellipsisWidth = metrics.stringWidth(ellipsis);

        int low = 0;
        int high = text.length();

        while (low < high) {
            int mid = (low + high) / 2;
            String candidate = text.substring(0, mid) + ellipsis;

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
        Path2D heart = new Path2D.Double();

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
        int[] xPoints = {x + 8, x + 10, x + 15, x + 11, x + 13, x + 8, x + 3, x + 5, x + 1, x + 6};
        int[] yPoints = {y, y + 6, y + 6, y + 10, y + 15, y + 12, y + 15, y + 10, y + 6, y + 6};
        g2.fillPolygon(xPoints, yPoints, 10);
    }

    private void drawClockIcon(Graphics2D g2, int x, int y, Color color) {
        int size = 16;
        g2.setColor(color);
        g2.fillOval(x, y, size, size);

        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2));
        int cx = x + size / 2;
        int cy = y + size / 2;
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

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setHeartClickListener(HeartClickListener listener) {
        this.heartClickListener = listener;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Restaurant Panel Demo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));

            Restaurant r1 = new Restaurant.Builder()
                    .id("1")
                    .name("Sweet Tooth Café")
                    .location("123 Main St", "https://maps.example.com/1", 43.6532, -79.3832)
                    .type("Desserts")
                    .rating(3.6, 150)
                    .contact("+1-416-555-0101", "https://sweettooth.example.com")
                    .studentDiscount(true, 0.15)
                    .openingHours(java.util.List.of("Mon-Fri: 8am-8pm"))
                    .photoIds(java.util.List.of("photo1a"))
                    .build();

            Restaurant r2 = new Restaurant.Builder()
                    .id("2")
                    .name("Pizza Paradise")
                    .location("456 Oak Ave", "https://maps.example.com/2", 43.6612, -79.3952)
                    .type("Italian")
                    .rating(4.2, 320)
                    .contact("+1-416-555-0102", "https://pizzaparadise.example.com")
                    .studentDiscount(true, 0.10)
                    .openingHours(java.util.List.of("Mon-Sun: 11am-11pm"))
                    .photoIds(java.util.List.of("photo2a"))
                    .build();

            Restaurant r3 = new Restaurant.Builder()
                    .id("3")
                    .name("Sushi Master")
                    .location("789 Pine Rd", "https://maps.example.com/3", 43.6482, -79.4012)
                    .type("Japanese")
                    .rating(4.8, 520)
                    .contact("+1-416-555-0103", "https://sushimaster.example.com")
                    .studentDiscount(false, 0)
                    .openingHours(java.util.List.of("Tue-Sun: 12pm-10pm"))
                    .photoIds(java.util.List.of("photo3a"))
                    .build();

            RestaurantPanel panel1 = new RestaurantPanel(r1);
            RestaurantPanel panel2 = new RestaurantPanel(r2);
            RestaurantPanel panel3 = new RestaurantPanel(r3);

            panel1.setHeartClickListener((restaurant, newState) -> {
                System.out.println(restaurant.getName() + " favorite: " + newState);
            });

            frame.add(panel1);
            frame.add(panel2);
            frame.add(panel3);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
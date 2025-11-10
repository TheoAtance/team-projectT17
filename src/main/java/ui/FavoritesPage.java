package ui;

import javax.swing.*;
import java.awt.*;

public class FavoritesPage extends JFrame {
    public FavoritesPage() {
        setTitle("Favorite Restaurants");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(new JLabel("Favorites List Here"), BorderLayout.CENTER);
    }

    // Temporary launcher for development
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FavoritesPage().setVisible(true));
    }
}

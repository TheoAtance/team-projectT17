package app;

import data_access.FirebaseService;
import interface_adapter.ViewManagerModel;
import view.ViewManager;

import javax.swing.*;
import java.awt.*;

/**
 * The Main class for the Restaurant Review Application.
 * This is the entry point that initializes Firebase and launches the UI.
 */
public class Main {
    public static void main(String[] args) {
        // Initialize Firebase
        System.out.println("Initializing Firebase...");
        FirebaseService firebaseService = FirebaseService.getInstance();
        System.out.println("Firebase initialized successfully.");

        // Create the main application window
        JFrame application = new JFrame("UofT Eats - Restaurant Review App");
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        application.setSize(600, 500);

        // Create CardLayout for view switching
        CardLayout cardLayout = new CardLayout();
        JPanel views = new JPanel(cardLayout);
        application.add(views);

        // Create ViewManagerModel
        ViewManagerModel viewManagerModel = new ViewManagerModel();
        new ViewManager(views, cardLayout, viewManagerModel);

        // Build the application
        AppBuilder appBuilder = new AppBuilder(views, viewManagerModel, cardLayout);
        appBuilder.addLoginView();
        appBuilder.addRegisterView();
        appBuilder.addLoggedInView();
        appBuilder.addFilterView();

        // Set initial view
        viewManagerModel.setState("login"); // Start at login screen
        viewManagerModel.firePropertyChange();

        // Display the application
        application.setLocationRelativeTo(null); // Center on screen
        application.setVisible(true);

        System.out.println("Application launched successfully!");
    }
}
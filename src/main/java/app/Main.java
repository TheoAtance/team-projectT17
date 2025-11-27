package app;

import data_access.FirebaseService;
import interface_adapter.ViewManagerModel;
import view.ViewManager;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * The Main class for the Restaurant Review Application.
 * This is the entry point that initializes Firebase and launches the UI.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        // Initialize Firebase
        System.out.println("Initializing Firebase...");
        FirebaseService firebaseService = FirebaseService.getInstance();
        System.out.println("Firebase initialized successfully.");

        System.setProperty("sun.java2d.opengl", "true");


        // Build the application
        AppBuilder appBuilder = new AppBuilder();
        JFrame application = appBuilder
                .addRestaurantView()
                .addLoginView()
                .addRegisterView()
                .addLoggedInView()
                .addFilterView()
                .addRestaurantUseCase()
                .addAddReviewUseCase()
                .build();



        // Display the application
        application.pack();
        application.setLocationRelativeTo(null); // Center on screen
        application.setVisible(true);

        System.out.println("Application launched successfully!");
    }
}
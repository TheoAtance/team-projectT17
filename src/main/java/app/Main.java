package app;

import data_access.FirebaseService;
import java.io.IOException;
import javax.swing.JFrame;

/**
 * The Main class for the Restaurant Review Application. This is the entry point that initializes
 * Firebase and launches the UI.
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
        .addDisplayReviewUseCase()
        .build();

    // Display the application
    application.pack();
    application.setLocationRelativeTo(null); // Center on screen
    application.setVisible(true);

    System.out.println("Application launched successfully!");
  }
}
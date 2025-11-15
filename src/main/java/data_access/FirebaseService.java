package data_access;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.cloud.FirestoreClient;
import com.google.cloud.firestore.Firestore;

import java.io.FileInputStream;
import java.io.IOException;

public class FirebaseService {
    private static FirebaseService instance;
    private final FirebaseAuth auth;
    private final Firestore db;
    private final String webApiKey;

    private FirebaseService() {
        try {
            FileInputStream serviceAccount = new FileInputStream("service-account-key.json");

            this.webApiKey = "AIzaSyBfDKk3pcc-MTH2d7ffDWjvVRseIb5xcho";

            // DEBUG: Print the API key
            System.out.println("   DEBUG FirebaseService:");
            System.out.println("   webApiKey SET TO: '" + this.webApiKey + "'");
            System.out.println("   webApiKey length: " + this.webApiKey.length());
            System.out.println("   webApiKey is null? " + (this.webApiKey == null));
            System.out.println("   webApiKey is empty? " + this.webApiKey.isEmpty());

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setProjectId("uoft-eats-bd368")
                    .build();

            FirebaseApp.initializeApp(options);

            this.auth = FirebaseAuth.getInstance();
            this.db = FirestoreClient.getFirestore();

            System.out.println("Firebase initialized successfully.");

        } catch (IOException e) {
            System.err.println("FATAL: Could not initialize Firebase. Check 'service-account-key.json' file location and permissions.");
            throw new RuntimeException("Firebase initialization failed: " + e.getMessage(), e);
        }
    }

    public static synchronized FirebaseService getInstance() {
        if (instance == null) {
            instance = new FirebaseService();
        }
        return instance;
    }

    public FirebaseAuth getAuth() {
        return auth;
    }

    public Firestore getFirestore() {
        return db;
    }

    public String getWebApiKey() {
        System.out.println("   DEBUG getWebApiKey() called:");
        System.out.println("   Returning: '" + webApiKey + "'");
        System.out.println("   Length: " + (webApiKey != null ? webApiKey.length() : "NULL"));
        return webApiKey;
    }
}
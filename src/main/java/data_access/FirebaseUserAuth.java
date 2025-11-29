package data_access;

import com.sun.net.httpserver.HttpServer;
import use_case.AuthFailureException;
import use_case.GoogleAuthResult;
import use_case.IAuthGateway;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.awt.Desktop; // For launching the default browser
import javax.swing.JOptionPane; // For displaying the input dialog

/**
 * Rigorous implementation of IAuthGateway using the public Firebase Authentication REST API.
 * This requires the Firebase Web API Key for secure client-side operations.
 *
 * NOTE: This version uses the Jackson ObjectMapper for reliable JSON parsing.
 * MUST add Jackson dependencies (e.g., jackson-databind) to the project.
 */
public class FirebaseUserAuth implements IAuthGateway {

    private final String webApiKey;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper; // For robust JSON handling
    private String currentUid;

    // REST API Endpoints
    private static final String SIGN_UP_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=";
    private static final String SIGN_IN_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=";
    // URL for exchanging a Google Access Token for a Firebase ID Token (used in loginWithGoogle)
    private static final String GOOGLE_EXCHANGE_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithIdp?key=";

    public FirebaseUserAuth() {
        this.webApiKey =  FirebaseService.getInstance().getWebApiKey();
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();

        // DEBUG
        System.out.println("DEBUG FirebaseUserAuth constructor:");
        System.out.println("   webApiKey received: '" + this.webApiKey + "'");
        System.out.println("   webApiKey length: " + (this.webApiKey != null ? this.webApiKey.length() : "NULL"));
    }

    /**
     * Executes the secure login request to the Firebase Auth REST API.
     * @return The User's unique ID (uid).
     */
    @Override
    public String loginWithEmailAndPassword(String email, String password) throws AuthFailureException {
        String jsonPayload = String.format(
                "{\"email\":\"%s\", \"password\":\"%s\", \"returnSecureToken\":true}",
                email, password);

        currentUid = executeAuthRequest(SIGN_IN_URL, jsonPayload, "Login");

        return currentUid;
    }

    /**
     * Executes the secure registration request to the Firebase Auth REST API.
     * @return The User's unique ID (uid).
     */
    @Override
    public String registerWithEmailAndPassword(String email, String password) throws AuthFailureException {
        String jsonPayload = String.format(
                "{\"email\":\"%s\", \"password\":\"%s\", \"returnSecureToken\":true}",
                email, password);

        currentUid = executeAuthRequest(SIGN_UP_URL, jsonPayload, "Registration");

        return currentUid;
    }

    /**
     * A utility method to handle the HTTP request execution and error parsing.
     * @param baseUrl The Firebase REST endpoint.
     * @param payload The JSON body.
     * @param action The operation name (Login/Registration) for error messaging.
     * @return The UID of the authenticated user.
     */
    private String executeAuthRequest(String baseUrl, String payload, String action) throws AuthFailureException {
        // DEBUG: Check webApiKey at the START of this method
        System.out.println("   DEBUG executeAuthRequest - START:");
        System.out.println("   this.webApiKey = '" + this.webApiKey + "'");
        System.out.println("   this.webApiKey length = " + (this.webApiKey != null ? this.webApiKey.length() : "NULL"));

        String fullUrl = baseUrl + webApiKey;

        System.out.println("   DEBUG: Attempting " + action);
        System.out.println("   Base URL: " + baseUrl);
        System.out.println("   webApiKey in this method: '" + this.webApiKey + "'");
        System.out.println("   Full URL: " + fullUrl);
        System.out.println("   API Key (first 20 chars): " + webApiKey.substring(0, Math.min(20, webApiKey.length())) + "...");
        System.out.println("   Payload: " + payload);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(fullUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("   Response Status: " + response.statusCode());
            System.out.println("   Response Body: " + response.body());

            if (response.statusCode() == 200) {
                return extractUidFromJson(response.body());
            } else {
                throw parseAuthError(response.body(), action);
            }
        } catch (IOException | InterruptedException e) {
            throw new AuthFailureException(action + " failed due to network error: " + e.getMessage());
        }
    }

    /**
     * Initiates Google Sign-in. In a desktop environment, this requires the user
     * to obtain a Google Access Token via a browser flow and manually provide it.
     * The token is then exchanged for a Firebase UID and ID token.
     * @return a GoogleAuthResult object which contains the user's uid, email and name.
     */
    @Override
    public GoogleAuthResult loginWithGoogle() throws AuthFailureException, IOException {
        System.out.println("\n--- Desktop Google Sign-in Flow Initiated ---");

        // 1. Get the Google Access Token
        String googleAccessToken = getGoogleAccessTokenFromCode();

        if (googleAccessToken == null || googleAccessToken.isEmpty()) {
            throw new AuthFailureException("Google sign-in cancelled or token exchange failed.");
        }

        // 2. Exchange the Google Access Token for Firebase credentials
        String jsonPayload = String.format(
                "{\"postBody\":\"access_token=%s&providerId=google.com\", \"requestUri\":\"http://localhost\", \"returnIdpCredential\":true, \"returnSecureToken\":true}",
                googleAccessToken);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GOOGLE_EXCHANGE_URL + webApiKey))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // Parse the response to extract uid, email, and display name
                GoogleAuthResult result = extractGoogleAuthResult(response.body());
                currentUid= result.getUid();
                return result;

            } else {
                throw parseAuthError(response.body(), "Google Sign-in");
            }
        } catch (IOException | InterruptedException e) {
            throw new AuthFailureException("Google Sign-in failed due to network error: " + e.getMessage());
        }


    }

    @Override
    public String getCurrentUserUid(){
        return currentUid;
    }

    /**
     * Extracts uid, email, and displayName from Firebase's Google sign-in response.
     */
    private GoogleAuthResult extractGoogleAuthResult(String json) throws AuthFailureException {
        try {
            JsonNode rootNode = objectMapper.readTree(json);

            // Extract UID (localId)
            JsonNode localIdNode = rootNode.get("localId");
            if (localIdNode == null) {
                throw new AuthFailureException("Could not parse user ID from Google sign-in response.");
            }
            String uid = localIdNode.asText();

            // Extract email
            JsonNode emailNode = rootNode.get("email");
            String email = (emailNode != null) ? emailNode.asText() : uid + "@google.com";

            // Extract display name (from federatedId or displayName field)
            String displayName = "Google User";
            JsonNode displayNameNode = rootNode.get("displayName");
            if (displayNameNode != null && !displayNameNode.asText().isEmpty()) {
                displayName = displayNameNode.asText();
            } else {
                // Fallback: try to extract from fullName or firstName
                JsonNode fullNameNode = rootNode.get("fullName");
                if (fullNameNode != null && !fullNameNode.asText().isEmpty()) {
                    displayName = fullNameNode.asText();
                } else {
                    JsonNode firstNameNode = rootNode.get("firstName");
                    if (firstNameNode != null && !firstNameNode.asText().isEmpty()) {
                        displayName = firstNameNode.asText();
                    }
                }
            }

            return new GoogleAuthResult(uid, email, displayName);

        } catch (IOException e) {
            throw new AuthFailureException("Failed to parse Google sign-in response: " + e.getMessage());
        }
    }

    /**
     * Handles the Swing UI interaction to retrieve the Google **Authorization Code**,
     * then exchanges that code for the required **Access Token**.
     * * This method is the core of the Google login flow.
     * @return The final Google Access Token, or null if cancelled/failed.
     */
    private String getGoogleAccessTokenFromCode() throws AuthFailureException, IOException {
        if (!Desktop.isDesktopSupported()) {
            JOptionPane.showMessageDialog(null, "Desktop operations not supported. Cannot launch browser.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        String clientId = "871031720059-pr7sphk9jqfkgrj213pub8hli4dr3rs8.apps.googleusercontent.com";
        // The Client Secret is only used server-side (in this Java app) for the code exchange.
        String clientSecret = "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";
        String redirectUri = "http://127.0.0.1:8888/";

        // Start a simple local HTTP server to receive the OAuth callback
        final String[] authCodeHolder = new String[1];
        HttpServer server = HttpServer.create(new InetSocketAddress(8888), 0);
        server.createContext("/", exchange -> {
            String query = exchange.getRequestURI().getQuery();
            if (query != null && query.contains("code=")) {
                String code = query.substring(query.indexOf("code=") + 5);
                authCodeHolder[0] = code.split("&")[0]; // Extract code only
                String response = "You may now close this tab and return to the app.";
                exchange.sendResponseHeaders(200, response.length());
                exchange.getResponseBody().write(response.getBytes());
                exchange.close();
            }
        });
        server.start();

        // --- STEP 1: Get Authorization Code from User via Browser/Swing ---

        // Use response_type=code as required for OOB redirect_uri
        String authUrl = String.format(
                "https://accounts.google.com/o/oauth2/v2/auth?scope=email%%20profile&access_type=offline&include_granted_scopes=true&response_type=code&client_id=%s&redirect_uri=%s",
                clientId, redirectUri
        );

        String authCode;
        try {

            // Wait for Google to redirect back with the code
            Desktop.getDesktop().browse(new URI(authUrl));
            while (authCodeHolder[0] == null) {
                Thread.sleep(200);
            }
            authCode = authCodeHolder[0];
            server.stop(0);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Could not launch web browser: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        } catch (java.net.URISyntaxException e) {
            JOptionPane.showMessageDialog(null, "Invalid Google Auth URL configuration.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (authCode == null || authCode.isEmpty()) {
            return null;
        }

        // --- STEP 2: Exchange Authorization Code for Access Token ---

        String tokenExchangeUrl = "https://oauth2.googleapis.com/token";
        String tokenPayload = String.format(
                "code=%s&client_id=%s&client_secret=%s&redirect_uri=%s&grant_type=authorization_code",
                authCode, clientId, clientSecret, redirectUri
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(tokenExchangeUrl))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(tokenPayload))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // Successful exchange: extract access_token
                JsonNode rootNode = objectMapper.readTree(response.body());
                JsonNode accessTokenNode = rootNode.get("access_token");

                if (accessTokenNode != null) {
                    return accessTokenNode.asText();
                }
            }

            // If response failed or access token wasn't found
            throw new AuthFailureException("Failed to exchange authorization code for access token. Server response: " + response.body());

        } catch (IOException | InterruptedException e) {
            throw new AuthFailureException("Google token exchange failed due to network error: " + e.getMessage());
        }
    }

    /**
     * Clears the current user session (signs out the user).
     * Since this implementation uses the REST API (stateless), 'logout' is a NO-OP
     * in the data access layer. The calling layer (use case/controller) is responsible
     * for clearing any locally stored Firebase ID Tokens or session data.
     */
    @Override
    public void logout() {
        // Implementation note: No API call needed for REST-based authentication logout.
        System.out.println("Logout: Data Access Layer executed. Local session cleanup is required upstream.");
    }

    // --- Utility Methods for JSON Parsing ---

    /**
     * Extracts the localId (UID) from a successful Firebase Auth JSON response using Jackson.
     */
    private String extractUidFromJson(String json) {
        try {
            JsonNode rootNode = objectMapper.readTree(json);
            JsonNode localIdNode = rootNode.get("localId");
            if (localIdNode != null) {
                return localIdNode.asText();
            }
        } catch (IOException e) {
            // Fallthrough to throw exception below
        }
        throw new AuthFailureException("Authentication successful, but could not parse user ID from response.");
    }

    /**
     * Parses the error message from a failed Firebase Auth JSON response using Jackson.
     */
    private AuthFailureException parseAuthError(String json, String action) {
        String reason = "Unknown Error";
        try {
            JsonNode rootNode = objectMapper.readTree(json);
            // Firebase errors are usually nested under "error" -> "message"
            JsonNode messageNode = rootNode.path("error").path("message");
            if (messageNode.isTextual()) {
                reason = messageNode.asText();
            }
        } catch (IOException e) {
            // Cannot even parse the error JSON, return generic network error.
            return new AuthFailureException(action + " failed: Failed to parse error response.");
        }

        // Translate common Firebase errors for better UX
        if (reason.contains("EMAIL_EXISTS")) {
            return new AuthFailureException(action + " failed: Email already in use.");
        } else if (reason.contains("INVALID_PASSWORD") || reason.contains("EMAIL_NOT_FOUND")) {
            return new AuthFailureException(action + " failed: Invalid email or password.");
        } else if (reason.contains("WEAK_PASSWORD")) {
            return new AuthFailureException(action + " failed: Password must be at least 6 characters.");
        }

        return new AuthFailureException(action + " failed: " + reason);
    }
}
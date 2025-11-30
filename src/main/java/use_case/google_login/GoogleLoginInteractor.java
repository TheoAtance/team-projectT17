package use_case.google_login;

import data_access.CurrentUser;
import entity.User;
import use_case.GoogleAuthResult;
import use_case.IAuthGateway;
import use_case.IUserRepo;

import java.io.IOException;

/**
 * Interactor for the Google Login/Registration Use Case (OAuth).
 * Handles the logic for checking if a Google-authenticated user
 * is new (registration) or existing (login).
 */
public class GoogleLoginInteractor implements GoogleLoginInputBoundary {

    private final IAuthGateway authGateway;
    private final IUserRepo userRepository;
    private final GoogleLoginOutputBoundary loginPresenter;

    public GoogleLoginInteractor(
            IAuthGateway authGateway,
            IUserRepo userRepository,
            GoogleLoginOutputBoundary loginPresenter) {
        this.authGateway = authGateway;
        this.userRepository = userRepository;
        this.loginPresenter = loginPresenter;
    }

    @Override
    public void execute() {
        GoogleAuthResult googleAuthResult;

        try {
            // 1. Initiate Google OAuth flow and get user data
            googleAuthResult = authGateway.loginWithGoogle();

        } catch (RuntimeException | IOException e) {
            // User cancelled or OAuth failed
            loginPresenter.prepareFailView("Google Sign-In failed: " + e.getMessage());
            return;
        }

        String uid = googleAuthResult.getUid();
        String email = googleAuthResult.getEmail();
        String displayName = googleAuthResult.getDisplayName();

        // 2. Check if this user already exists in Firestore
        boolean isExistingUser = userRepository.existsByUid(uid);

        User user;

        if (!isExistingUser) {
            // REGISTRATION Flow: Create and save new User Entity
            user = new User(uid, email, displayName);

            try {
                userRepository.save(user);

                System.out.println("New Google user registered: " + displayName);
            } catch (RuntimeException e) {
                // Failed to save profile after successful Google auth
                authGateway.logout();
                loginPresenter.prepareFailView("Google Sign-In failed: Could not save user profile.");
                return;
            }

        } else {
            // LOGIN Flow: Retrieve existing User Entity from database
            user = userRepository.getUserByUid(uid);
            System.out.println("Existing Google user logged in: " + user.getNickname());
        }

        // 3. Success - Prepare output data
        GoogleLoginOutputData outputData = new GoogleLoginOutputData(
                user.getNickname(),
                true,
                user.getUid()
        );

        userRepository.loadAllUsers();

        // Debugging
        CurrentUser test = new CurrentUser(authGateway, userRepository);
        String curUser = test.getCurrentUser().getNickname();
        System.out.println("Google Login Current User test: " + curUser);

        loginPresenter.prepareSuccessView(outputData);
    }
}

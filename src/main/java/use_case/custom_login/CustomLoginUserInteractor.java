package use_case.custom_login;

import data_access.CurrentUser;
import entity.User;
import org.checkerframework.checker.units.qual.C;
import use_case.IAuthGateway;
import use_case.IUserRepo;

import java.util.Optional;

/**
 * Interactor for the Login Use Case.
 */
public class CustomLoginUserInteractor implements CustomLoginInputBoundary {

    private final IAuthGateway authGateway;
    private final IUserRepo userRepository;
    private final CustomLoginOutputBoundary loginPresenter;

    public CustomLoginUserInteractor(
            IAuthGateway authGateway,
            IUserRepo userRepository,
            CustomLoginOutputBoundary loginPresenter) {
        this.authGateway = authGateway;
        this.userRepository = userRepository;
        this.loginPresenter = loginPresenter;
    }

    /**
     * Executes the login process using the provided input data (email/password).
     */
    @Override
    public void execute(CustomLoginInputData inputData) {
        String email = inputData.getEmail();
        String password = inputData.getPassword();
        String uid = "";

        try {
            // 1. Authenticate using Firebase Auth
            uid = authGateway.loginWithEmailAndPassword(email, password);

        } catch (RuntimeException e) {
            // Alternative Flow: Credentials don't match or the account doesn't exist.
            loginPresenter.prepareFailView("Login failed: " + e.getMessage());
            return;
        }

        // 2. Authentication was successful, now fetch the corresponding User Entity (profile)
        User user = userRepository.getUserByUid(uid);

        if (user == null) {
            // 3. The user was authenticated by Firebase, but their profile data is missing from Firestore database.
            authGateway.logout(); // Clear the successful login session.
            loginPresenter.prepareFailView("Account found, but profile data is missing.");
            return;
        }

        // 4. Success: User is logged in and profile data is retrieved.

        // Prepare output data for the presenter
        CustomLoginOutputData outputData = new CustomLoginOutputData(
                user.getNickname(),
                true,
                user.getUid()
        );

        userRepository.loadAllUsers();

        CurrentUser test = new CurrentUser(authGateway, userRepository);
        String curUser = test.getCurrentUser().getNickname();
        System.out.println("Custom Login Current User test: " + curUser);

        loginPresenter.prepareSuccessView(outputData);
    }
}
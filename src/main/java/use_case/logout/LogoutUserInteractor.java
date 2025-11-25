package use_case.logout;

import data_access.CurrentUser;
import use_case.IAuthGateway;

/**
 * Interactor for the Logout Use Case.
 * Handles the business logic for logging out a user.
 */
public class LogoutUserInteractor implements LogoutInputBoundary {

    private final IAuthGateway authGateway;
    private final LogoutOutputBoundary logoutPresenter;
    private final CurrentUser currentUser;

    public LogoutUserInteractor(IAuthGateway authGateway, LogoutOutputBoundary logoutPresenter, CurrentUser currentUser) {
        this.authGateway = authGateway;
        this.logoutPresenter = logoutPresenter;
        this.currentUser = currentUser;
    }

    @Override
    public void execute() {
        // Call Firebase logout (clears any server-side session if applicable)
        authGateway.logout();

        //clear in memory current user
        currentUser.clearCache();

        // Prepare success output
        LogoutOutputData outputData = new LogoutOutputData("Logged out successfully");
        logoutPresenter.prepareSuccessView(outputData);
    }
}
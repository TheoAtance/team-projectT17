package use_case.logout;

import use_case.IAuthGateway;

/**
 * Interactor for the Logout Use Case.
 * Handles the business logic for logging out a user.
 */
public class LogoutUserInteractor implements LogoutInputBoundary {

    private final IAuthGateway authGateway;
    private final LogoutOutputBoundary logoutPresenter;

    public LogoutUserInteractor(IAuthGateway authGateway, LogoutOutputBoundary logoutPresenter) {
        this.authGateway = authGateway;
        this.logoutPresenter = logoutPresenter;
    }

    @Override
    public void execute() {
        // Call Firebase logout (clears any server-side session if applicable)
        authGateway.logout();

        // Prepare success output
        LogoutOutputData outputData = new LogoutOutputData("Logged out successfully");
        logoutPresenter.prepareSuccessView(outputData);
    }
}
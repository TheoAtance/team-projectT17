package use_case.custom_register;

import data_access.CurrentUser;
import entity.User;
import use_case.IAuthGateway;
import use_case.IUserRepo;

/**
 * The Interactor for the Register Use Case.
 */
public class RegisterUserInteractor implements RegisterInputBoundary {

  private final IAuthGateway authGateway;
  private final IUserRepo userRepository;
  private final RegisterOutputBoundary registerPresenter;

  public RegisterUserInteractor(
      IAuthGateway authGateway,
      IUserRepo userRepository,
      RegisterOutputBoundary registerPresenter) {
    this.authGateway = authGateway;
    this.userRepository = userRepository;
    this.registerPresenter = registerPresenter;
  }

  @Override
  public void execute(RegisterInputData inputData) {
    // 1. Validate passwords match
    if (!inputData.getPassword().equals(inputData.getRepeatPassword())) {
      registerPresenter.prepareFailView("Passwords do not match.");
      return;
    }

    // 2. Validate non-empty nickname
    if (inputData.getNickname() == null || inputData.getNickname().trim().isEmpty()) {
      registerPresenter.prepareFailView("Nickname cannot be empty.");
      return;
    }

    String email = inputData.getEmail();
    String password = inputData.getPassword();
    String nickname = inputData.getNickname();

    try {
      // 2. Create account in Firebase Auth
      String uid = authGateway.registerWithEmailAndPassword(email, password);

      // 3. Create and save User Entity
      User newUser = new User(uid, email, nickname);

      try {
        userRepository.save(newUser);

      } catch (RuntimeException e) {
        // Failed to save profile after successful auth
        registerPresenter.prepareFailView("Registration failed: Could not save user profile.");
        return;
      }

      // 4. Success
      RegisterOutputData outputData = new RegisterOutputData(
          newUser.getNickname(),
          true,
          newUser.getUid()
      );

      userRepository.loadAllUsers();

      CurrentUser test = new CurrentUser(authGateway, userRepository);
      String curUser = test.getCurrentUser().getNickname();
      System.out.println("Register User Current User test: " + curUser);

      registerPresenter.prepareSuccessView(outputData);

    } catch (RuntimeException e) {
      registerPresenter.prepareFailView("Registration failed: " + e.getMessage());
    }
  }
}
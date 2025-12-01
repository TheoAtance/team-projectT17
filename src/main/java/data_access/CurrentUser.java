package data_access;

import entity.User;
import use_case.IAuthGateway;
import use_case.IUserRepo;

public class CurrentUser {

  private final IAuthGateway userAuth;
  private final IUserRepo userRepo;
  private User currentUser = null;

  public CurrentUser(IAuthGateway userAuth, IUserRepo userRepo) {
    this.userAuth = userAuth;
    this.userRepo = userRepo;
  }

  public User getCurrentUser() {
    String currentUid = userAuth.getCurrentUserUid();

    if (currentUser != null) {
      return currentUser;
    }

    return userRepo.getUserByUid(currentUid);
  }

  /**
   * Method to clear current user when logging out.
   */
  public void clearCache() {
    this.currentUser = null;
  }
}

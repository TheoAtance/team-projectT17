package data_access;

import entity.User;
import use_case.IAuthGateway;
import use_case.IUserRepo;

import java.util.Optional;

public class CurrentUser {
    private User currentUser = null;

    private final IAuthGateway userAuth;
    private final IUserRepo userRepo;

    public CurrentUser(IAuthGateway userAuth, IUserRepo userRepo) {
        this.userAuth = userAuth;
        this.userRepo = userRepo;
    }
    
    public User getCurrentUser(){
        String currentUid = userAuth.getCurrentUserUid();

        if (currentUser != null){
            return currentUser;
        }

        return userRepo.getUserByUid(currentUid);
    }

    /**
     * Method to clear current user when logging out
     */
    public void clearCache() {
        this.currentUser = null;
    }
}

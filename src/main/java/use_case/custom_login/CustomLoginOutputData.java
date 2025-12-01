package use_case.custom_login;

/**
 * Output Data for the Login Use Case. Carries the result back to the Presenter, including user's
 * nickname if login is successful.
 */
public class CustomLoginOutputData {

  private final String nickname;
  private final boolean success;
  private final String uid; // Useful for passing state to other use cases/views

  public CustomLoginOutputData(String nickname, boolean success, String uid) {
    this.nickname = nickname;
    this.success = success;
    this.uid = uid;
  }

  public String getNickname() {
    return nickname;
  }

  public boolean isSuccess() {
    return success;
  }

  public String getUid() {
    return uid;
  }
}
package use_case.custom_register;

/**
 * Output Data for the Register Use Case.
 */
public class RegisterOutputData {

  private final String nickname;
  private final boolean success;
  private final String uid;

  public RegisterOutputData(String nickname, boolean success, String uid) {
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
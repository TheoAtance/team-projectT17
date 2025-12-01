package use_case;

/**
 * Data object returned from Google authentication containing user profile information.
 */
public class GoogleAuthResult {

  private final String uid;
  private final String email;
  private final String displayName;

  public GoogleAuthResult(String uid, String email, String displayName) {
    this.uid = uid;
    this.email = email;
    this.displayName = displayName;
  }

  public String getUid() {
    return uid;
  }

  public String getEmail() {
    return email;
  }

  public String getDisplayName() {
    return displayName;
  }
}
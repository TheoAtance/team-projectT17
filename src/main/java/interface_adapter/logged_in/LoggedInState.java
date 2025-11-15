package interface_adapter.logged_in;

/**
 * The state for the Logged In View.
 * Represents the user session after successful authentication.
 */
public class LoggedInState {
    private String uid = "";
    private String nickname = "";
    private String email = "";

    public LoggedInState(LoggedInState copy) {
        this.uid = copy.uid;
        this.nickname = copy.nickname;
        this.email = copy.email;
    }

    public LoggedInState() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
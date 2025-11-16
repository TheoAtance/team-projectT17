package use_case.google_login;

public class GoogleLoginOutputData {

    private final String nickname;
    private final boolean success;
    private final String uid;

    public GoogleLoginOutputData(String nickname, boolean success, String uid) {
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

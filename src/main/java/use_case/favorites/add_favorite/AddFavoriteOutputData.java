package use_case.favorites.add_favorite;

public class AddFavoriteOutputData {
    private final String message;
    private final boolean success;

    public AddFavoriteOutputData(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }
}
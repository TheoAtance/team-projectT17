package interface_adapter.display_reviews;

import java.util.List;

public class DisplayReviewsStateList {
    List<DisplayReviewsState> displayReviewsStateList;
    private String errorMessage;

    public DisplayReviewsStateList() {
    }

    public List<DisplayReviewsState> getDisplayReviewsStateList() {
        return displayReviewsStateList;
    }

    public void setDisplayReviewsStateList(List<DisplayReviewsState> displayReviewsStateList) {
        this.displayReviewsStateList = displayReviewsStateList;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}

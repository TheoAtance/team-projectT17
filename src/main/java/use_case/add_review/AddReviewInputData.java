package use_case.add_review;

public class AddReviewInputData {
    private final String restaurantId;
    private final String content;

    public AddReviewInputData(String restaurantId, String content) {
        this.restaurantId = restaurantId;
        this.content = content;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public String getContent() {
        return content;
    }
}

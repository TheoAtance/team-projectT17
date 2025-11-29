package use_case.display_reviews;

public class DisplayReviewsInputData {
    private String restaurantId;

    public DisplayReviewsInputData(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }
}

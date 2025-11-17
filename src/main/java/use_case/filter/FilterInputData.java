package use_case.filter;

/**
 * Input data for the filter use case.
 */
public class FilterInputData {
    private final String restaurantType;

    public FilterInputData(String restaurantType) {
        this.restaurantType = restaurantType;
    }

    public String getRestaurantType() {
        return restaurantType;
    }
}
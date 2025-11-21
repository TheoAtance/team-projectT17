package use_case.list_search;

import entity.Restaurant;
import use_case.filter.IRestaurantDataAccess;

import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;

public class ListSearchInteractor implements ListSearchInputBoundary {  // <--- implement interface
    private final IRestaurantDataAccess restaurantDataAccess;
    private final ListSearchOutputBoundary outputBoundary;
    private final int maxVisibleRestaurants = 10;

    public ListSearchInteractor(IRestaurantDataAccess restaurantDataAccess,
                                ListSearchOutputBoundary outputBoundary) {
        this.restaurantDataAccess = restaurantDataAccess;
        this.outputBoundary = outputBoundary;
    }

    @Override
    public void search(ListSearchInputData inputData) {  // <--- implement interface method
        String query = inputData.getQuery();  // get query string from inputData
        try {
            List<Restaurant> allRestaurants = restaurantDataAccess.getAllRestaurants();

            List<Restaurant> filtered = allRestaurants.stream()
                    .filter(r -> r.getName().toLowerCase().contains(query.toLowerCase()))
                    .sorted(Comparator.comparingDouble(this::ratingToReviewRatio).reversed())
                    .limit(maxVisibleRestaurants)
                    .collect(Collectors.toList());

            outputBoundary.presentResults(filtered);
        } catch (Exception e) {
            outputBoundary.presentError("Error searching restaurants: " + e.getMessage());
        }
    }

    @Override
    public List<Restaurant> getAllRestaurants() {
        return List.of();
    }

    private double ratingToReviewRatio(Restaurant r) {
        return r.getRating() / (r.getRatingCount() + 1);
    }
}

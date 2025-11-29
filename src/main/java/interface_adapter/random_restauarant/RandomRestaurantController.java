package interface_adapter.random_restauarant;

import use_case.random_restaurant.RandomRestaurantInputBoundary;

import java.io.IOException;

public class RandomRestaurantController {
    private final RandomRestaurantInputBoundary randomRestaurantInteractor;

    public RandomRestaurantController(RandomRestaurantInputBoundary randomRestaurantInteractor) {
        this.randomRestaurantInteractor = randomRestaurantInteractor;
    }

    public void execute() throws IOException {
        randomRestaurantInteractor.execute();
    }
}

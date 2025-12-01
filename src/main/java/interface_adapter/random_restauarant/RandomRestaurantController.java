package interface_adapter.random_restauarant;

import java.io.IOException;
import use_case.random_restaurant.RandomRestaurantInputBoundary;

public class RandomRestaurantController {

  private final RandomRestaurantInputBoundary randomRestaurantInteractor;

  public RandomRestaurantController(RandomRestaurantInputBoundary randomRestaurantInteractor) {
    this.randomRestaurantInteractor = randomRestaurantInteractor;
  }

  public void execute() throws IOException {
    randomRestaurantInteractor.execute();
  }
}

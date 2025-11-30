package use_case.random_restaurant;

import entity.Restaurant;
import java.io.IOException;
import use_case.view_restaurant.ViewRestaurantOutputBoundary;
import use_case.view_restaurant.ViewRestaurantOutputData;

public class RandomRestaurantInteractor implements RandomRestaurantInputBoundary {

  private final RandomRestaurantDataAccessInterface randomRestaurantDataAccessObject;
  private final ViewRestaurantOutputBoundary viewRestaurantPresenter;


  public RandomRestaurantInteractor(
      RandomRestaurantDataAccessInterface jsonRestaurantDataAccessObject,
      ViewRestaurantOutputBoundary viewRestaurantPresenter) {

    this.randomRestaurantDataAccessObject = jsonRestaurantDataAccessObject;
    this.viewRestaurantPresenter = viewRestaurantPresenter;
  }

  @Override
  public void execute() throws IOException {
    final Restaurant restaurant = randomRestaurantDataAccessObject.getRandom();

    final ViewRestaurantOutputData viewRestaurantOutputData =
        new ViewRestaurantOutputData.Builder()
            .name(restaurant.getName())
            .id(restaurant.getId())
            .address(restaurant.getAddress())
            .type(restaurant.getType())
            .rating(restaurant.getRating())
            .ratingCount(restaurant.getRatingCount())
            .phoneNumber(restaurant.getPhoneNumber())
            .openingHours(restaurant.getHours())
            .photoIds(restaurant.getPhotoIds())
            .build();

    System.out.println("[Interactor] calling presenter.prepareSuccessView");
    viewRestaurantPresenter.prepareSuccessView(viewRestaurantOutputData);

  }
}

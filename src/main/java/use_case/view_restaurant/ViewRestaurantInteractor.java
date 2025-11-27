package use_case.view_restaurant;

import data_access.GooglePlacesGateway;
import entity.Restaurant;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * The View Restaurant Interactor
 */
public class ViewRestaurantInteractor implements ViewRestaurantInputBoundary{

    private final ViewRestaurantDataAccessInterface restaurantDataAccessObject;
    private final ViewRestaurantOutputBoundary viewRestaurantPresenter;


    public ViewRestaurantInteractor(ViewRestaurantDataAccessInterface restaurantDataAccessObject,
                                    ViewRestaurantOutputBoundary viewRestaurantPresenter) {
        this.restaurantDataAccessObject = restaurantDataAccessObject;
        this.viewRestaurantPresenter = viewRestaurantPresenter;
    }

    @Override
    public void execute(ViewRestaurantInputData viewRestaurantInputData) throws IOException {
        System.out.println("[Interactor] execute called with id = " + viewRestaurantInputData.getRestaurantId());


        final String id = viewRestaurantInputData.getRestaurantId();

        if(!restaurantDataAccessObject.existById(id)){
            System.out.println("Restaurant does not exist");
            viewRestaurantPresenter.prepareFailView( id + ": Restaurant does not exist.");
        }

        else{

            Restaurant restaurant = restaurantDataAccessObject.get(id);

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
}

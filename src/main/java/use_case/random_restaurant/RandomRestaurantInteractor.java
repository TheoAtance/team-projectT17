package use_case.random_restaurant;

import data_access.GooglePlacesGateway;
import entity.Restaurant;
import entity.RestaurantFactory;
import data_access.JsonRestaurantDataAccessObject;
import use_case.view_restaurant.ViewRestaurantOutputBoundary;
import use_case.view_restaurant.ViewRestaurantOutputData;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class RandomRestaurantInteractor implements RandomRestaurantInputBoundary{
    private final JsonRestaurantDataAccessObject jsonRestaurantDataAccessObject;
    private final GooglePlacesGateway googlePlacesGateway;
    private final ViewRestaurantOutputBoundary viewRestaurantPresenter;


    public RandomRestaurantInteractor(JsonRestaurantDataAccessObject jsonRestaurantDataAccessObject,
                                      GooglePlacesGateway googlePlacesGateway,
                                      ViewRestaurantOutputBoundary viewRestaurantPresenter) {

        this.jsonRestaurantDataAccessObject = jsonRestaurantDataAccessObject;
        this.googlePlacesGateway = googlePlacesGateway;
        this.viewRestaurantPresenter = viewRestaurantPresenter;
    }

    @Override
    public void execute() throws IOException {
        final Restaurant restaurant = jsonRestaurantDataAccessObject.getRandom();


        ArrayList<BufferedImage> images = new ArrayList<>();
        String apiKey = System.getenv("PLACES_API_TOKEN");
        System.out.println(restaurant.getPhotoIds().size());

        BufferedImage image = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/no_image.png")));;
        if(restaurant.getPhotoIds().isEmpty()){
            images.add(image);
        }

        for(int i = 0; i < restaurant.getPhotoIds().size();i++){

            if (apiKey == null) {
                // No API key → use placeholder images
                image = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/placeholder.png")));
            }
            else {
                // API key exists → load real Google image
                image = googlePlacesGateway.fetchRestaurantImage(restaurant.getPhotoIds().get(i), apiKey);
            }

            images.add(image);
        }

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
                        .photos(images)
                        .build();

        System.out.println("[Interactor] calling presenter.prepareSuccessView");
        viewRestaurantPresenter.prepareSuccessView(viewRestaurantOutputData);

    }
}

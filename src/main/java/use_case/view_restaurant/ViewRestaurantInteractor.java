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
    private final  GooglePlacesGateway googlePlacesGateway;

    public ViewRestaurantInteractor(ViewRestaurantDataAccessInterface restaurantDataAccessObject,
                                    GooglePlacesGateway googlePlacesGateway,
                                    ViewRestaurantOutputBoundary viewRestaurantPresenter) {
        this.restaurantDataAccessObject = restaurantDataAccessObject;
        this.googlePlacesGateway = googlePlacesGateway;
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
            ArrayList<BufferedImage> images = new ArrayList<>();
            String apiKey = System.getenv("PLACES_API_TOKEN");
            Restaurant restaurant = restaurantDataAccessObject.get(id);
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
}

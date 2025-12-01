package interface_adapter.view_restaurant;

import interface_adapter.ImageDataAccessInterface;
import interface_adapter.ViewManagerModel;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import javax.imageio.ImageIO;
import use_case.view_restaurant.ViewRestaurantOutputBoundary;
import use_case.view_restaurant.ViewRestaurantOutputData;

public class ViewRestaurantPresenter implements ViewRestaurantOutputBoundary {

  private final ViewRestaurantViewModel viewRestaurantViewModel;
  private final ViewManagerModel viewManagerModel;
  private final ImageDataAccessInterface imageDataAccess;


  public ViewRestaurantPresenter(ViewManagerModel viewManagerModel,
      ViewRestaurantViewModel viewRestaurantViewModel,
      ImageDataAccessInterface imageDataAccess) {

    this.viewRestaurantViewModel = viewRestaurantViewModel;
    this.viewManagerModel = viewManagerModel;
    this.imageDataAccess = imageDataAccess;
  }

  @Override
  public void prepareSuccessView(ViewRestaurantOutputData data) throws IOException {
    // copies the current state, so that in case there are states that do not require updating,
    // we can preserve those states.
    System.out.println("[Presenter] prepareSuccessView called, name = " + data.getName());

    ViewRestaurantState newState = new ViewRestaurantState(viewRestaurantViewModel.getState());

    // Here we are adding to a new state object instead of just mutating the current state because
    // this way we can prevent errors where the UI loop prints the UI before our states are finished updating.
    // (this error results in accidentally painting partially wrong information onto screen)

    ArrayList<BufferedImage> images = new ArrayList<>();
    String apiKey = System.getenv("PLACES_API_TOKEN");
    System.out.println(data.getPhotoIds().size());

    BufferedImage image = ImageIO.read(
        Objects.requireNonNull(getClass().getResource("/images/no_image.png")));
    if (data.getPhotoIds().isEmpty()) {
      images.add(image);
    }

    for (int i = 0; i < data.getPhotoIds().size(); i++) {

      if (apiKey == null) {
        // No API key → use placeholder images
        image = ImageIO.read(
            Objects.requireNonNull(getClass().getResource("/images/placeholder.png")));
      } else {
        // API key exists → load real Google image
        image = imageDataAccess.fetchRestaurantImage(data.getPhotoIds().get(i), apiKey);
      }

      images.add(image);
    }

    newState.setName(data.getName());
    newState.setId(data.getId());
    newState.setAddress(data.getAddress());
    newState.setType(data.getType());
    newState.setRating(data.getRating());
    newState.setRatingCount(data.getRatingCount());
    newState.setPhoneNumber(data.getPhoneNumber());
    newState.setOpeningHours(data.getOpeningHours());
    newState.setPhotos(images);

    viewRestaurantViewModel.setState(newState);
    viewRestaurantViewModel.firePropertyChange("restaurant info");
  }

  @Override
  public void prepareFailView(String errorMessage) {
    viewRestaurantViewModel.getState().setRestaurantDndError(errorMessage);
    viewRestaurantViewModel.firePropertyChange("restaurant info");
  }
}

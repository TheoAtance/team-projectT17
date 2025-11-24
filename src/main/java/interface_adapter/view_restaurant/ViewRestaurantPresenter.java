package interface_adapter.view_restaurant;

import use_case.view_restaurant.ViewRestaurantOutputBoundary;
import use_case.view_restaurant.ViewRestaurantOutputData;

import javax.swing.text.View;

public class ViewRestaurantPresenter implements ViewRestaurantOutputBoundary {

    private final ViewRestaurantViewModel viewRestaurantViewModel;
    private final ViewRestaurantOutputData viewRestaurantOutputData;

    public ViewRestaurantPresenter(ViewRestaurantViewModel viewRestaurantViewModel,
                                   ViewRestaurantOutputData viewRestaurantOutputData) {
        this.viewRestaurantViewModel = viewRestaurantViewModel;
        this.viewRestaurantOutputData = viewRestaurantOutputData;
    }

    @Override
    public void prepareSuccessView(ViewRestaurantOutputData data){
        // copies the current state, so that in case there are states that do not require updating,
        // we can preserve those states.

        ViewRestaurantState newState = new ViewRestaurantState(viewRestaurantViewModel.getState());

        // Here we are adding to a new state object instead of just mutating the current state because
        // this way we can prevent errors where the UI loop prints the UI before our states are finished updating.
        // (this error results in accidentally painting partially wrong information onto screen)

        newState.setName(data.getName());
        newState.setAddress(data.getAddress());
        newState.setType(data.getType());
        newState.setRating(data.getRating());
        newState.setRatingCount(data.getRatingCount());
        newState.setPhoneNumber(data.getPhoneNumber());
        newState.setOpeningHours(data.getOpeningHours());
        newState.setPhotoIds(data.getPhotoIds());

        viewRestaurantViewModel.setState(newState);
        viewRestaurantViewModel.firePropertyChange("restaurant info");
    }

    @Override
    public void prepareFailView(String errorMessage){
        viewRestaurantViewModel.getState().setRestaurantDndError(errorMessage);
        viewRestaurantViewModel.firePropertyChange("restaurant info");
    }
}

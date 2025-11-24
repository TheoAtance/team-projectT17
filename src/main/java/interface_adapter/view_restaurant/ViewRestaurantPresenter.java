package interface_adapter.view_restaurant;

import interface_adapter.ViewManagerModel;
import use_case.view_restaurant.ViewRestaurantOutputBoundary;
import use_case.view_restaurant.ViewRestaurantOutputData;

import javax.swing.text.View;

public class ViewRestaurantPresenter implements ViewRestaurantOutputBoundary {

    private final ViewRestaurantViewModel viewRestaurantViewModel;
    private final ViewManagerModel viewManagerModel;


    public ViewRestaurantPresenter(ViewManagerModel viewManagerModel,
                                   ViewRestaurantViewModel viewRestaurantViewModel) {

        this.viewRestaurantViewModel = viewRestaurantViewModel;
        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void prepareSuccessView(ViewRestaurantOutputData data){
        // copies the current state, so that in case there are states that do not require updating,
        // we can preserve those states.
        System.out.println("[Presenter] prepareSuccessView called, name = " + data.getName());

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
        newState.setPhotos(data.getPhotos());

        viewRestaurantViewModel.setState(newState);
        viewRestaurantViewModel.firePropertyChange("restaurant info");
    }

    @Override
    public void prepareFailView(String errorMessage){
        viewRestaurantViewModel.getState().setRestaurantDndError(errorMessage);
        viewRestaurantViewModel.firePropertyChange("restaurant info");
    }
}

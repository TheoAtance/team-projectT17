package interface_adapter.view_restaurant;

import use_case.view_restaurant.ViewRestaurantInputBoundary;
import use_case.view_restaurant.ViewRestaurantInputData;


/**
 * Controller for the view restaurant use case.
 */
public class ViewRestaurantController {
    private final ViewRestaurantInputBoundary viewRestaurantUseCaseInteractor;

    public ViewRestaurantController(ViewRestaurantInputBoundary viewRestaurantUseCaseInteractor) {
        this.viewRestaurantUseCaseInteractor = viewRestaurantUseCaseInteractor;
    }

    public void execute(String restaurantId){
        final ViewRestaurantInputData viewRestaurantInputData = new ViewRestaurantInputData(restaurantId);

        viewRestaurantUseCaseInteractor.execute(viewRestaurantInputData);
    }
}

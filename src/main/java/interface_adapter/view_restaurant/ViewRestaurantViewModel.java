package interface_adapter.view_restaurant;

import interface_adapter.ViewModel;

public class ViewRestaurantViewModel extends ViewModel<ViewRestaurantState>{

    public ViewRestaurantViewModel(){
        super("restaurant info");
        setState(new ViewRestaurantState());
    }
}

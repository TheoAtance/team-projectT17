package interface_adapter.view_restaurant;

import interface_adapter.ViewModel;

public class ViewRestaurantViewModel extends ViewModel<ViewRestaurantState>{

    public ViewRestaurantViewModel(){
        super("view restaurant");
        setState(new ViewRestaurantState());
    }
}

package use_case.view_restaurant;
import entity.Restaurant;

public interface ViewRestaurantDataAccessInterface {

    /**
     * Returns the restaurant obj with given restaurant id
     * @param id id of the restaurant to look up
     * @return the restaurant obj with given id
     */
    Restaurant get(String id);
}

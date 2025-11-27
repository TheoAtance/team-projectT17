package use_case.random_restaurant;

import entity.Restaurant;

public interface RandomRestaurantDataAccessInterface {

    /**
     * Get a random restaurant object from database
     * @return a random restaurant object from database
     */
    Restaurant getRandom();
}

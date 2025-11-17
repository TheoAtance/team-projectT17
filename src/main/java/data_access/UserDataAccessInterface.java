package data_access;

import entity.Restaurant;
import entity.User;

import java.util.List;

/**
 * Data Access Interface for User operations.
 * Defines the contract for accessing user and restaurant data.
 */
public interface UserDataAccessInterface {

    /**
     * Retrieves a user by their ID.
     * @param userId the ID of the user to retrieve
     * @return the User object, or null if not found
     */
    User getUser(String userId);

    /**
     * Saves or updates a user.
     * @param user the user to save
     */
    void saveUser(User user);

    /**
     * Retrieves a restaurant by its ID.
     * @param restaurantId the ID of the restaurant to retrieve
     * @return the Restaurant object, or null if not found
     */
    Restaurant getRestaurantById(String restaurantId);

    /**
     * Retrieves multiple restaurants by their IDs.
     * @param restaurantIds list of restaurant IDs to retrieve
     * @return list of Restaurant objects
     */
    List<Restaurant> getRestaurantsByIds(List<String> restaurantIds);
}
package interface_adapter;

import java.awt.image.BufferedImage;

public interface ImageDataAccessInterface {

    /**
     * Fetch restaurant image from source
     * @return image from source
     */
    BufferedImage fetchRestaurantImage(String photoId, String api_key);
}

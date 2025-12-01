package data_access;

import interface_adapter.ImageDataAccessInterface;

import java.awt.image.BufferedImage;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.imageio.ImageIO;

public class GooglePlacesGateway implements ImageDataAccessInterface {

    public GooglePlacesGateway() {
    }

    @Override
    public BufferedImage fetchRestaurantImage(String photoReference, String apiKey) {
        try {
            String mediaName = photoReference + "/media";
            String urlStr = "https://places.googleapis.com/v1/" + mediaName + "?maxWidthPx=800";

            System.out.println("Fetching photo from: " + urlStr);

            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-Goog-Api-Key", apiKey);

            BufferedImage image = ImageIO.read(connection.getInputStream());
            connection.disconnect();

            return image;
        } catch (Exception e) {
            System.err.println("Failed to fetch image: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
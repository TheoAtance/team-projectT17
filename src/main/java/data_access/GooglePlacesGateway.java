package data_access;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.imageio.ImageIO;

public class GooglePlacesGateway {

    public GooglePlacesGateway() {
    }

    public BufferedImage fetchRestaurantImage(String photoReference, String apiKey) {
        try {
            // photoName == value from photos[].name"
            String mediaName = photoReference + "/media";

            String urlStr = "https://places.googleapis.com/v1/"
                    + mediaName
                    + "?maxWidthPx=800"
                    + "&key=" + apiKey;

            System.out.println("Fetching photo from: " + urlStr);

            return ImageIO.read(new URL(urlStr));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
package helper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A helper that imports restaurant data from Google Place API and saves those data in a local json
 * file.
 */
public class RestaurantDataImporter {

  // * static client because clients are meant to be reused for better efficiency (as recommended by okhttp3)

  private static final OkHttpClient client = new OkHttpClient();
  private static final String request = "src/main/java/helper/places_request.json";
  private static final JSONObject requestJSON;

  static {
    try {
      requestJSON = new JSONObject(Files.readString(Path.of(request)));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  // private static int test = 0;

  public static void main(String[] arg) throws Exception {

    // get api key from env and check if it's valid

    String apiKey = System.getenv("PLACES_API_TOKEN");
    if (apiKey == null || apiKey.isEmpty()) {
      throw new IllegalStateException("Missing environment variable: PLACES_API_TOKEN");
    }

    // Map to keep track of unique places by ID (to avoid duplication across pages)
    // LinkedHashMap is used instead of HasMap because regular HashMap would change the order data in JSON file
    // which doesn't cause errors but would be a pain to read

    final Map<String, JSONObject> placeById = new LinkedHashMap<>();

    String nextPageToken = null;

    do {

      // ====================== Build request ======================

      //read from the request json file and parse that request into request body
      String jsonText = "";

      // returns json formatted string

      if (nextPageToken != null) {
        requestJSON.put("pageToken", nextPageToken);
      }

      jsonText = requestJSON.toString();

      RequestBody body = RequestBody.create(jsonText, MediaType.parse("application/json"));

      final Request request = new Request.Builder()
          .url("https://places.googleapis.com/v1/places:searchText")
          .addHeader("Content-Type", "application/json")
          .addHeader("X-Goog-Api-Key", apiKey)
          .addHeader("X-Goog-FieldMask",
              "places.name," +
                  "places.displayName," +
                  "places.primaryTypeDisplayName," +
                  "places.primaryType," +
                  "places.nationalPhoneNumber," +
                  "places.websiteUri," +
                  "places.formattedAddress," +
                  "places.location," +
                  "places.rating," +
                  "places.userRatingCount," +
                  "places.regularOpeningHours," +
                  "places.photos," +
                  "places.googleMapsLinks," +
                  "places.priceRange," +
                  "nextPageToken"
          )
          .post(body)
          .build();

      // ====================== Execute request ======================

      try {
        Response response = client.newCall(request).execute();
        String responseBodyStr = response.body().string();
        JSONObject responseBody = new JSONObject(responseBodyStr);
        nextPageToken = responseBody.optString("nextPageToken", null);

        if (!response.isSuccessful()) {

          throw new RuntimeException("API error: " + response.code() + "-" +
              (response.body() != null ? responseBodyStr : ""));

          // <<<====>>> response.body() != null ? response.body().string() : "" <<<====>>>
          // this line is a ternary operator, a short version of an if-else statement
          // it means check if response body is null,
          // if it is, run return response.body().string(),
          // else, return ""
          // it is good practice to include response body in the api error message.
        }

        JSONArray places = responseBody.getJSONArray("places");

        if (places != null) {
          for (int i = 0; i < places.length(); i++) {
            JSONObject curPlace = places.getJSONObject(i);
            String curId = curPlace.getString("name");
            //test ++;
            if (!placeById.containsKey(curId)) {
//                            curPlace.put("rating", 0);
//                            curPlace.put("userRatingCount", 0); // uncomment these when we want to implement our own rating system
              placeById.put(curId, curPlace);
            }
          }
        }
      } catch (IllegalStateException e) {
        throw new IllegalArgumentException(e);
      }
    } while (nextPageToken != null);

    JSONArray restaurants = new JSONArray(placeById.values());

    LocalDate today = LocalDate.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yy");

    String formatted = today.format(formatter);

    Path folder = Path.of("src", "main", "java", "data");
    Files.createDirectories(folder);               // create if missing

    // Version control (create new file with date without overwriting pre-existing file
    int counter = 1;
    String baseName = "restaurant";
    String extension = ".json";

    Path file = folder.resolve(baseName + "_" + formatted + extension);

    while (Files.exists(file)) {
      file = folder.resolve(baseName + "_" + formatted + "(" + counter + ")" + extension);
      counter++;
    }

    Files.writeString(file, restaurants.toString(2));

//        for(int i = 0; i < restaurants.length();i++){
//            String name = restaurants.getJSONObject(i).getJSONObject("displayName").getString("text");
//            System.out.println(name);
//        }
//        System.out.print(test);

  }
}

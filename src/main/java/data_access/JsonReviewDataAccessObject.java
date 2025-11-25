package data_access;
import entity.Review;
import java.io.*;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Path;
import org.json.JSONArray;
import org.json.JSONObject;
import use_case.add_review.AddReviewDataAccessInterface;

public class JsonReviewDataAccessObject implements AddReviewDataAccessInterface {
    private final Map<String, Review> reviewsById = new HashMap<>();

    public JsonReviewDataAccessObject(String jsonPath){
        try{
            JSONArray reviewData = new JSONArray(Files.readString(Path.of(jsonPath)));
            for(int i = 0; i < reviewData.length();i++){
                JSONObject curObj = reviewData.getJSONObject(i);
                String reviewId = curObj.getString("id");
                String userId = curObj.getString("userId");
                String restaurantId = curObj.getString("restaurantId");
                String content = curObj.getString("content");
                int likes = curObj.getInt("likes");
                Review review = new Review(reviewId, userId, restaurantId, content, likes);
                reviewsById.put(reviewId, review);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addReview(String reviewId, String userId, String restaurantId, String content) throws IOException {
        Review review = new Review(reviewId, userId, restaurantId, content);
        reviewsById.put(reviewId, review);
        save();
    }

    public void save() throws IOException {
        JSONArray reviews = new JSONArray(reviewsById.values());
        Path folder = Path.of("src", "main", "java", "data");
        Files.createDirectories(folder);
        Path file = folder.resolve("reviews.json");
        Files.writeString(file, reviews.toString(2));
    }
}

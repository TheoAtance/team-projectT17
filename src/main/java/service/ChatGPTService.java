// src/main/java/service/ChatGPTService.java
package service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import use_case.chat.ChatGPTClient;

import java.io.IOException;

/**
 * Concrete implementation of ChatGPTClient using the OpenAI Chat Completions API.
 */
public class ChatGPTService implements ChatGPTClient {

    private static final String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY");
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();

    public ChatGPTService() {
        if (OPENAI_API_KEY == null || OPENAI_API_KEY.isEmpty()) {
            throw new IllegalStateException(
                    "Environment variable OPENAI_API_KEY is not set. " +
                            "Please set it before running the app."
            );
        }
    }

    @Override
    public String getRestaurantRecommendation(String userQuery) {
        String prompt = String.format(
                "Please recommend restaurants near the University of Toronto that meet the following requirements: %s.\n" +
                        "Requirement: Return a JSON format containing an array named 'recommendations', with the array element being the restaurant recommendation text (at least one). Example:\n" +
                        "{\"recommendations\": [\"Restaurant 1: XX Sichuan Cuisine Restaurant, Address: XXX, Specialty: Spicy Hot Pot\", \"Restaurant 2: XX Hunan Cuisine Restaurant, Address: XXX, Specialty: Diced Pepper Fish Head\"]}",
                userQuery
        );

        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", "gpt-3.5-turbo");

        JsonArray messages = new JsonArray();
        JsonObject message = new JsonObject();
        message.addProperty("role", "user");
        message.addProperty("content", prompt);
        messages.add(message);
        requestBody.add("messages", messages);
        requestBody.addProperty("temperature", 0.7);

        Request request = new Request.Builder()
                .url(API_URL)
                .post(RequestBody.create(
                        MediaType.parse("application/json"),
                        gson.toJson(requestBody)
                ))
                .addHeader("Authorization", "Bearer " + OPENAI_API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return "API call failed: " + response.code() + " - " + response.message();
            }

            String responseBody = response.body() != null
                    ? response.body().string()
                    : "";
            JsonObject apiResponse = JsonParser.parseString(responseBody).getAsJsonObject();

            JsonArray choices = apiResponse.getAsJsonArray("choices");
            if (choices == null || choices.size() == 0) {
                return "No AI response was received. Please try again!";
            }

            String aiContent = choices.get(0)
                    .getAsJsonObject()
                    .get("message")
                    .getAsJsonObject()
                    .get("content")
                    .getAsString()
                    .trim();

            JsonObject recommendationJson = JsonParser.parseString(aiContent).getAsJsonObject();
            JsonArray recommendations = recommendationJson.getAsJsonArray("recommendations");

            if (recommendations == null || recommendations.size() == 0) {
                return "Here are some restaurants near the University of Toronto recommended for you:\n"
                        + aiContent;
            }

            StringBuilder result = new StringBuilder(
                    "Here are some restaurants near the University of Toronto recommended for you:\n");
            for (int i = 0; i < recommendations.size(); i++) {
                result.append(i + 1)
                        .append(". ")
                        .append(recommendations.get(i).getAsString())
                        .append("\n");
            }
            return result.toString();

        } catch (IOException e) {
            return "Network request failed: " + e.getMessage();
        } catch (Exception e) {
            return "Analysis of AI response failed: " + e.getMessage()
                    + "\nPlease try again or ask in a different way!";
        }
    }
}

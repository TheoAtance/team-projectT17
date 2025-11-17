package service;

import entity.QuerySpec;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 使用 OpenAI Chat Completions API 调 GPT。
 */
public class OpenAIGPTClient implements LLMClient {

    private static final String ENDPOINT = "https://api.openai.com/v1/chat/completions";
    private static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client = new OkHttpClient();

    @Override
    public QuerySpec extractQuerySpec(String userUtterance) throws Exception {
        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("Environment variable OPENAI_API_KEY is not set");
        }

        JSONObject body = new JSONObject()
                .put("model", "gpt-3.5-turbo")
                .put("messages", new JSONArray()
                        .put(new JSONObject()
                                .put("role", "system")
                                .put("content",
                                        "You are a restaurant assistant for UofT students. " +
                                                "Given a user message about where/what they want to eat, " +
                                                "you MUST return STRICT JSON with this schema:\n" +
                                                "{ \"must\": string[], \"should\": string[], \"avoid\": string[], " +
                                                "  \"budgetMax\": int | null, \"radiusKm\": number | null, " +
                                                "  \"openNow\": boolean | null, " +
                                                "  \"cuisine\": string[] | null, \"dietary\": string[] | null, " +
                                                "  \"locationHint\": string | null }\n" +
                                                "If you are not sure, use null or an empty array. " +
                                                "Do not add any explanation, return JSON only."))
                        .put(new JSONObject()
                                .put("role", "user")
                                .put("content", userUtterance))
                );

        Request request = new Request.Builder()
                .url(ENDPOINT)
                .addHeader("Authorization", "Bearer " + apiKey)
                .post(RequestBody.create(body.toString(), JSON))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("OpenAI API error: " + response.code() + " " + response.message());
            }

            String raw = response.body().string();
            JSONObject root = new JSONObject(raw);
            String content = root.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
                    .trim();


            JSONObject obj = new JSONObject(content);

            QuerySpec q = new QuerySpec();
            q.must = toStringList(obj.optJSONArray("must"));
            q.should = toStringList(obj.optJSONArray("should"));
            q.avoid = toStringList(obj.optJSONArray("avoid"));

            q.budgetMax = obj.isNull("budgetMax") ? null : obj.getInt("budgetMax");
            q.radiusKm = obj.isNull("radiusKm") ? null : obj.getDouble("radiusKm");
            q.openNow = obj.isNull("openNow") ? null : obj.getBoolean("openNow");

            q.cuisine = toStringList(obj.optJSONArray("cuisine"));
            q.dietary = toStringList(obj.optJSONArray("dietary"));
            q.locationHint = obj.isNull("locationHint") ? null : obj.getString("locationHint");

            return q;
        }
    }

    private List<String> toStringList(JSONArray arr) {
        List<String> out = new ArrayList<>();
        if (arr == null) {
            return out;
        }
        for (int i = 0; i < arr.length(); i++) {
            out.add(arr.optString(i));
        }
        return out;
    }
}

package service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChatGPTService {
    private static final String OPENAI_API_KEY = "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"; // 确保环境变量已配置
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();

    // 修复后的推荐方法：增加空值检查+异常处理+强制JSON格式Prompt
    public String getRestaurantRecommendation(String userQuery) {
        // 关键：强制ChatGPT返回指定结构的JSON，避免格式混乱
        String prompt = String.format(
                "Please recommend restaurants near the University of Toronto that meet the following requirements:%s。\n" +
                        "Requirement: Return a JSON format containing an array named 'recommendations', with the array element being the restaurant recommendation text (at least one). Example:\n" +
                        "{\"recommendations\": [\"Restaurant 1: XX Sichuan Cuisine Restaurant, Address: XXX, Specialty: Spicy Hot Pot\", \"Restaurant 2: XX Hunan Cuisine Restaurant, Address: XXX, Specialty: Diced Pepper Fish Head\"]}",
                userQuery
        );

        // 构建API请求体
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", "gpt-3.5-turbo");

        JsonArray messages = new JsonArray();
        JsonObject message = new JsonObject();
        message.addProperty("role", "user");
        message.addProperty("content", prompt);
        messages.add(message);
        requestBody.add("messages", messages);
        requestBody.addProperty("temperature", 0.7);

        // 构建请求
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
            // 1. 检查API响应是否成功
            if (!response.isSuccessful()) {
                return "API call failed:" + response.code() + " - " + response.message();
            }

            // 2. 解析API响应
            String responseBody = response.body().string();
            JsonObject apiResponse = JsonParser.parseString(responseBody).getAsJsonObject();

            // 3. 提取ChatGPT的回复内容（先拿到choices数组）
            JsonArray choices = apiResponse.getAsJsonArray("choices");
            if (choices == null || choices.size() == 0) {
                return "No AI response was received. Please try again!";
            }
            String aiContent = choices.get(0).getAsJsonObject().get("message").getAsJsonObject().get("content").getAsString().trim();

            // 4. 解析AI返回的推荐数组（增加空值检查）
            JsonObject recommendationJson = JsonParser.parseString(aiContent).getAsJsonObject();
            JsonArray recommendations = recommendationJson.getAsJsonArray("recommendations");

            // 空值处理：如果数组为null/空，返回友好提示
            if (recommendations == null || recommendations.size() == 0) {
                // 降级处理：直接返回AI的纯文本回复（避免完全失败）
                return "Here are some restaurants near the University of Toronto recommended for you:\n" + aiContent;
            }

            // 5. 拼接推荐结果
            StringBuilder result = new StringBuilder("Here are some restaurants near the University of Toronto recommended for you:\n");
            for (int i = 0; i < recommendations.size(); i++) {
                result.append(i + 1).append(". ").append(recommendations.get(i).getAsString()).append("\n");
            }
            return result.toString();

        } catch (IOException e) {
            return "Network request failed:" + e.getMessage();
        } catch (Exception e) {
            // 捕获所有解析异常（如JSON格式错误），避免NPE
            return "Analysis of AI response failure:" + e.getMessage() + "\nPlease try again or ask in a different way!";
        }
    }
}
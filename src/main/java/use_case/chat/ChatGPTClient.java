// src/main/java/use_case/chat/ChatGPTClient.java
package use_case.chat;

/**
 * Gateway / Service interface for talking to OpenAI.
 * The use case depends on this interface, not on a concrete implementation.
 */
public interface ChatGPTClient {

    /**
     * Ask the AI for restaurant recommendations.
     *
     * @param userQuery the user's requirements / question
     * @return formatted recommendation text
     */
    String getRestaurantRecommendation(String userQuery);
}

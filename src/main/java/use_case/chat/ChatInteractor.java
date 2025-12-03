// src/main/java/use_case/chat/ChatInteractor.java
package use_case.chat;

/**
 * The AI Chat Interactor.
 * It calls ChatGPTClient and passes the result to the presenter
 * (ChatOutputBoundary).
 */
public class ChatInteractor implements ChatInputBoundary {

    private final ChatGPTClient chatGPTClient;
    private final ChatOutputBoundary chatOutputBoundary;

    public ChatInteractor(ChatGPTClient chatGPTClient,
                          ChatOutputBoundary chatOutputBoundary) {
        this.chatGPTClient = chatGPTClient;
        this.chatOutputBoundary = chatOutputBoundary;
    }

    @Override
    public void execute(ChatInputData inputData) {
        try {
            String userQuery = inputData.getUserQuery();

            String responseText =
                    chatGPTClient.getRestaurantRecommendation(userQuery);

            ChatOutputData outputData =
                    new ChatOutputData(responseText, true, null);
            chatOutputBoundary.present(outputData);

        } catch (Exception e) {
            // Catch any unexpected runtime errors and report them
            ChatOutputData outputData =
                    new ChatOutputData("", false, e.getMessage());
            chatOutputBoundary.present(outputData);
        }
    }
}

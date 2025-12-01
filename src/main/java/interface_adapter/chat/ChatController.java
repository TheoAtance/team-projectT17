// src/main/java/interface_adapter/chat/ChatController.java
package interface_adapter.chat;

import service.ChatGPTService;
import javax.swing.*;
import java.io.IOException;

public class ChatController {
    private final ChatGPTService chatGPTService;
    private final ChatViewModel chatViewModel;

    public ChatController(ChatGPTService service, ChatViewModel viewModel) {
        this.chatGPTService = service;
        this.chatViewModel = viewModel;
    }

    public void sendQuery(String query) {
        // Update state to show "Searching..."
        ChatState state = chatViewModel.getState();
        state.setUserInput(query);
        state.setResponse("Searching for recommended restaurants...");
        chatViewModel.setState(state);
        chatViewModel.firePropertyChange();

        // Execute API call in new thread to avoid UI blocking
        new Thread(() -> {
            try {
                String response = chatGPTService.getRestaurantRecommendation(query);
                state.setResponse(response);
            } catch (Exception e) {
                state.setResponse("Failed to get recommendations: " + e.getMessage());
            }
            chatViewModel.setState(state);
            chatViewModel.firePropertyChange();
        }).start();
    }
}
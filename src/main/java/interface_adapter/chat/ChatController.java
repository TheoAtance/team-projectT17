// src/main/java/interface_adapter/chat/ChatController.java
package interface_adapter.chat;

import use_case.chat.ChatInputBoundary;
import use_case.chat.ChatInputData;

/**
 * Controller for the AI chat feature.
 * It receives UI events and calls the ChatInputBoundary.
 */
public class ChatController {

    private final ChatInputBoundary chatInputBoundary;
    private final ChatViewModel chatViewModel;

    public ChatController(ChatInputBoundary chatInputBoundary,
                          ChatViewModel chatViewModel) {
        this.chatInputBoundary = chatInputBoundary;
        this.chatViewModel = chatViewModel;
    }

    /**
     * Called when the user presses "Send" in the UI.
     */
    public void sendQuery(String query) {
        // Update state immediately to show "Searching..."
        ChatState state = chatViewModel.getState();
        state.setUserInput(query);
        state.setResponse("Searching for recommended restaurants...");
        chatViewModel.setState(state);
        chatViewModel.firePropertyChange();

        // Run the use case in a background thread so the UI does not freeze.
        new Thread(() -> {
            ChatInputData inputData = new ChatInputData(query);
            chatInputBoundary.execute(inputData);
        }).start();
    }
}

// src/main/java/interface_adapter/chat/ChatPresenter.java
package interface_adapter.chat;

import use_case.chat.ChatOutputBoundary;
import use_case.chat.ChatOutputData;

/**
 * Presenter for the AI chat use case.
 * It translates ChatOutputData into ChatState and updates the ViewModel.
 */
public class ChatPresenter implements ChatOutputBoundary {

    private final ChatViewModel chatViewModel;

    public ChatPresenter(ChatViewModel chatViewModel) {
        this.chatViewModel = chatViewModel;
    }

    @Override
    public void present(ChatOutputData outputData) {
        ChatState state = chatViewModel.getState();

        if (outputData.isSuccess()) {
            state.setResponse(outputData.getResponseText());
        } else {
            state.setResponse("Failed to get recommendations: "
                    + outputData.getErrorMessage());
        }

        chatViewModel.setState(state);
        chatViewModel.firePropertyChange();
    }
}
